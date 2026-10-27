package com.example.yelpclone.data.network

import com.example.yelpclone.domain.common.FailureReason
import com.example.yelpclone.domain.common.RepositoryResult
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException

/**
 * Translates transport and mapping failures while allowing coroutine cancellation to propagate.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
suspend fun <T, R> apiRequest(
    request: suspend () -> Response<T>,
    map: (T) -> R,
): RepositoryResult<R> =
    try {
        val response = request()
        val body = response.body()
        if (!response.isSuccessful) {
            RepositoryResult.Failure(FailureReason.SERVICE)
        } else if (body == null) {
            RepositoryResult.Failure(FailureReason.INVALID_DATA)
        } else {
            RepositoryResult.Success(map(body))
        }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: IOException) {
        RepositoryResult.Failure(FailureReason.NETWORK)
    } catch (_: JsonParseException) {
        RepositoryResult.Failure(FailureReason.INVALID_DATA)
    } catch (_: IllegalArgumentException) {
        RepositoryResult.Failure(FailureReason.INVALID_DATA)
    }
