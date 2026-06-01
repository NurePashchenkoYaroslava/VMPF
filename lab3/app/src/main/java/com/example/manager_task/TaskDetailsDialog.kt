package com.example.manager_task

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskDetailsDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var editedTitle by remember { mutableStateOf(task.title) }
    var editedDesc by remember { mutableStateOf(task.description) }
    var editedDate by remember { mutableStateOf(task.dueDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Деталі завдання") },
        text = {
            Column {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text("Назва") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = editedDesc,
                    onValueChange = { editedDesc = it },
                    label = { Text("Опис завдання") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    minLines = 3
                )
                OutlinedTextField(
                    value = editedDate,
                    onValueChange = { editedDate = it },
                    label = { Text("Дедлайн (РРРР-ММ-ДД)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(editedTitle, editedDesc, editedDate)
                onDismiss()
            }) {
                Text("Зберегти зміни")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрити")
            }
        }
    )
}