package com.example.financecontrolapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financecontrolapp.data.LancamentoResponse
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardCard(lancamentos: List<LancamentoResponse>) {
    val receitas = lancamentos.filter { it.tipo == "RECEITA" }.sumOf { it.valor }
    val despesas = lancamentos.filter { it.tipo == "DESPESA" }.sumOf { it.valor }
    val saldo = receitas - despesas

    val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Saldo Atual", style = MaterialTheme.typography.titleMedium)

            Text(
                text = formatador.format(saldo),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (saldo >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color(0xFF43A047))
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text("Receitas", style = MaterialTheme.typography.bodySmall)
                        Text(formatador.format(receitas), fontWeight = FontWeight.SemiBold, color = Color(0xFF43A047))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFFE53935))
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text("Despesas", style = MaterialTheme.typography.bodySmall)
                        Text(formatador.format(despesas), fontWeight = FontWeight.SemiBold, color = Color(0xFFE53935))
                    }
                }
            }
        }
    }
}