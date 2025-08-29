package com.example.todoapplication.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val uiState by authViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = authState is AuthState.Loading

    LaunchedEffect(key1 = authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                if (state.message.contains("Giriş")) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                    authViewModel.resetState()
                }
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // <<< DEĞİŞİKLİK
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Hoş Geldiniz", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground) // <<< DEĞİŞİKLİK
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = uiState.username,
                onValueChange = { authViewModel.onUsernameChange(it) },
                label = { Text("Kullanıcı Adı") }, // <<< DEĞİŞİKLİK
                leadingIcon = { Icon(Icons.Default.AccountCircle, "Username") }, // <<< DEĞİŞİKLİK
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors( // <<< DEĞİŞİKLİK
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { authViewModel.onPasswordChange(it) },
                label = { Text("Şifre") }, // <<< DEĞİŞİKLİK
                leadingIcon = { Icon(Icons.Default.Lock, "Password") }, // <<< DEĞİŞİKLİK
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors( // <<< DEĞİŞİKLİK
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                enabled = !isLoading
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Checkbox(
                    checked = uiState.rememberMe,
                    onCheckedChange = { authViewModel.onRememberMeChange(it) },
                    colors = CheckboxDefaults.colors( // <<< DEĞİŞİKLİK
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = !isLoading
                )
                Text(
                    "Beni Hatırla",
                    color = MaterialTheme.colorScheme.onBackground, // <<< DEĞİŞİKLİK
                    modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { authViewModel.login() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), // <<< DEĞİŞİKLİK
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary) else Text("Giriş Yap") // <<< DEĞİŞİKLİK
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Hesabınız yok mu?", color = MaterialTheme.colorScheme.onBackground) // <<< DEĞİŞİKLİK
                TextButton(onClick = onNavigateToRegister, enabled = !isLoading) {
                    Text("Kayıt Ol", color = MaterialTheme.colorScheme.primary) // <<< DEĞİŞİKLİK
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val uiState by authViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = authState is AuthState.Loading

    LaunchedEffect(key1 = authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                if (state.message.contains("Kayıt")) {
                    snackbarHostState.showSnackbar(state.message)
                    onNavigateToLogin()
                    authViewModel.resetState()
                }
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // <<< DEĞİŞİKLİK
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Yeni Hesap Oluştur", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground) // <<< DEĞİŞİKLİK
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = uiState.username,
                onValueChange = { authViewModel.onUsernameChange(it) },
                label = { Text("Kullanıcı Adı (Giriş için)") }, // <<< DEĞİŞİKLİK
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors( // <<< DEĞİŞİKLİK
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = { authViewModel.onFullNameChange(it) },
                label = { Text("Adınız Soyadınız (Görünecek isim)") }, // <<< DEĞİŞİKLİK
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors( // <<< DEĞİŞİKLİK
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { authViewModel.onPasswordChange(it) },
                label = { Text("Şifre") }, // <<< DEĞİŞİKLİK
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors( // <<< DEĞİŞİKLİK
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { authViewModel.register() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), // <<< DEĞİŞİKLİK
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary) else Text("Kayıt Ol") // <<< DEĞİŞİKLİK
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Zaten hesabın var mı?", color = MaterialTheme.colorScheme.onBackground) // <<< DEĞİŞİKLİK
                TextButton(onClick = onNavigateToLogin, enabled = !isLoading) {
                    Text("Giriş Yap", color = MaterialTheme.colorScheme.primary) // <<< DEĞİŞİKLİK
                }
            }
        }
    }
}