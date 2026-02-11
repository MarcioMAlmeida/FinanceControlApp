package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financecontrolapp.data.LancamentoRequest
import com.example.financecontrolapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class NovoLancamentoViewModel(application: Application) : AndroidViewModel(application) {

    val descricao = MutableStateFlow("")
    val valor = MutableStateFlow("")

    val data = MutableStateFlow(LocalDate.now().toString())

    val tipo = MutableStateFlow("DESPESA")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sucesso = MutableStateFlow(false)
    val sucesso: StateFlow<Boolean> = _sucesso.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    fun salvarLancamento() {
        viewModelScope.launch {
            _isLoading.value = true
            _erro.value = null

            try {
                val valorDouble = valor.value.replace(",", ".").toDoubleOrNull() ?: 0.0

                val request = LancamentoRequest(
                    descricao = descricao.value,
                    valor = valorDouble,
                    data = data.value,
                    tipo = tipo.value
                )

                val api = RetrofitClient.getApiService(getApplication())
                api.criarLancamento(request)

                _sucesso.value = true

            } catch (e: Exception) {
                _erro.value = "Erro ao salvar: Verifique os dados."
            } finally {
                _isLoading.value = false
            }
        }
    }
}