package com.example.financecontrolapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financecontrolapp.ui.viewmodel.RedefinirSenhaViewModel

@Composable
fun RedefinirSenhaScreen(
    emailRecuperacao: String,
    onNavigateToLogin: () -> Unit,
    viewModel: RedefinirSenhaViewModel = viewModel()
) {
    val context = LocalContext.current

    val passoAtual by viewModel.passoAtual.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sucesso by viewModel.sucesso.collectAsState()
    val erro by viewModel.erro.collectAsState()

    val codigo by viewModel.codigo.collectAsState()
    val novaSenha by viewModel.novaSenha.collectAsState()
    val confirmarSenha by viewModel.confirmarSenha.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.email.value = emailRecuperacao
    }

    LaunchedEffect(sucesso) {
        if (sucesso) {
            Toast.makeText(context, "Senha redefinida com sucesso!", Toast.LENGTH_LONG).show()
            onNavigateToLogin()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Recuperar Senha",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        erro?.let { mensagemErro ->
            Text(
                text = mensagemErro,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (passoAtual == 1) {
            Text(
                text = "Enviamos um código de 6 dígitos para $emailRecuperacao. Digite-o abaixo:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = codigo,
                onValueChange = {
                    viewModel.codigo.value = it
                    viewModel.limparErro()
                },
                label = { Text("Código de Recuperação") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.avancarParaSenhas() },
                modifier = Modifier.fillMaxWidth(),
                enabled = codigo.isNotBlank()
            ) {
                Text("Avançar")
            }
        }

        else if (passoAtual == 2) {
            Text(
                text = "Código validado localmente. Crie sua nova senha.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = novaSenha,
                onValueChange = {
                    viewModel.novaSenha.value = it
                    viewModel.limparErro()
                },
                label = { Text("Nova Senha") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmarSenha,
                onValueChange = {
                    viewModel.confirmarSenha.value = it
                    viewModel.limparErro()
                },
                label = { Text("Confirmar Nova Senha") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.enviarNovaSenha() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Redefinir Senha")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { viewModel.voltarParaCodigo() }) {
                    Text("Voltar e corrigir código")
                }
            }
        }
    }
}