package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financecontrolapp.data.RedefinirSenhaRequest
import com.example.financecontrolapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RedefinirSenhaViewModel(application: Application) : AndroidViewModel(application) {

    val email = MutableStateFlow("")
    val codigo = MutableStateFlow("")
    val novaSenha = MutableStateFlow("")
    val confirmarSenha = MutableStateFlow("")

    private val _passoAtual = MutableStateFlow(1)
    val passoAtual: StateFlow<Int> = _passoAtual.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sucesso = MutableStateFlow(false)
    val sucesso: StateFlow<Boolean> = _sucesso.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    fun avancarParaSenhas() {
        if (codigo.value.isBlank()) {
            _erro.value = "Por favor, digite o código recebido no e-mail."
            return
        }
        _erro.value = null
        _passoAtual.value = 2
    }

    fun voltarParaCodigo() {
        _erro.value = null
        _passoAtual.value = 1
    }

    fun enviarNovaSenha() {
        if (novaSenha.value.isBlank() || confirmarSenha.value.isBlank()) {
            _erro.value = "Preencha a nova senha e a confirmação."
            return
        }

        if (novaSenha.value != confirmarSenha.value) {
            _erro.value = "As senhas não coincidem. Digite novamente."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _erro.value = null

            try {
                val request = RedefinirSenhaRequest(email.value, codigo.value, novaSenha.value)
                val api = RetrofitClient.getApiService(getApplication())
                val response = api.redefinirSenha(request)

                if (response.isSuccessful) {
                    _sucesso.value = true
                } else {
                    _erro.value = "Código inválido ou expirado. Tente novamente."
                    _passoAtual.value = 1
                }
            } catch (e: Exception) {
                _erro.value = "Erro de conexão: Verifique sua internet."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limparErro() {
        _erro.value = null
    }
}