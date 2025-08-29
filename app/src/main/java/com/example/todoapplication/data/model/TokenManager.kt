package com.example.todoapplication.data.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context) {

    companion object {
        val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val REMEMBER_ME_KEY = stringPreferencesKey("remember_me")
        val PLAYER_ID_KEY = stringPreferencesKey("player_id")
    }
    private val dataStore = context.dataStore

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[USER_TOKEN_KEY] = token
        }
    }

    suspend fun saveUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    suspend fun getUserId(): String? {
        return dataStore.data.map { prefs ->
            prefs[USER_ID_KEY]
        }.first()
    }

    suspend fun getToken(): String? {
        return dataStore.data.map { prefs ->
            prefs[USER_TOKEN_KEY]
        }.first()
    }
    val tokenFlow: Flow<String?> = dataStore.data.map { prefs -> prefs[USER_TOKEN_KEY] }

    suspend fun saveFullName(name: String) {
        dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
        }
    }
    suspend fun savePlayerId(playerId: String) {
        dataStore.edit { prefs ->
            prefs[PLAYER_ID_KEY] = playerId
        }
    }

    suspend fun getPlayerId(): String? {
        return dataStore.data.map { prefs ->
            prefs[PLAYER_ID_KEY]
        }.first()
    }
    fun getFullName(): Flow<String?> {
        return dataStore.data.map { prefs ->
            prefs[USER_NAME_KEY]
        }
    }
    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
    suspend fun saveRememberMe(rememberMe: Boolean) {
        dataStore.edit { prefs ->
            prefs[REMEMBER_ME_KEY] = rememberMe.toString()
        }
    }
    suspend fun getRememberMe(): Boolean {
        val rememberMeString = dataStore.data.map { prefs ->
            prefs[REMEMBER_ME_KEY]
        }.first()
        return rememberMeString?.toBoolean() ?: false
    }
}


class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.getToken() } // Sadece TokenManager'dan oku
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}