package com.example.mfcmvvm.network

import com.example.mfcmvvm.model.UserResponse
import retrofit2.http.GET

interface ApiService {
    @GET("task/user.json")
    suspend fun getUsers(): UserResponse
}
