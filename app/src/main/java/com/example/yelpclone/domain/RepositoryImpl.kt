package com.example.yelpclone.domain

import com.example.yelpclone.data.api.ApiService
import com.example.yelpclone.data.model.yelp.YelpSearchResult
import com.example.yelpclone.core.events.Resource
import com.example.yelpclone.data.db.BusinessDAO
import com.example.yelpclone.data.model.yelp.YelpBusinesses
import com.example.yelpclone.domain.sot.YelpRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/*
Implementing methods outlined in our repository. Serves as layer between api and views.
 */
@Singleton
class RepositoryImpl @Inject constructor (
    private val apiService: ApiService,
    private val businessDAO: BusinessDAO
) : YelpRepository {

    override suspend fun searchBusinesses(
        authHeader: String,
        searchTerm: String,
        location: String,
        limit: Int,
        offset: Int
    ): Resource<YelpSearchResult> {
        return try {
            val response = apiService.searchBusinesses(
                authHeader,
                searchTerm,
                location,
                limit,
                offset
            )
            val result = response.body()
            if ((response.isSuccessful) && (result != null)) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to retrieve businesses.")
        }
    }

    override suspend fun insertBusiness(business: YelpBusinesses) = withContext(Dispatchers.IO) {
        businessDAO.insertABusiness(business)
    }
}

