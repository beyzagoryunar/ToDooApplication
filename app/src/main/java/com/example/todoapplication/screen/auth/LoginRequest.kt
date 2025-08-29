package com.example.todoapplication.screen.auth

data class LoginRequest(
    val username: String,
    val password: String
)
data class RegisterRequest(
    val username: String,
    val fullName: String,
    val password: String
)