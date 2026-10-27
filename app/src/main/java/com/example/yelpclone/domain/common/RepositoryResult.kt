package com.example.yelpclone.domain.common

/**
 * Describes the outcome of data access without exposing transport exceptions.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
sealed interface RepositoryResult<out T> {
    data class Success<T>(
        val value: T,
    ) : RepositoryResult<T>

    data class Failure(
        val reason: FailureReason,
    ) : RepositoryResult<Nothing>
}
