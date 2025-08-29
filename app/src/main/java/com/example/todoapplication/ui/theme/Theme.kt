package com.example.todoapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = AccentPink,
    onPrimary = Color.White,
    secondary = AccentBlue,
    onSecondary = Color.Black,
    tertiary = AccentPink,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFB0B0B0)
)

private val LightColorScheme = lightColorScheme(
    primary = AccentPink,
    onPrimary = Color.White,
    secondary = AccentBlue,
    onSecondary = Color.White,
    tertiary = AccentPink,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary
)

enum class ThemeSetting{
    SYSTEM, LIGHT, DARK
}

@Composable
fun ToDoApplicationTheme(
    themeSetting: ThemeSetting = ThemeSetting.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeSetting) {
        ThemeSetting.SYSTEM -> isSystemInDarkTheme()
        ThemeSetting.LIGHT -> false
        ThemeSetting.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
