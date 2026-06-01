package com.example.manager_task

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TaskItem(
    task: Task,
    userRole: String,
    onStatusChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onDetailsClick: () -> Unit
) {
    val context = LocalContext.current
    val isOverdue = task.isOverdue()
    val cardColor = when {
        task.completed -> Color(0xFFE8F5E9)
        isOverdue -> Color(0xFFFFEBEE)
        else -> Color(0xFFFFF8E1)
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Checkbox(
                    checked = task.completed,
                    onCheckedChange = { onStatusChange(it) },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF81C784))
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = task.title, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Text(text = "До: ${task.dueDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    if (isOverdue) {
                        Text(text = "Прострочено", color = Color(0xFFD32F2F), fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Row {
                IconButton(onClick = onDetailsClick) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Деталі", tint = Color(0xFF64B5F6))
                }
                IconButton(onClick = {
                    if (userRole == "admin") {
                        onDelete()
                    } else {
                        Toast.makeText(context, "Видаляти може тільки admin!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Видалити", tint = Color(0xFFE57373))
                }
            }
        }
    }
}