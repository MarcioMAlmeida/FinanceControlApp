package com.example.financecontrolapp.data

data class LancamentoRequest(
    val descricao: String,
    val valor: Double,
    val data: String,
    val tipo: String
)