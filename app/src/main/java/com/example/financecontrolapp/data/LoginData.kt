package com.example.financecontrolapp.data

data class LoginRequest(
    val email: String,
    val senha: String
)

data class LoginResponse(
    val token: String
)