package com.example.todoapplication.di

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapplication.data.model.TokenManager
import com.example.todoapplication.notification.NotificationWorker
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class ToDoApplication : Application(),Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var tokenManager: TokenManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        // Initialize with your OneSignal App ID
        OneSignal.initWithContext(this, "3949f1de-a978-4cff-9541-920bd065b7be")
        // Use this method to prompt for push notifications.
        // We recommend removing this method after testing and instead use In-App Messages to prompt for notification permission.
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
            scheduleWorkerOnAppStart()
        }
    }


        private fun scheduleWorkerOnAppStart() {
            // Bu işlemi ana iş parçacığını bloklamamak için bir coroutine içinde yapın
            CoroutineScope(Dispatchers.IO).launch {
                val token = tokenManager.getToken()
                if (!token.isNullOrBlank()) {
                    // Kullanıcı giriş yapmış, Worker'ı planla
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                    val workRequest =
                        PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .build()

                    workManager.enqueueUniquePeriodicWork(
                        "periodicNotificationWorker",
                        ExistingPeriodicWorkPolicy.KEEP,
                        workRequest
                    )
                    Log.d("ToDoApplication", "Worker uygulama başlangıcında planlandı.")
                }
            }
        }
    }

