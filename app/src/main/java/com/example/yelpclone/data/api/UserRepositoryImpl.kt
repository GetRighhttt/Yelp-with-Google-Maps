package com.example.yelpclone.data.api

import com.example.yelpclone.core.events.Resource
import com.example.yelpclone.data.api.UserApiService
import com.example.yelpclone.domain.model.users.UserList
import com.example.yelpclone.domain.sot.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService
) : UserRepository {
    override suspend fun getUsers(size: Int): Resource<List<UserList>> {
        return try {
            withContext(Dispatchers.IO) {
                val response = apiService.getUsers(size)
                val result = response.body()
                if ((response.isSuccessful) && (result != null)) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message())
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to retrieve users.")
        }
    }
}