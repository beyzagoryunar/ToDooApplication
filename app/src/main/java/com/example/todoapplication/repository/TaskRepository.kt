package com.example.todoapplication.repository

import android.util.Log
import com.example.todoapplication.api.ApiService
import com.example.todoapplication.data.model.NotificationSendDTO
import com.example.todoapplication.data.model.TaskModelDto
import com.example.todoapplication.data.model.TokenManager
import com.example.todoapplication.data.model.toLocalTask
import com.example.todoapplication.task.Task
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    // val tasksFlow = dataStore.tasksFlow
    suspend fun getTasks(): List<Task> {

        val token = tokenManager.getToken()
        if (token == null) {
            return emptyList()
        }
        val userId = tokenManager.getUserId()
        if (userId == null) {
            return emptyList()
        }


        Log.d("token", token)
        Log.d("userId", "$userId")
        val token2 = "Bearer $token"
        val response = api.getTasks(token2, userId = userId.toInt())
        if (response.isSuccessful) {
            response.body()?.let { taskModelDtoList ->
                return taskModelDtoList.map { it.toLocalTask() }
            }
        }
        return emptyList()
    }

    suspend fun addTask(task: TaskModelDto): TaskModelDto? {
        val userId = tokenManager.getUserId() ?: return null

        val token = tokenManager.getToken()
        val token2="Bearer $token"
        val newTask = task.copy(userId = userId.toInt())
        val response = api.addTask(
            token = token2 ?: return null,
            task = newTask ?: return null
        )

        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun updateTask(id: Int, task: TaskModelDto): TaskModelDto? {
        val token = tokenManager.getToken() ?: return null
        val token2="Bearer $token"
        val response = api.updateTask(
            token = token2,
            id = id,
            task = task
        )
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
    suspend fun sendNotification(): Boolean {
        val token = tokenManager.getToken() ?: return false
        val token2="Bearer $token"
        val userId = tokenManager.getUserId() ?: return false
        val response = api.SendNotification(token2, userId.toInt())
        return response.isSuccessful
    }

    suspend fun deleteTask(id: Int): Boolean {
        val token = tokenManager.getToken() ?: return false
        val token2 = "Bearer $token"
        val response = api.deleteTask(token2, id)
        return response.isSuccessful
    }
}


