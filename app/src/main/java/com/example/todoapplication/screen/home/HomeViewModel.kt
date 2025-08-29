    package com.example.todoapplication.screen.home

    import android.app.Application
    import android.util.Log
    import androidx.lifecycle.AndroidViewModel
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import androidx.work.ExistingPeriodicWorkPolicy
    import androidx.work.PeriodicWorkRequestBuilder
    import androidx.work.WorkManager
    import com.example.todoapplication.data.model.TaskModelDto
    import com.example.todoapplication.data.model.TokenManager
    import com.example.todoapplication.data.model.toDto
    import com.example.todoapplication.data.model.toLocalTask
    import com.example.todoapplication.notification.NotificationWorker
    import com.example.todoapplication.repository.AuthRepository
    import com.example.todoapplication.repository.TaskRepository
    import com.example.todoapplication.task.Task
    import com.onesignal.OneSignal
    import dagger.hilt.android.lifecycle.HiltViewModel
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.flow.first
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch
    import java.text.SimpleDateFormat
    import java.util.Calendar
    import java.util.Date
    import java.util.Locale
    import java.util.TimeZone
    import java.util.concurrent.TimeUnit
    import javax.inject.Inject

    @HiltViewModel
    class HomeViewModel @Inject constructor(
        private val repository: TaskRepository,
        private val authRepository: AuthRepository,
        private val tokenManager: TokenManager,
        private val application: Application
    ) : AndroidViewModel(application) {
        private var allTasks: List<Task> = emptyList()
        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

        private val _uiState = MutableStateFlow(HomeScreenState())
        val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()
        private val _logoutState = MutableStateFlow(false)
        val logoutState: StateFlow<Boolean> = _logoutState.asStateFlow()

        fun logout() {
            viewModelScope.launch {
                cancelNotificationWorker()
                authRepository.performLogout()
                _logoutState.value = true
            }
        }

        fun onLogoutComplete() {
            _logoutState.value = false
        }

        fun loadUserName() {
            viewModelScope.launch {
                val currentUserName = tokenManager.getFullName().first()
                _uiState.update { currentState ->
                    currentState.copy(fullName = currentUserName ?: "Kullanıcı")
                }
            }
        }

        fun getTasks() {
            viewModelScope.launch {
                _uiState.update { state ->
                    state.copy(isLoading = true, error = null)
                }
                try {

                    val responseTasks = repository.getTasks()
                    allTasks = responseTasks

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            tasks = filteredTasks(allTasks, state.searchQuery)
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = "Görevler yüklenemedi: ${e.message}"
                        )
                    }
                }
            }
        }

        fun addTask(name: String, description: String) {
            viewModelScope.launch {
                _uiState.update { state ->
                    state.copy(isLoading = true, error = null)
                }
                try {
                    val calender = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 24) }
                    val dueDateString = getIsoString(calender)
                    val newTaskDto = Task(
                        name = name,
                        description = description,
                        isComplete = false,
                        createdAt = getCurrentIsoTime(),
                        dueDate = dueDateString,
                    ).toDto()

                    val resultDto = repository.addTask(newTaskDto)

                    if (resultDto != null) {
                        val result = resultDto.toLocalTask()
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                tasks = state.tasks + result
                            )
                        }
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = "Görev eklenirken bir hata oluştu"
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Error adding task: ${e.message}")
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = "Görev eklenirken bir hata oluştu: ${e.message}"
                        )
                    }
                }
            }
        }

        fun updateTask(taskToUpdate: Task, newName: String, newDescription: String) {
            viewModelScope.launch {
                _uiState.update { state ->
                    state.copy(isLoading = true, error = null)
                }
                try {
                    val updatedTaskDto = taskToUpdate.copy(
                        name = newName,
                        description = newDescription
                    ).toDto()
                    val result = repository.updateTask(taskToUpdate.id, updatedTaskDto)

                    if (result != null) {
                        getTasks()
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = "Görev güncellenirken bir hata oluştu"
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = "Görev güncellenirken bir hata oluştu: ${e.message}"
                        )
                    }
                }
            }
        }

        fun deleteTask(taskId: Int) {
            viewModelScope.launch {
                _uiState.update { state ->
                    state.copy(isLoading = true, error = null)
                }
                try {
                    val success = repository.deleteTask(taskId)
                    if (success) {

                        allTasks = repository.getTasks()
                        val hasUnfinishedTasks = allTasks.any { !it.isComplete }

                        if (!hasUnfinishedTasks) {
                        }

                        // UI'ı güncelle
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                tasks = filteredTasks(allTasks, state.searchQuery)
                            )
                        }
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = "Görev silinirken bir hata oluştu"
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = "Görev silinirken bir hata oluştu: ${e.message}"
                        )
                    }
                }
            }
        }
        fun onSearchQueryChange(query: String) {
            _uiState.update { state ->
                state.copy(
                    searchQuery = query,
                    tasks = filteredTasks(allTasks, query)
                )
            }
        }
        private fun filteredTasks(tasks: List<Task>, query: String): List<Task> {
            if (query.isBlank()) {
                return tasks
            }
            return tasks.filter { task ->
                task.name.contains(query, ignoreCase = true) ||
                        (task.description?.contains(query, ignoreCase = true) == true)
            }
        }
        fun toggleDone(toggledTask: Task) {
            viewModelScope.launch {
                _uiState.update { state ->
                    state.copy(isLoading = true, error = null)
                }
                try {
                    val updatedTaskDto = toggledTask.copy(isComplete = !toggledTask.isComplete).toDto()
                    val result = repository.updateTask(toggledTask.id, updatedTaskDto)
                    if (result != null) {
                        // Güncel görev listesini çek
                        allTasks = repository.getTasks()
                        // UI'ı güncelle
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                tasks = filteredTasks(allTasks, state.searchQuery)
                            )
                        }
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = "Görev güncellenirken bir hata oluştu"
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = "Görev güncellenirken bir hata oluştu: ${e.message}"
                        )
                    }
                }
            }
        }

        private fun getIsoString(calendar: Calendar): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(calendar.time)
        }

        private fun getCurrentIsoTime(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date())
        }

        private fun cancelNotificationWorker() {
            WorkManager.getInstance(application).cancelUniqueWork("periodicNotificationWorker")
            Log.d("HomeViewModel", "NotificationWorker iptal edildi.")
        }
    }