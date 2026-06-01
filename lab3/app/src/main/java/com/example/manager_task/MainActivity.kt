package com.example.manager_task

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.ui.graphics.Color
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manager_task.ui.theme.Manager_taskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Manager_taskTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    var currentUser by remember { mutableStateOf<User?>(null) }
    var currentScreen by remember { mutableStateOf("login") }

    val tasks = remember { mutableStateListOf(
        Task(1, "Підготувати презентацію", "Зробити 8 слайдів до курсової роботи", false, "2026-06-15"),
        Task(2, "Купити книгу мовчазна пацієнтка", "Подарунок для подруги на день народження", false, "2026-05-20")
    ) }

    when (currentScreen) {
        "login" -> LoginScreen(
            modifier = modifier,
            onLogin = { username, password ->
                val user = mockUsers.find { it.username == username && it.password == password }
                if (user != null) {
                    currentUser = user
                    currentScreen = "tasks"
                }
                user != null
            }
        )
        "tasks" -> MainTasksScreen(
            modifier = modifier,
            user = currentUser!!,
            tasks = tasks,
            onNavigateToAnalytics = { currentScreen = "analytics" },
            onLogout = {
                currentUser = null
                currentScreen = "login"
            }
        )
        "analytics" -> AnalyticsScreen(
            tasks = tasks,
            onBack = { currentScreen = "tasks" }
        )
    }
}

@Composable
fun MainTasksScreen(
    modifier: Modifier = Modifier,
    user: User,
    tasks: MutableList<Task>,
    onNavigateToAnalytics: () -> Unit,
    onLogout: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var targetEmail by remember { mutableStateOf("") }

    var activeTaskForDetails by remember { mutableStateOf<Task?>(null) }
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Привіт, ${user.username}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Роль: ${user.role}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Row {
                IconButton(onClick = onNavigateToAnalytics) {
                    Icon(imageVector = Icons.Default.List, contentDescription = "Аналітика", tint = Color(0xFF9575CD))
                }
                TextButton(onClick = onLogout) { Text("Вийти", color = Color(0xFFD32F2F)) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Назва задачі") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Дедлайн (РРРР-ММ-ДД)") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    tasks.add(Task(tasks.size + 1, title, "Опис відсутній. Натисніть іконку інформації щоб додати.", false, date))
                    title = ""
                    date = ""
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784))
        ) {
            Text("Додати задачу")
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

        Text("Відправити нагадування:", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = targetEmail,
                onValueChange = { targetEmail = it },
                label = { Text("Email отримувача") },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                singleLine = true
            )
            Button(
                onClick = {
                    if (targetEmail.isBlank()) {
                        Toast.makeText(context, "Введіть пошту!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val pendingTasks = tasks.filter { !it.completed }
                    var reportText = "Привіт!\nНагадування про невиконані задачі:\n\n"
                    pendingTasks.forEach { task -> reportText += "- ${task.title} (Дедлайн: ${task.dueDate})\n" }

                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$targetEmail")
                        putExtra(Intent.EXTRA_SUBJECT, "Нагадування: Менеджер Задач")
                        putExtra(Intent.EXTRA_TEXT, reportText)
                    }
                    try { context.startActivity(intent) } catch (_: Exception) {
                        Toast.makeText(context, "Поштовий додаток не знайдено!", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
            ) {
                Text("Надіслати")
            }
        }

        Text("Список ваших завдань:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    userRole = user.role,
                    onStatusChange = { isChecked ->
                        val index = tasks.indexOf(task)
                        if (index != -1) tasks[index] = task.copy(completed = isChecked)
                    },
                    onDelete = { tasks.remove(task) },
                    onDetailsClick = { activeTaskForDetails = task }
                )
            }
        }
    }

    activeTaskForDetails?.let { task ->
        TaskDetailsDialog(
            task = task,
            onDismiss = { activeTaskForDetails = null },
            onSave = { newTitle, newDesc, newDate ->
                val index = tasks.indexOf(task)
                if (index != -1) {
                    tasks[index] = task.copy(title = newTitle, description = newDesc, dueDate = newDate)
                }
            }
        )
    }
}