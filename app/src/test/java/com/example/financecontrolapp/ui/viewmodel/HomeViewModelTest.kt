package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import app.cash.turbine.test
import com.example.financecontrolapp.data.LancamentoResponse
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
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var apiService: FinanceApiService

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        mockkObject(RetrofitClient)
        every { RetrofitClient.getApiService(any()) } returns apiService
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `init - deve carregar lancamentos com sucesso ao iniciar o ViewModel`() = runTest {

        val lancamentoMock = mockk<LancamentoResponse>(relaxed = true)
        coEvery { apiService.getLancamentos() } returns listOf(lancamentoMock)

        viewModel = HomeViewModel(application)
        advanceUntilIdle()

        viewModel.lancamentos.test {
            val lista = awaitItem()
            assertEquals(1, lista.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init - deve expirar sessao quando API retornar erro 401 ou 403`() = runTest {

        val errorResponse = Response.error<List<LancamentoResponse>>(403, "".toResponseBody(null))
        val httpException = HttpException(errorResponse)
        coEvery { apiService.getLancamentos() } throws httpException

        viewModel = HomeViewModel(application)
        advanceUntilIdle()

        viewModel.sessionExpired.test {
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init - deve exibir erro generico quando houver falha de conexao`() = runTest {

        coEvery { apiService.getLancamentos() } throws RuntimeException("Sem internet")


        viewModel = HomeViewModel(application)
        advanceUntilIdle()

        viewModel.erro.test {
            assertEquals("Erro de conexão: Verifique sua internet.", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deletarLancamento - deve remover item da lista quando API retornar sucesso`() = runTest {

        val lancamentoMock = mockk<LancamentoResponse>(relaxed = true) {
            every { id } returns 10L
        }
        coEvery { apiService.getLancamentos() } returns listOf(lancamentoMock)

        viewModel = HomeViewModel(application)
        advanceUntilIdle()

        coEvery { apiService.deletarLancamento(10L) } returns Response.success(Unit)

        viewModel.deletarLancamento(10L)
        advanceUntilIdle()

        viewModel.lancamentos.test {
            val listaAtualizada = awaitItem()
            assertTrue(listaAtualizada.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deletarLancamento - deve exibir erro quando API rejeitar a delecao`() = runTest {

        coEvery { apiService.getLancamentos() } returns emptyList()
        viewModel = HomeViewModel(application)
        advanceUntilIdle()

        val errorResponse = Response.error<Unit>(404, "".toResponseBody(null))
        coEvery { apiService.deletarLancamento(99L) } returns errorResponse

        viewModel.deletarLancamento(99L)
        advanceUntilIdle()

        viewModel.erro.test {
            assertEquals("Erro ao apagar: Código 404", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}