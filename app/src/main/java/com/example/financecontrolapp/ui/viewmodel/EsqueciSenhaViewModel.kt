package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financecontrolapp.data.EsqueciSenhaRequest
import com.example.financecontrolapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EsqueciSenhaViewModel(application: Application) : AndroidViewModel(application) {

    val email = MutableStateFlow("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sucesso = MutableStateFlow(false)
    val sucesso: StateFlow<Boolean> = _sucesso.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    fun enviarEmailRecuperacao() {
        viewModelScope.launch {
            _isLoading.value = true
            _erro.value = null

            try {
                val request = EsqueciSenhaRequest(email.value)
                val api = RetrofitClient.getApiService(getApplication())
                val response = api.recuperarSenha(request)

                if (response.isSuccessful) {
                    _sucesso.value = true
                } else {
                    _erro.value = "Erro ao solicitar recuperação. Tente novamente."
                }
            } catch (e: Exception) {
                _erro.value = "Erro de conexão: Verifique sua internet."
            } finally {
                _isLoading.value = false
            }
        }
    }
}