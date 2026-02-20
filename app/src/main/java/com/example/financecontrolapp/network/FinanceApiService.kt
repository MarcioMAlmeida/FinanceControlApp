package com.example.financecontrolapp.network

import com.example.financecontrolapp.data.CadastroRequest
import com.example.financecontrolapp.data.EsqueciSenhaRequest
import com.example.financecontrolapp.data.LancamentoRequest
import com.example.financecontrolapp.data.LancamentoResponse
import com.example.financecontrolapp.data.LoginRequest
import com.example.financecontrolapp.data.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.Response


interface FinanceApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("lancamentos")
    suspend fun getLancamentos(): List<LancamentoResponse>

    @POST("lancamentos")
    suspend fun criarLancamento(@Body request: LancamentoRequest): LancamentoResponse

    @DELETE("lancamentos/{id}")
    suspend fun deletarLancamento(@Path("id") id: Long): Response<Unit>

    @POST("usuarios")
    suspend fun cadastrarUsuario(@Body request: CadastroRequest): retrofit2.Response<Unit>

    @POST("usuarios/esqueci-senha")
    suspend fun recuperarSenha(@Body request: EsqueciSenhaRequest): retrofit2.Response<Unit>
}