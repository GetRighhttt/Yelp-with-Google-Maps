package com.example.yelpclone.data

import com.example.yelpclone.data.business.model.BusinessDto
import com.example.yelpclone.data.business.model.BusinessResponseDto
import com.example.yelpclone.data.business.repository.DefaultBusinessRepository
import com.example.yelpclone.data.business.source.BusinessApi
import com.example.yelpclone.domain.common.FailureReason
import com.example.yelpclone.domain.common.RepositoryResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Checks repository validation, response shape, failure translation, and cancellation.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
class RepositoryTest {
    @Test fun `missing Yelp credentials fail before contacting service`() =
        runTest {
            val api = businessApi { error("Must not call the service") }
            assertEquals(
                RepositoryResult.Failure(FailureReason.MISSING_CREDENTIALS),
                DefaultBusinessRepository(api, "").searchBusinesses("coffee", "Tampa, FL"),
            )
        }

    @Test fun `network failures become domain failures`() =
        runTest {
            val api = businessApi { throw IOException("offline") }
            assertEquals(
                RepositoryResult.Failure(FailureReason.NETWORK),
                DefaultBusinessRepository(api, "key").searchBusinesses("coffee", "Tampa, FL"),
            )
        }

    @Test fun `cancellation propagates out of repository`() =
        runTest {
            val api = businessApi { throw CancellationException("cancelled") }
            try {
                DefaultBusinessRepository(api, "key").searchBusinesses("coffee", "Tampa, FL")
                fail("Cancellation must propagate")
            } catch (_: CancellationException) {
            }
        }

    @Test fun `missing business identity is invalid data`() =
        runTest {
            val api = businessApi { Response.success(BusinessResponseDto(listOf(BusinessDto(name = "Cafe")))) }
            assertEquals(
                RepositoryResult.Failure(FailureReason.INVALID_DATA),
                DefaultBusinessRepository(api, "key").searchBusinesses("coffee", "Tampa, FL"),
            )
        }

    @Test fun `empty results are successful`() =
        runTest {
            val api = businessApi { Response.success(BusinessResponseDto(emptyList())) }
            assertEquals(
                RepositoryResult.Success(emptyList<Any>()),
                DefaultBusinessRepository(api, "key").searchBusinesses("coffee", "Tampa, FL"),
            )
        }

    @Test fun `invalid location never contacts the service`() =
        runTest {
            val api = businessApi { error("Must validate before contacting Yelp") }
            for (location in listOf("", "   ", "x".repeat(251))) {
                assertEquals(
                    RepositoryResult.Failure(FailureReason.INVALID_LOCATION),
                    DefaultBusinessRepository(api, "key").searchBusinesses("coffee", location),
                )
            }
        }

    @Test fun `location and business term are encoded as separate query parameters`() =
        runTest {
            var requestedLocation: String? = null
            var requestedTerm: String? = null
            val client =
                OkHttpClient
                    .Builder()
                    .addInterceptor { chain ->
                        requestedLocation = chain.request().url.queryParameter("location")
                        requestedTerm = chain.request().url.queryParameter("term")
                        okhttp3.Response
                            .Builder()
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .code(200)
                            .message("OK")
                            .body("""{"businesses":[]}""".toResponseBody("application/json".toMediaType()))
                            .build()
                    }.build()
            val api =
                Retrofit
                    .Builder()
                    .baseUrl("https://api.yelp.com/v3/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(BusinessApi::class.java)
            val result = DefaultBusinessRepository(api, "key").searchBusinesses("coffee & tea", " São Paulo, Brazil ")
            assertEquals(RepositoryResult.Success(emptyList<Any>()), result)
            assertEquals("São Paulo, Brazil", requestedLocation)
            assertEquals("coffee & tea", requestedTerm)
        }

    private fun businessApi(block: suspend () -> Response<BusinessResponseDto>): BusinessApi =
        object : BusinessApi {
            override suspend fun searchBusinesses(
                authorization: String,
                term: String,
                location: String,
                limit: Int,
            ) = block()
        }
}
