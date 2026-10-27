package com.example.yelpclone.domain.business.repository

import com.example.yelpclone.domain.business.model.Business
import com.example.yelpclone.domain.common.RepositoryResult

/**
 * Defines the data access boundary used by the feature ViewModel.
 *
 * @author Stefan Bayne
 * @since 2023-04-23
 */
interface BusinessRepository {
    suspend fun searchBusinesses(
        term: String,
        location: String,
    ): RepositoryResult<List<Business>>
}
