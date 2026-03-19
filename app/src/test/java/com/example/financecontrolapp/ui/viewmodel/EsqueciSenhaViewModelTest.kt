package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import app.cash.turbine.test
import com.example.financecontrolapp.data.EsqueciSenhaRequest
import com.example.financecontrolapp.network.FinanceApiService
import com.example.financecontrolapp.network.RetrofitClient
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class EsqueciSenhaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var apiService: FinanceApiService

    private lateinit var viewModel: EsqueciSenhaViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        mockkObject(RetrofitClient)
        every { RetrofitClient.getApiService(any()) } returns apiService

        viewModel = EsqueciSenhaViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `deve atualizar estado de sucesso quando API retornar 200 OK`() = runTest {
        viewModel.email.value = "teste@dominio.com"

        coEvery { apiService.recuperarSenha(any()) } returns Response.success(Unit)

        viewModel.sucesso.test {
            assertEquals(false, awaitItem())

            viewModel.enviarEmailRecuperacao()
            advanceUntilIdle()

            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) {
            apiService.recuperarSenha(EsqueciSenhaRequest("teste@dominio.com"))
        }
    }

    @Test
    fun `deve atualizar estado de erro quando API retornar falha`() = runTest {
        viewModel.email.value = "teste@dominio.com"
        val erroResponse = Response.error<Unit>(400, "".toResponseBody(null))
        coEvery { apiService.recuperarSenha(any()) } returns erroResponse


        viewModel.erro.test {
            assertNull(awaitItem())

            viewModel.enviarEmailRecuperacao()
            advanceUntilIdle()

            assertEquals("Erro ao solicitar recuperação. Tente novamente.", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deve atualizar estado de erro quando houver falha de conexao`() = runTest {
        viewModel.email.value = "teste@dominio.com"
        coEvery { apiService.recuperarSenha(any()) } throws RuntimeException("Sem internet")

        viewModel.erro.test {
            assertNull(awaitItem())

            viewModel.enviarEmailRecuperacao()
            advanceUntilIdle()

            assertEquals("Erro de conexão: Verifique sua internet.", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}