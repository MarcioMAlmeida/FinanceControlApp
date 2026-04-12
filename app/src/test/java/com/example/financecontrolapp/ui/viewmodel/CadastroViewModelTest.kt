package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import app.cash.turbine.test
import com.example.financecontrolapp.data.CadastroRequest
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
class CadastroViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var apiService: FinanceApiService

    private lateinit var viewModel: CadastroViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        mockkObject(RetrofitClient)
        every { RetrofitClient.getApiService(any()) } returns apiService

        viewModel = CadastroViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `cadastrar - deve emitir sucesso quando API retornar 200 OK ou 201 Created`() = runTest {
        viewModel.nome.value = "Márcio Almeida"
        viewModel.email.value = "marcio@dominio.com"
        viewModel.senha.value = "senhaSegura123"

        coEvery { apiService.cadastrarUsuario(any()) } returns Response.success(mockk(relaxed = true))

        viewModel.sucesso.test {
            assertEquals(false, awaitItem())

            viewModel.cadastrar()
            advanceUntilIdle()

            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) {
            apiService.cadastrarUsuario(CadastroRequest("Márcio Almeida", "marcio@dominio.com", "senhaSegura123"))
        }
    }

    @Test
    fun `cadastrar - deve exibir erro generico quando a API rejeitar o cadastro (ex 400 Bad Request)`() = runTest {
        viewModel.nome.value = "Teste"
        viewModel.email.value = "email_ja_existente@dominio.com"
        viewModel.senha.value = "123"

        val errorResponse = Response.error<Unit>(400, "".toResponseBody(null))
        coEvery { apiService.cadastrarUsuario(any()) } returns errorResponse

        viewModel.erro.test {
            assertNull(awaitItem())

            viewModel.cadastrar()
            advanceUntilIdle()

            assertEquals("Erro no cadastro. Verifique os dados.", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cadastrar - deve repassar a mensagem de erro da excecao quando houver falha de rede`() = runTest {

        coEvery { apiService.cadastrarUsuario(any()) } throws RuntimeException("Timeout na rede")

        viewModel.erro.test {
            assertNull(awaitItem())

            viewModel.cadastrar()
            advanceUntilIdle()

            assertEquals("Erro de conexão: Timeout na rede", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}