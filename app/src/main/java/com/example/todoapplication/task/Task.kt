package com.example.todoapplication.task

// task/Task.kt dosyasında
data class Task(
    val id: Int=0,
    val name: String,
    var isComplete: Boolean,
    val description: String? = "",
    val createdAt: String,
    val dueDate: String? = "",
    val userId: Int? = null
) {
}
