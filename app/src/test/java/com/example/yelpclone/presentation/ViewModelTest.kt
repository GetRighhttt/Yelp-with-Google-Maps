package com.example.yelpclone.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.yelpclone.domain.business.model.Business
import com.example.yelpclone.domain.business.repository.BusinessRepository
import com.example.yelpclone.domain.common.FailureReason
import com.example.yelpclone.domain.common.RepositoryResult
import com.example.yelpclone.presentation.business.BusinessViewModel
import com.example.yelpclone.presentation.common.model.ScreenError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Checks ViewModel request ownership, saved input, retry state, and request cancellation.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setMain() {
        Dispatchers.setMain(dispatcher)
    }

    @After fun resetMain() {
        Dispatchers.resetMain()
    }

    @Test fun `new search cancels older results and publishes only the latest`() =
        runTest(dispatcher) {
            val repository =
                object : BusinessRepository {
                    override suspend fun searchBusinesses(
                        term: String,
                        location: String,
                    ): RepositoryResult<List<Business>> {
                        delay(if (term == "old") 1000 else 10)
                        return RepositoryResult.Success(listOf(business(term)))
                    }
                }
            val vm = BusinessViewModel(repository, SavedStateHandle())
            vm.search("old")
            runCurrent()
            vm.search("new")
            assertTrue(vm.uiState.value.isLoading)
            advanceUntilIdle()
            assertEquals(
                "new",
                vm.uiState.value.items
                    .single()
                    .name,
            )
            assertFalse(vm.uiState.value.isLoading)
        }

    @Test
    fun `cancelled requests cannot publish even when repository ignores cancellation`() =
        runTest(dispatcher) {
            val repository =
                object : BusinessRepository {
                    override suspend fun searchBusinesses(
                        term: String,
                        location: String,
                    ): RepositoryResult<List<Business>> =
                        withContext(NonCancellable) {
                            delay(if (term == "old") 1000 else 10)
                            RepositoryResult.Success(listOf(business(term)))
                        }
                }
            val vm = BusinessViewModel(repository, SavedStateHandle())
            vm.search("old")
            runCurrent()
            vm.search("new")
            advanceUntilIdle()
            assertEquals(
                "new",
                vm.uiState.value.items
                    .single()
                    .name,
            )
        }

    @Test fun `restored query is used for the initial request`() =
        runTest(dispatcher) {
            var requested = ""
            val repository =
                object : BusinessRepository {
                    override suspend fun searchBusinesses(
                        term: String,
                        location: String,
                    ): RepositoryResult<List<Business>> {
                        requested = term
                        return RepositoryResult.Success(emptyList())
                    }
                }
            val vm = BusinessViewModel(repository, SavedStateHandle(mapOf("query" to "coffee")))
            advanceUntilIdle()
            assertEquals("coffee", requested)
            assertTrue(
                vm.uiState.value.items
                    .isEmpty(),
            )
            assertNull(vm.uiState.value.error)
        }

    @Test fun `retry clears a persistent failure after success`() =
        runTest(dispatcher) {
            var fail = true
            val repository =
                object : BusinessRepository {
                    override suspend fun searchBusinesses(
                        term: String,
                        location: String,
                    ): RepositoryResult<List<Business>> =
                        if (fail) {
                            RepositoryResult.Failure(
                                FailureReason.NETWORK,
                            )
                        } else {
                            RepositoryResult.Success(emptyList())
                        }
                }
            val vm = BusinessViewModel(repository, SavedStateHandle())
            advanceUntilIdle()
            assertEquals(ScreenError.NETWORK, vm.uiState.value.error)
            fail = false
            vm.retry()
            advanceUntilIdle()
            assertNull(vm.uiState.value.error)
            assertFalse(vm.uiState.value.isLoading)
        }

    @Test fun `changing city cancels old results and keeps the current business term`() =
        runTest(dispatcher) {
            val requests = mutableListOf<Pair<String, String>>()
            val repository =
                object : BusinessRepository {
                    override suspend fun searchBusinesses(
                        term: String,
                        location: String,
                    ): RepositoryResult<List<Business>> {
                        requests += term to location
                        delay(if (location == "Tampa, FL") 1000 else 10)
                        return RepositoryResult.Success(listOf(business(location)))
                    }
                }
            val vm = BusinessViewModel(repository, SavedStateHandle())
            vm.search("coffee")
            runCurrent()
            vm.openLocationEditor()
            vm.updateLocationDraft("Chicago, IL")
            vm.confirmLocation()
            advanceUntilIdle()
            assertEquals("coffee" to "Chicago, IL", requests.last())
            assertEquals(
                "Chicago, IL",
                vm.uiState.value.items
                    .single()
                    .name,
            )
            vm.retry()
            advanceUntilIdle()
            assertEquals("coffee" to "Chicago, IL", requests.last())
        }

    @Test fun `invalid location leaves existing results and location available`() =
        runTest(dispatcher) {
            var requests = 0
            val repository =
                object : BusinessRepository {
                    override suspend fun searchBusinesses(
                        term: String,
                        location: String,
                    ): RepositoryResult<List<Business>> {
                        requests++
                        return RepositoryResult.Success(listOf(business("Cafe")))
                    }
                }
            val vm = BusinessViewModel(repository, SavedStateHandle())
            advanceUntilIdle()
            vm.openLocationEditor()
            vm.updateLocationDraft("   ")
            vm.confirmLocation()
            assertEquals(1, requests)
            assertEquals("Tampa, FL", vm.uiState.value.location)
            assertEquals(
                "Cafe",
                vm.uiState.value.items
                    .single()
                    .name,
            )
            assertTrue(vm.uiState.value.isEditingLocation)
            assertEquals(ScreenError.INVALID_LOCATION, vm.uiState.value.locationError)
            vm.updateLocationDraft("x".repeat(251))
            vm.confirmLocation()
            assertEquals(1, requests)
            vm.cancelLocationEdit()
            assertFalse(vm.uiState.value.isEditingLocation)
            assertNull(vm.uiState.value.locationError)
        }

    @Test fun `saved location and unfinished editor are restored together`() =
        runTest(dispatcher) {
            var requestedLocation = ""
            val repository =
                object : BusinessRepository {
                    override suspend fun searchBusinesses(
                        term: String,
                        location: String,
                    ): RepositoryResult<List<Business>> {
                        requestedLocation = location
                        return RepositoryResult.Success(emptyList())
                    }
                }
            val vm =
                BusinessViewModel(
                    repository,
                    SavedStateHandle(
                        mapOf(
                            "query" to "pizza",
                            "location" to "Chicago, IL",
                            "locationDraft" to "60601",
                            "isEditingLocation" to true,
                        ),
                    ),
                )
            advanceUntilIdle()
            assertEquals("Chicago, IL", requestedLocation)
            assertEquals("60601", vm.uiState.value.locationDraft)
            assertTrue(vm.uiState.value.isEditingLocation)
            vm.cancelLocationEdit()
            assertEquals("Chicago, IL", vm.uiState.value.locationDraft)
            assertEquals("pizza", vm.uiState.value.query)
        }

    private fun business(name: String) = Business(name, name, "", 4.0, 10, "", "", "", null, null, null)
}
