package com.example.financecontrolapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financecontrolapp.ui.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onSessionExpired: () -> Unit
) {
    val lancamentos by viewModel.lancamentos.collectAsState()
    val erro by viewModel.erro.collectAsState()

    val sessionExpired by viewModel.sessionExpired.collectAsState()

    LaunchedEffect(sessionExpired) {
        if (sessionExpired) {
            onSessionExpired()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Meus Lançamentos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        erro?.let { mensagem ->
            Text(text = mensagem, color = MaterialTheme.colorScheme.error)
        }

        if (lancamentos.isEmpty() && erro == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum lançamento encontrado.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(lancamentos) { lancamento ->
                    LancamentoItem(lancamento)
                }
            }
        }
    }
}

@Composable
fun LancamentoItem(lancamento: com.example.financecontrolapp.data.LancamentoResponse) {

    val isDespesa = lancamento.tipo == "DESPESA"
    val corPrincipal = if (isDespesa) Color(0xFFE53935) else Color(0xFF43A047)
    val icone = if (isDespesa) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp

    val formatadorMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val valorFormatado = formatadorMoeda.format(lancamento.valor)

    val dataFormatada = try {
        val dataLocalDate = LocalDate.parse(lancamento.data)
        dataLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        lancamento.data ?: "Sem data"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(corPrincipal.copy(alpha = 0.1f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icone,
                    contentDescription = null,
                    tint = corPrincipal,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lancamento.descricao,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = dataFormatada,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Text(
                text = valorFormatado,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = corPrincipal
            )
        }
    }
}