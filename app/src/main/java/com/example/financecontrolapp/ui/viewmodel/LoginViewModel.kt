package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financecontrolapp.data.LoginRequest
import com.example.financecontrolapp.data.TokenManager
import com.example.financecontrolapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _senha = MutableStateFlow("")
    val senha: StateFlow<String> = _senha.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginStatus = MutableStateFlow<String?>(null)
    val loginStatus: StateFlow<String?> = _loginStatus.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onSenhaChange(newSenha: String) {
        _senha.value = newSenha
    }

    fun fazerLogin() {
        viewModelScope.launch {
            _isLoading.value = true
            _loginStatus.value = null

            try {
                val request = LoginRequest(email.value, senha.value)
                val response = RetrofitClient.apiService.login(request)

                Log.d("LoginViewModel", "Sucesso! Token: ${response.token}")
                _loginStatus.value = "Login realizado com sucesso!"

                tokenManager.saveToken(response.token)

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Erro: ${e.message}")
                _loginStatus.value = "Falha no login: Verifique seus dados"
            } finally {
                _isLoading.value = false
            }
        }
    }
}