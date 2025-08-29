package com.example.todoapplication.data.model

data class LoginResponse(
    var accessToken: String? = null,
    var refreshToken: String? = null,
    var userId: Int? = null,
    var userName: String? = null,
    var fullName: String? = null
)

data class RegisterResponse(
    val token: String,
    val user: User
)
