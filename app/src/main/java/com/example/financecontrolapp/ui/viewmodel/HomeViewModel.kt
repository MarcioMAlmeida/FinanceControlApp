package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financecontrolapp.data.LancamentoResponse
import com.example.financecontrolapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _lancamentos = MutableStateFlow<List<LancamentoResponse>>(emptyList())
    val lancamentos: StateFlow<List<LancamentoResponse>> = _lancamentos.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    private val _sessionExpired = MutableStateFlow(false)
    val sessionExpired: StateFlow<Boolean> = _sessionExpired.asStateFlow()

    init {
        buscarLancamentos()
    }

    private fun buscarLancamentos() {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getApiService(getApplication())
                val resultado = api.getLancamentos()
                _lancamentos.value = resultado

            } catch (e: HttpException) {
                if (e.code() == 401 || e.code() == 403) {
                    _sessionExpired.value = true
                } else {
                    _erro.value = "Erro no servidor: Código ${e.code()}"
                }
            } catch (e: Exception) {
                _erro.value = "Erro de conexão: Verifique sua internet."
            }
        }
    }
}