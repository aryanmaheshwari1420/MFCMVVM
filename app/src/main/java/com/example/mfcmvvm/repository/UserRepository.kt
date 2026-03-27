package com.example.mfcmvvm.repository

import android.content.Context
import com.example.mfcmvvm.db.AppDatabase
import com.example.mfcmvvm.db.UserEntity
import com.example.mfcmvvm.model.User
import com.example.mfcmvvm.network.RetrofitClient
import com.example.mfcmvvm.utils.NetworkUtils

class UserRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService
    private val userDao = AppDatabase.getInstance(context).userDao()

    suspend fun getUsers(): Result<List<User>> {
        return if (NetworkUtils.isInternetAvailable(context)) {
            try {
                val response = apiService.getUsers()
                // Cache to Room
                val entities = response.users.map { it.toEntity() }
                userDao.deleteAll()
                userDao.insertAll(entities)
                Result.success(response.users)
            } catch (e: Exception) {
                // Network error - fall back to cache
                val cached = userDao.getAllUsers()
                if (cached.isNotEmpty()) {
                    Result.success(cached.map { it.toUser() })
                } else {
                    Result.failure(e)
                }
            }
        } else {
            // No internet - return cached data
            val cached = userDao.getAllUsers()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toUser() })
            } else {
                Result.failure(Exception("OFFLINE"))
            }
        }
    }

    suspend fun searchUsers(query: String): List<User> {
        return userDao.searchUsers(query).map { it.toUser() }
    }

    private fun User.toEntity() = UserEntity(
        id, name, username, email, mobile, age, gender, city,
        profileImage, bio, rating, followers, following,
        isOnline, isVerified, isPremium, status, joinedDate
    )

    private fun UserEntity.toUser() = User(
        id, name, username, email, mobile, age, gender, city,
        profileImage, bio, rating, followers, following,
        isOnline, isVerified, isPremium, status, joinedDate
    )
}
