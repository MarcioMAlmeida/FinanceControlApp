package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import app.cash.turbine.test
import com.example.financecontrolapp.data.RedefinirSenhaRequest
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
class RedefinirSenhaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var apiService: FinanceApiService

    private lateinit var viewModel: RedefinirSenhaViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        mockkObject(RetrofitClient)
        every { RetrofitClient.getApiService(any()) } returns apiService

        viewModel = RedefinirSenhaViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `avancarParaSenhas - deve exibir erro se codigo estiver em branco`() = runTest {
        viewModel.codigo.value = ""

        viewModel.erro.test {
            assertNull(awaitItem())

            viewModel.avancarParaSenhas()

            assertEquals("Por favor, digite o código recebido no e-mail.", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(1, viewModel.passoAtual.value)
    }

    @Test
    fun `avancarParaSenhas - deve ir para o passo 2 se codigo for valido`() = runTest {
        viewModel.codigo.value = "123456"

        viewModel.passoAtual.test {
            assertEquals(1, awaitItem())

            viewModel.avancarParaSenhas()

            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `enviarNovaSenha - deve exibir erro se as senhas nao coincidirem`() = runTest {
        viewModel.novaSenha.value = "senha123"
        viewModel.confirmarSenha.value = "senha456"

        viewModel.erro.test {
            assertNull(awaitItem())

            viewModel.enviarNovaSenha()

            assertEquals("As senhas não coincidem. Digite novamente.", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { apiService.redefinirSenha(any()) }
    }

    @Test
    fun `enviarNovaSenha - deve atualizar estado de sucesso quando API retornar 200 OK`() = runTest {

        viewModel.email.value = "teste@dominio.com"
        viewModel.codigo.value = "123456"
        viewModel.novaSenha.value = "novaSenha123"
        viewModel.confirmarSenha.value = "novaSenha123"

        coEvery { apiService.redefinirSenha(any()) } returns Response.success(Unit)

        viewModel.sucesso.test {
            assertEquals(false, awaitItem())

            viewModel.enviarNovaSenha()
            advanceUntilIdle()

            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) {
            apiService.redefinirSenha(RedefinirSenhaRequest("teste@dominio.com", "123456", "novaSenha123"))
        }
    }

    @Test
    fun `enviarNovaSenha - deve retornar erro e voltar ao passo 1 quando API rejeitar o codigo`() = runTest {
        viewModel.email.value = "teste@dominio.com"
        viewModel.codigo.value = "CODIGO_ERRADO"
        viewModel.novaSenha.value = "novaSenha123"
        viewModel.confirmarSenha.value = "novaSenha123"

        viewModel.avancarParaSenhas()

        val erroResponse = Response.error<Unit>(400, "".toResponseBody(null))
        coEvery { apiService.redefinirSenha(any()) } returns erroResponse

        viewModel.enviarNovaSenha()
        advanceUntilIdle()

        assertEquals("Código inválido ou expirado. Tente novamente.", viewModel.erro.value)

        assertEquals(1, viewModel.passoAtual.value)
    }
}