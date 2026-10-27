package com.example.yelpclone.data.business.repository

import com.example.yelpclone.data.business.mapper.toDomain
import com.example.yelpclone.data.business.source.BusinessApi
import com.example.yelpclone.data.network.apiRequest
import com.example.yelpclone.domain.business.model.Business
import com.example.yelpclone.domain.business.model.SearchLocation
import com.example.yelpclone.domain.business.repository.BusinessRepository
import com.example.yelpclone.domain.common.FailureReason
import com.example.yelpclone.domain.common.RepositoryResult
import javax.inject.Inject
import javax.inject.Named

/**
 * Owns Yelp authentication and maps business responses before returning domain results.
 *
 * @author Stefan Bayne
 * @since 2023-04-23
 */
class DefaultBusinessRepository
    @Inject
    constructor(
        private val api: BusinessApi,
        @param:Named("yelpKey") private val apiKey: String,
    ) : BusinessRepository {
        override suspend fun searchBusinesses(
            term: String,
            location: String,
        ): RepositoryResult<List<Business>> {
            if (!SearchLocation.isValid(location)) return RepositoryResult.Failure(FailureReason.INVALID_LOCATION)
            if (apiKey.isBlank()) return RepositoryResult.Failure(FailureReason.MISSING_CREDENTIALS)
            return apiRequest({ api.searchBusinesses("Bearer $apiKey", term, location.trim()) }) { response ->
                requireNotNull(response.businesses).map { it.toDomain() }
            }
        }
    }
