package com.example.todoapplication.repository

import android.content.Context
import android.util.Log
import com.example.todoapplication.api.ApiService
import com.example.todoapplication.data.model.LoginResponse
import com.example.todoapplication.data.model.NotificationDTO
import com.example.todoapplication.data.model.TokenManager
import com.example.todoapplication.data.model.TokenResponseDto
import com.example.todoapplication.data.model.User
import com.example.todoapplication.screen.auth.LoginRequest
import com.example.todoapplication.screen.auth.RegisterRequest
import com.onesignal.OneSignal
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService,
    @ApplicationContext private val context: Context,
    private val tokenManager: TokenManager
) {
    suspend fun login(request: LoginRequest): LoginResponse? {
        try {
            val response = api.loginUser(request)
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                val token = loginResponse.accessToken

                if (!token.isNullOrEmpty()) {
                    val formattedToken = "Bearer $token"

                    val playerId = OneSignal.User.pushSubscription.id
                    val deviceId = android.provider.Settings.Secure.getString(
                        context.contentResolver,
                        android.provider.Settings.Secure.ANDROID_ID
                    )
                    val notificationDTO = NotificationDTO(playerId, deviceId)

                    api.InsertOneSignalPlayerId(
                        token = formattedToken,
                        id = loginResponse.userId!!,
                        request = notificationDTO
                    )
                }

                return loginResponse
            }
            return null
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error during login: ${e.message}")
            return null
        }
    }

    suspend fun register(request: RegisterRequest): Boolean {
        try {
            val response = api.registerUser(request)
            if (response.isSuccessful) {
                return true
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    suspend fun performLogout() {
        try {
            val token = tokenManager.getToken()
            val userId = tokenManager.getUserId()?.toIntOrNull()
            val playerId = OneSignal.User.pushSubscription.id


            if (token.isNullOrEmpty() || userId == null || playerId.isNullOrEmpty()) {
                Log.w("AuthRepository", "Token/UserId/PlayerId eksik, sunucudan silinemedi.")
            } else {
                val response = api.deleteOneSignalPlayerId(
                    token = "Bearer $token",
                    userId = userId,
                    playerId = playerId
                )
                if (response.isSuccessful) {
                    Log.i("AuthRepository", "Player ID sunucudan başarıyla silindi.")
                } else {
                    Log.e(
                        "AuthRepository",
                        "Player ID sunucudan silinemedi. Kod: ${response.code()}"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Logout sırasında sunucu hatası: ${e.message}")
        } finally {
            tokenManager.clear()
        }
    }
}


