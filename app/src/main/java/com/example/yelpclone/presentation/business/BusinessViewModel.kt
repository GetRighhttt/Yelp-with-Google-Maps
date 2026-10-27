package com.example.yelpclone.presentation.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yelpclone.domain.business.model.SearchLocation
import com.example.yelpclone.domain.business.repository.BusinessRepository
import com.example.yelpclone.domain.common.RepositoryResult
import com.example.yelpclone.presentation.business.model.BusinessScreenState
import com.example.yelpclone.presentation.common.model.ScreenError
import com.example.yelpclone.presentation.common.toScreenError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the search term, chosen location, editor state, and cancellation of replaced requests.
 *
 * @author Stefan Bayne
 * @since 2023-04-23
 */
@HiltViewModel
class BusinessViewModel
    @Inject
    constructor(
        private val repository: BusinessRepository,
        private val savedState: SavedStateHandle,
    ) : ViewModel() {
        private val mutableState =
            MutableStateFlow(
                BusinessScreenState(
                    query = savedState["query"] ?: "",
                    location = savedState["location"] ?: SearchLocation.DEFAULT,
                    locationDraft = savedState["locationDraft"] ?: savedState["location"] ?: SearchLocation.DEFAULT,
                    isEditingLocation = savedState["isEditingLocation"] ?: false,
                ),
            )
        val locationCharacterLimit: Int = SearchLocation.MAX_LENGTH
        val uiState = mutableState.asStateFlow()
        private var request: Job? = null

        init {
            search(uiState.value.query)
        }

        fun search(query: String) {
            request?.cancel()
            savedState["query"] = query
            val location = uiState.value.location
            mutableState.update { it.copy(query = query, isLoading = true, items = emptyList(), error = null) }
            request =
                viewModelScope.launch {
                    val result = repository.searchBusinesses(query, location)
                    currentCoroutineContext().ensureActive()
                    when (result) {
                        is RepositoryResult.Success -> {
                            mutableState.update {
                                it.copy(
                                    isLoading = false,
                                    items =
                                        result.value.map { business ->
                                            business.toUiModel()
                                        },
                                )
                            }
                        }

                        is RepositoryResult.Failure -> {
                            mutableState.update {
                                it.copy(isLoading = false, error = result.reason.toScreenError())
                            }
                        }
                    }
                }
        }

        fun openLocationEditor() {
            savedState["isEditingLocation"] = true
            updateLocationDraft(uiState.value.location)
            mutableState.update { it.copy(isEditingLocation = true, locationError = null) }
        }

        fun updateLocationDraft(value: String) {
            savedState["locationDraft"] = value
            mutableState.update {
                it.copy(
                    locationDraft = value,
                    locationError =
                        it.locationError?.takeUnless {
                            SearchLocation.isValid(value)
                        },
                )
            }
        }

        fun cancelLocationEdit() {
            savedState["isEditingLocation"] = false
            updateLocationDraft(uiState.value.location)
            mutableState.update { it.copy(isEditingLocation = false, locationError = null) }
        }

        fun confirmLocation() {
            val location = uiState.value.locationDraft.trim()
            if (!SearchLocation.isValid(location)) {
                mutableState.update { it.copy(locationError = ScreenError.INVALID_LOCATION) }
                return
            }
            savedState["location"] = location
            savedState["locationDraft"] = location
            savedState["isEditingLocation"] = false
            mutableState.update {
                it.copy(location = location, locationDraft = location, isEditingLocation = false, locationError = null)
            }
            search(uiState.value.query)
        }

        fun retry() = search(uiState.value.query)
    }
