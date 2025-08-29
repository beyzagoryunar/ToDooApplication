package com.example.todoapplication // veya projenizdeki uygun bir paket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapplication.data.model.TokenManager
import com.example.todoapplication.screen.settings.SettingsManager
import com.example.todoapplication.ui.theme.ThemeSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsManager: SettingsManager,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    val themeSetting = settingsManager.themeSettingFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeSetting.SYSTEM
    )

    init {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            if (token.isNullOrBlank()) {
                _startDestination.value = "login"
            } else {
                _startDestination.value = "home"
            }
        }
    }
}