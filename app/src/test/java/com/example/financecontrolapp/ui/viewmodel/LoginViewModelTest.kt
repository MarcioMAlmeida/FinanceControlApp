package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import android.util.Log
import app.cash.turbine.test
import com.example.financecontrolapp.data.LoginResponse
import com.example.financecontrolapp.data.TokenManager
import com.example.financecontrolapp.network.FinanceApiService
import com.example.financecontrolapp.network.RetrofitClient
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var apiService: FinanceApiService

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        mockkObject(RetrofitClient)
        every { RetrofitClient.getApiService(any()) } returns apiService

        mockkConstructor(TokenManager::class)
        every { anyConstructed<TokenManager>().saveToken(any()) } just Runs

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        viewModel = LoginViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `fazerLogin - deve salvar token e emitir sucesso quando API retornar token`() = runTest {
        viewModel.onEmailChange("teste@dominio.com")
        viewModel.onSenhaChange("senha123")

         val respostaMock = LoginResponse(token = "meu_token_jwt_falso")
        coEvery { apiService.login(any()) } returns respostaMock

        viewModel.loginStatus.test {
            assertNull(awaitItem())

            viewModel.fazerLogin()
            advanceUntilIdle()

            assertEquals("Login realizado com sucesso!", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { anyConstructed<TokenManager>().saveToken(any()) }
    }

    @Test
    fun `fazerLogin - deve emitir falha quando API lancar excecao (ex 403 Forbidden)`() = runTest {
        viewModel.onEmailChange("teste@dominio.com")
        viewModel.onSenhaChange("senhaErrada")

        coEvery { apiService.login(any()) } throws RuntimeException("HTTP 403 Forbidden")

        viewModel.loginStatus.test {
            assertNull(awaitItem())

            viewModel.fazerLogin()
            advanceUntilIdle()

            assertEquals("Falha no login: Verifique seus dados", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 0) { anyConstructed<TokenManager>().saveToken(any()) }
    }
}