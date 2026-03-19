package com.example.financecontrolapp.data

data class RedefinirSenhaRequest(
    val email: String,
    val codigo: String,
    val novaSenha: String
)