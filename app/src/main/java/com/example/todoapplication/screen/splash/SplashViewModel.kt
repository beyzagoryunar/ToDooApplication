package com.example.todoapplication.screen.splash

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapplication.data.model.TokenManager
import com.example.todoapplication.di.ToDoApplication
import com.example.todoapplication.notification.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val application: Application
) : ViewModel() {

    private val _splashState = MutableStateFlow(SplashState())
    val splashState: StateFlow<SplashState> = _splashState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            if (!token.isNullOrBlank()) {
                _splashState.update { it.copy(navigateToHome = true) }
            } else {
                _splashState.update { it.copy(navigateToAuth = true) }
            }
        }
    }


    data class SplashState(
        val rememberMe: Boolean = false,
        val navigateToHome: Boolean = false,
        val navigateToAuth: Boolean = false
    )
}
