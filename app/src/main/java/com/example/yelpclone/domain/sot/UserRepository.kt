package com.example.yelpclone.domain.sot

import com.example.yelpclone.core.events.Resource
import com.example.yelpclone.domain.model.users.UserList

interface UserRepository {
    suspend fun getUsers(size: Int): Resource<List<UserList>>
}