package com.example.todoapplication.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todoapplication.ui.theme.ThemeSetting

@Composable
fun ThemeSelectionDialog(
    onDismiss: () -> Unit
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val currentTheme by viewModel.themeSetting.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tema Seçimi") },
        text = {
            Column {
                ThemeSetting.entries.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (theme == currentTheme),
                                onClick = {
                                    viewModel.setTheme(theme)
                                }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (theme == currentTheme),
                            onClick = { viewModel.setTheme(theme) }
                        )
                        Text(
                            text = theme.name.replaceFirstChar { it.titlecase() },
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}