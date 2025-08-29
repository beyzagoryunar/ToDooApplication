package com.example.todoapplication.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.todoapplication.data.model.TokenManager
import com.example.todoapplication.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object Idle : AuthState()
}

data class AuthUiState(
    val username: String = "",
    val fullName: String = "",
    val password: String = "",
    val rememberMe: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username) }
    }
    fun onFullNameChange(fullName: String) {
        _uiState.update { it.copy(fullName = fullName) }
    }
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }
    fun resetState() {
        _authState.value = AuthState.Idle
    }
    fun onRememberMeChange(remember: Boolean) {
        _uiState.update { it.copy(rememberMe = remember) }
    }
    fun login() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val request = LoginRequest(
                    username = _uiState.value.username,
                    password = _uiState.value.password
                )
                val loginResponse = authRepository.login(request)

                if (loginResponse != null) {

                    tokenManager.saveToken(loginResponse.accessToken!!)
                    tokenManager.saveUserId(loginResponse.userId.toString())
                    tokenManager.saveFullName(loginResponse.fullName!!)

                    tokenManager.saveRememberMe(_uiState.value.rememberMe)

                    _authState.value = AuthState.Success("Giriş başarılı!")
                } else {
                    _authState.value = AuthState.Error("Kullanıcı adı veya şifre yanlış.")
                }
            } catch (e: Exception) {
                // ...
            }
        }
    }

    fun register() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    username = _uiState.value.username,
                    fullName = _uiState.value.fullName,
                    password = _uiState.value.password
                )
                val isSuccessful = authRepository.register(request)

                if (isSuccessful) {
                    _authState.value = AuthState.Success("Kayıt başarılı! Lütfen giriş yapın.")
                } else {
                    _authState.value = AuthState.Error("Kayıt başarısız oldu.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Kayıt sırasında bir hata oluştu.")
            }
        }
    }
}