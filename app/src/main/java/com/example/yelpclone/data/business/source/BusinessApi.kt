package com.example.yelpclone.data.business.source

import com.example.yelpclone.data.business.model.BusinessResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Declares the feature HTTP operations and their source-owned response records.
 *
 * @author Stefan Bayne
 * @since 2023-04-23
 */
interface BusinessApi {
    @GET("businesses/search")
    suspend fun searchBusinesses(
        @Header("Authorization") authorization: String,
        @Query("term") term: String,
        @Query("location") location: String,
        @Query("limit") limit: Int = 50,
    ): Response<BusinessResponseDto>
}
