package com.example.todoapplication.screen.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.todoapplication.ui.theme.ThemeSetting
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val THEME_KEY = stringPreferencesKey("theme_setting")
    }

    val themeSettingFlow: Flow<ThemeSetting> = dataStore.data.map { preferences ->
        ThemeSetting.valueOf(preferences[THEME_KEY] ?: ThemeSetting.SYSTEM.name)
    }

    suspend fun setThemeSetting(themeSetting: ThemeSetting) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeSetting.name
        }
    }
}