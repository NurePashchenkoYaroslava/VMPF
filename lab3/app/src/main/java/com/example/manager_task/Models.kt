package com.example.manager_task

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Task(
    val id: Int,
    var title: String,
    var description: String,
    var completed: Boolean,
    var dueDate: String
) {
    fun isOverdue(): Boolean {
        if (completed || dueDate.isBlank()) return false
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val taskDate = sdf.parse(dueDate) ?: return false

            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            taskDate.before(today)
        } catch (_: Exception) {
            false
        }
    }
}

data class User(val username: String, val password: String, val role: String)

val mockUsers = listOf(
    User("admin", "1234", "admin"),
    User("user1", "0000", "user")
)