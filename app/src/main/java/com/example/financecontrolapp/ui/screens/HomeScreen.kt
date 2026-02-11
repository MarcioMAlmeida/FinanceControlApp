package com.example.financecontrolapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financecontrolapp.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {

    val lancamentos by viewModel.lancamentos.collectAsState()
    val erro by viewModel.erro.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Meus Lançamentos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        erro?.let { mensagem ->
            Text(text = mensagem, color = MaterialTheme.colorScheme.error)
        }

        if (lancamentos.isEmpty() && erro == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum lançamento encontrado ou carregando...")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lancamentos) { lancamento ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = lancamento.descricao, style = MaterialTheme.typography.titleMedium)
                            Text(text = "R$ ${lancamento.valor}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}