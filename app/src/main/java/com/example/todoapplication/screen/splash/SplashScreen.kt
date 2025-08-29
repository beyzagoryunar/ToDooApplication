package com.example.todoapplication.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.todoapplication.R

@Composable
fun SplashScreen(
    navController: NavController,
    splashViewModel: SplashViewModel= hiltViewModel()
) {
    //val state = splashViewModel.splashState.collectAsStateWithLifecycle()
    val state by splashViewModel.splashState.collectAsStateWithLifecycle()


    LaunchedEffect(state){
        if (state.navigateToHome) {
            navController.navigate("home") {
                popUpTo("splash") {
                    inclusive = true
                }
            }
        }
        if (state.navigateToAuth) {
            navController.navigate("login") {
                popUpTo("splash") {
                    inclusive = true
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource( R.mipmap.ic_launcher_foreground),
                contentDescription = "Uygulama Logosu",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "ToDo Application",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}