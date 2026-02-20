package com.example.financecontrolapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financecontrolapp.ui.viewmodel.CadastroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroScreen(
    viewModel: CadastroViewModel = viewModel(),
    onVoltar: () -> Unit,
    onCadastroSucesso: () -> Unit
) {
    val nome by viewModel.nome.collectAsState()
    val email by viewModel.email.collectAsState()
    val senha by viewModel.senha.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sucesso by viewModel.sucesso.collectAsState()
    val erro by viewModel.erro.collectAsState()

    LaunchedEffect(sucesso) {
        if (sucesso) {
            onCadastroSucesso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Conta") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nome,
                onValueChange = { viewModel.nome.value = it },
                label = { Text("Nome completo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.email.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { viewModel.senha.value = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            erro?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { viewModel.cadastrar() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && nome.isNotBlank() && email.isNotBlank() && senha.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Cadastrar")
                }
            }
        }
    }
}