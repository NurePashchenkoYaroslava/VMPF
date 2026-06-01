package com.example.librarylab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    var currentScreen by remember { mutableStateOf(0) }

    when (currentScreen) {
        0 -> MainMenu(onLevelSelected = { currentScreen = it })
        1 -> Level1Screen(onBack = { currentScreen = 0 })
        2 -> Level2Screen(onBack = { currentScreen = 0 })
        3 -> Level3Screen(onBack = { currentScreen = 0 })
        4 -> Level4Screen(onBack = { currentScreen = 0 })
    }
}

@Composable
fun MainMenu(onLevelSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Лабораторна робота", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Варіант 10", fontSize = 18.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = { onLevelSelected(1) }, modifier = Modifier.fillMaxWidth().height(60.dp)) {
            Text("Рівень 1: день тижня за номером", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onLevelSelected(2) }, modifier = Modifier.fillMaxWidth().height(60.dp)) {
            Text("Рівень 2: привітання з днем народження", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onLevelSelected(3) }, modifier = Modifier.fillMaxWidth().height(60.dp)) {
            Text("Рівень 3: підрахунок файлів", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onLevelSelected(4) }, modifier = Modifier.fillMaxWidth().height(60.dp)) {
            Text("Рівень 4: додаток «бібліотека»", fontSize = 16.sp)
        }
    }
}

@Composable
fun Level1Screen(onBack: () -> Unit) {
    var inputNumber by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("Введіть число від 1 до 7") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Рівень 1 (завдання 10)", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = inputNumber,
            onValueChange = { inputNumber = it },
            label = { Text("Номер дня (1-7)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val num = inputNumber.toIntOrNull()
            resultText = when (num) {
                1 -> "Понеділок"
                2 -> "Вівторок"
                3 -> "Середа"
                4 -> "Четвер"
                5 -> "П'ятниця"
                6 -> "Субота"
                7 -> "Неділя"
                else -> "Помилка: введіть число суворо від 1 до 7!"
            }
        }) {
            Text("Визначити день")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(resultText, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = onBack) { Text("Назад в меню") }
    }
}

@Composable
fun Level2Screen(onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var dayInput by remember { mutableStateOf("") }
    var monthInput by remember { mutableStateOf("") }
    var greetingResult by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Рівень 2 (завдання 10)", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Ім'я іменинника") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = dayInput, onValueChange = { dayInput = it }, label = { Text("День народження (напр. 29)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = monthInput, onValueChange = { monthInput = it }, label = { Text("Місяць народження (1-12)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val d = dayInput.toIntOrNull()
            val m = monthInput.toIntOrNull()
            if (name.isEmpty() || d == null || m == null) {
                greetingResult = "Будь ласка, заповніть усі поля коректно."
            } else {
                val today = Calendar.getInstance()
                val currentDay = today.get(Calendar.DAY_OF_MONTH)
                val currentMonth = today.get(Calendar.MONTH) + 1

                greetingResult = if (d == currentDay && m == currentMonth) {
                    "З Днем Народження, $name! Успіхв тобі"
                } else {
                    "Привіт, $name. Сьогодні не твій день народження, але все одно гарного дня."
                }
            }
        }) {
            Text("Перевірити дату й привітати")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(greetingResult, fontSize = 18.sp, modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = onBack) { Text("Назад в меню") }
    }
}

@Composable
fun Level3Screen(onBack: () -> Unit) {
    val context = LocalContext.current
    var fileCountInfo by remember { mutableStateOf("Натисніть кнопку для сканування каталогу додатка") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Рівень 3 (завдання 10)", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            val cacheDir = context.cacheDir
            File(cacheDir, "test_file1.txt").createNewFile()
            File(cacheDir, "test_file2.log").createNewFile()
            File(cacheDir, "lab3_report.pdf").createNewFile()

            val filesList = cacheDir.listFiles()
            val count = filesList?.size ?: 0

            fileCountInfo = "Шлях до каталогу:\n${cacheDir.absolutePath}\n\n" +
                    "Знайдено файлів у каталозі: $count\n\n" +
                    "Список файлів:\n" +
                    (filesList?.joinToString("\n") { it.name } ?: "каталог порожній")
        }) {
            Text("Підрахувати файли")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)).padding(16.dp)) {
            Text(fileCountInfo, fontSize = 14.sp, modifier = Modifier.verticalScroll(rememberScrollState()))
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onBack) { Text("Назад в меню") }
    }
}

@Composable
fun Level4Screen(onBack: () -> Unit) {
    val libraryManager = remember {
        LibraryManager().apply {
            addBook(Book(1, "Мовчазна пацієнтка", "Блейк Крауч", 3))
            addBook(Book(2, "Казка", "Стівен Кінг", 1))
            addReader(Reader(1, "Валерія Сергієнко"))
            addReader(Reader(2, "Мирослав Дмітрюк"))
        }
    }

    var statusText by remember { mutableStateOf(libraryManager.getLibraryStatus()) }
    var logText by remember { mutableStateOf("Лог операцій порожній\n") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Рівень 4 (завдання 10)", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxWidth().weight(0.4f).background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)).padding(12.dp)) {
            Text(statusText, fontSize = 14.sp, modifier = Modifier.verticalScroll(rememberScrollState()))
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Інтерактивні дії:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                val msg = libraryManager.borrowBook(1, 1)
                logText += "- $msg\n"
                statusText = libraryManager.getLibraryStatus()
            }) { Text("Валерія бере Пацієнтку") }

            Button(onClick = {
                val msg = libraryManager.borrowBook(2, 2)
                logText += "- $msg\n"
                statusText = libraryManager.getLibraryStatus()
            }) { Text("Мирослав бере Казку") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                val msg = libraryManager.borrowBook(1, 2)
                logText += "- $msg\n"
                statusText = libraryManager.getLibraryStatus()
            }) { Text("Валерія бере Казку (конфлікт)") }

            Button(onClick = {
                val msg = libraryManager.returnBook(2, 2)
                logText += "- $msg\n"
                statusText = libraryManager.getLibraryStatus()
            }) { Text("Мирослав повертає Казку") }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxWidth().weight(0.4f).background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)).padding(12.dp)) {
            Text(logText, fontSize = 12.sp, modifier = Modifier.verticalScroll(rememberScrollState()))
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onBack) { Text("Назад в меню") }
    }
}