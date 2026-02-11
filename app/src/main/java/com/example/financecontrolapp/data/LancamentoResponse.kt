package com.example.financecontrolapp.data

data class LancamentoResponse(
    val id: Long,
    val descricao: String,
    val valor: Double,
    val data: String?,
    val tipo: String
)