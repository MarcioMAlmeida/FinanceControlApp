package com.example.financecontrolapp.network

import com.example.financecontrolapp.data.LoginRequest
import com.example.financecontrolapp.data.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST


interface FinanceApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}