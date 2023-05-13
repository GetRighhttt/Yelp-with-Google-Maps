package com.example.yelpclone.data.api

import com.example.yelpclone.data.model.users.UserList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {

    @GET("users")
    suspend fun getUsers(
        @Query("size") size: Int
    ): Response<List<UserList>>
}