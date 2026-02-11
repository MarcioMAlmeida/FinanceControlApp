package com.example.financecontrolapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financecontrolapp.ui.viewmodel.NovoLancamentoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovoLancamentoScreen(
    viewModel: NovoLancamentoViewModel = viewModel(),
    onVoltar: () -> Unit
) {
    val descricao by viewModel.descricao.collectAsState()
    val valor by viewModel.valor.collectAsState()
    val data by viewModel.data.collectAsState()
    val tipo by viewModel.tipo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sucesso by viewModel.sucesso.collectAsState()
    val erro by viewModel.erro.collectAsState()

    LaunchedEffect(sucesso) {
        if (sucesso) {
            onVoltar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Lançamento") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = tipo == "RECEITA",
                    onClick = { viewModel.tipo.value = "RECEITA" },
                    label = { Text("Receita") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF43A047).copy(alpha = 0.2f)),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = tipo == "DESPESA",
                    onClick = { viewModel.tipo.value = "DESPESA" },
                    label = { Text("Despesa") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFE53935).copy(alpha = 0.2f)),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = descricao,
                onValueChange = { viewModel.descricao.value = it },
                label = { Text("Descrição (ex: Almoço)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = valor,
                onValueChange = { viewModel.valor.value = it },
                label = { Text("Valor (ex: 25.50)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data,
                onValueChange = { viewModel.data.value = it },
                label = { Text("Data (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            erro?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.salvarLancamento() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && descricao.isNotBlank() && valor.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Salvar")
                }
            }
        }
    }
}