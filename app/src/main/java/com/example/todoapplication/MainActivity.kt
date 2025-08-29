package com.example.todoapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapplication.notification.NotificationWorker
import com.example.todoapplication.screen.home.HomeScreen
import com.example.todoapplication.screen.auth.LoginScreen
import com.example.todoapplication.screen.auth.RegisterScreen
import com.example.todoapplication.screen.splash.SplashScreen
import com.example.todoapplication.ui.theme.ToDoApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by viewModel.themeSetting.collectAsState()
            ToDoApplicationTheme(themeSetting = theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),

                    color = MaterialTheme.colorScheme.background
                ) {

                    ToDoAppNavigation()
                }
            }
        }
    }
}


//
//    private fun calculateInitialDelay(): Long {
//        val now = Calendar.getInstance()
//        val due = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 9)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//        }
//        if (due.before(now)) {
//            due.add(Calendar.DAY_OF_MONTH, 1)
//        }
//        return due.timeInMillis - now.timeInMillis
//    }
//}

    @Composable
    fun ToDoAppNavigation() {
        val navController = rememberNavController()


        val mainViewModel: MainViewModel = hiltViewModel()


        val startDestination by mainViewModel.startDestination.collectAsStateWithLifecycle()
        if (startDestination != null) {

            NavHost(navController = navController, startDestination = "splash") {

                composable("splash") {
                    SplashScreen(navController = navController)
                }

                composable("login") {
                    LoginScreen(
                        onNavigateToRegister = { navController.navigate("register") },
                        navController = navController
                    )
                }
                composable("register") {
                    RegisterScreen(
                        onNavigateToLogin = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("home") {
                    HomeScreen(navController = navController)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize())
        }
    }

