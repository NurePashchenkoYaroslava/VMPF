package com.example.manager_task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnalyticsScreen(tasks: List<Task>, onBack: () -> Unit) {
    val total = tasks.size
    val completed = tasks.count { it.completed }
    val pending = total - completed
    val overdue = tasks.count { it.isOverdue() }
    val completedPct = if (total > 0) completed.toFloat() / total else 0f
    val pendingPct = if (total > 0) pending.toFloat() / total else 0f

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Аналітика виконання задач", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Всього задач: $total")
                Text("Виконано: $completed", color = Color(0xFF2E7D32))
                Text("В процесі: $pending", color = Color(0xFFE65100))
                Text("Прострочено: $overdue", color = Color.Red)
            }
        }

        Text("Співвідношення", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        if (total > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
            ) {
                if (completedPct > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(if (completedPct > 0) completedPct else 0.001f)
                            .background(Color(0xFF4CAF50), RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${(completedPct * 100).toInt()}%", color = Color.White, fontSize = 12.sp)
                    }
                }
                if (pendingPct > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(if (pendingPct > 0) pendingPct else 0.001f)
                            .background(Color(0xFFFF9800), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${(pendingPct * 100).toInt()}%", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        } else {
            Text("Немає даних для побудови графіка", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Назад до завдань")
        }
    }
}