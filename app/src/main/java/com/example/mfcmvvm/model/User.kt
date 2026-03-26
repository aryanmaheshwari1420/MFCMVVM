package com.example.mfcmvvm.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("users")
    val users: List<User>
)

data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("mobile")
    val mobile: Long,

    @SerializedName("age")
    val age: Int,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("profileImage")
    val profileImage: String,

    @SerializedName("bio")
    val bio: String,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("followers")
    val followers: Int,

    @SerializedName("following")
    val following: Int,

    @SerializedName("isOnline")
    val isOnline: Boolean,

    @SerializedName("isVerified")
    val isVerified: Boolean,

    @SerializedName("isPremium")
    val isPremium: Boolean,

    @SerializedName("status")
    val status: String,

    @SerializedName("joinedDate")
    val joinedDate: String
)
