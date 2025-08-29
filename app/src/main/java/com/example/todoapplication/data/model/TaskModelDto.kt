package com.example.todoapplication.data.model

import com.example.todoapplication.task.Task

data class TaskModelDto(
    val id: Int? = null,
    val name: String,
    val isComplete: Boolean,
    val description: String? = null,
    val createdAt: String?,
    val dueDate: String? = null,
    val userId: Int?
)
// DTO'dan local Task objesine dönüşüm
fun TaskModelDto.toLocalTask() = Task(
    id = id ?: 0,
    name = name,
    description = description ?: "",
    isComplete = isComplete,
    createdAt = createdAt?: "",
    dueDate = this.dueDate,
    userId = userId
)

// Local Task objesinden DTO’ya dönüşüm
fun Task.toDto() = TaskModelDto(
    id = id,
    name = name,
    description = description,
    isComplete = isComplete,
    createdAt = createdAt,
    dueDate = this.dueDate,
    userId = this.userId
)
