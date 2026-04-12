package com.example.financecontrolapp.ui.viewmodel

import android.app.Application
import app.cash.turbine.test
import com.example.financecontrolapp.data.LancamentoRequest
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
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class NovoLancamentoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var apiService: FinanceApiService

    private lateinit var viewModel: NovoLancamentoViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        mockkObject(RetrofitClient)
        every { RetrofitClient.getApiService(any()) } returns apiService

        viewModel = NovoLancamentoViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `salvarLancamento - deve formatar valor com virgula e emitir sucesso quando API retornar 200 OK`() = runTest {
        viewModel.descricao.value = "Compra no Mercado"
        viewModel.valor.value = "150,50"
        viewModel.data.value = "2025-10-25"
        viewModel.tipo.value = "DESPESA"

        coEvery { apiService.criarLancamento(any()) } returns mockk(relaxed = true)

        viewModel.sucesso.test {
            assertEquals(false, awaitItem())

            viewModel.salvarLancamento()
            advanceUntilIdle()

            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val requestCapturado = slot<LancamentoRequest>()
        coVerify(exactly = 1) { apiService.criarLancamento(capture(requestCapturado)) }

        assertEquals("Compra no Mercado", requestCapturado.captured.descricao)
        assertEquals(150.5, requestCapturado.captured.valor, 0.0)
    }

    @Test
    fun `salvarLancamento - deve enviar valor 0_0 se o valor digitado for invalido`() = runTest {

        viewModel.valor.value = "abcde"

        coEvery { apiService.criarLancamento(any()) } returns mockk(relaxed = true)

        viewModel.salvarLancamento()
        advanceUntilIdle()

        val requestCapturado = slot<LancamentoRequest>()
        coVerify { apiService.criarLancamento(capture(requestCapturado)) }

        assertEquals(0.0, requestCapturado.captured.valor, 0.0)
    }

    @Test
    fun `salvarLancamento - deve emitir erro quando houver falha na API`() = runTest {

        viewModel.descricao.value = "Conta de Luz"
        viewModel.valor.value = "100.0"

        coEvery { apiService.criarLancamento(any()) } throws RuntimeException("Erro 500 do Servidor")

        viewModel.erro.test {
            assertNull(awaitItem())

            viewModel.salvarLancamento()
            advanceUntilIdle()

            assertEquals("Erro ao salvar: Verifique os dados.", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}