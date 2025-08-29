package com.example.todoapplication.screen.home

import com.example.todoapplication.task.Task


data class HomeScreenState (
    val tasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val fullName: String? = null
)
