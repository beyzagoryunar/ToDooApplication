package com.example.todoapplication.notification

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapplication.data.model.NotificationSendDTO
import com.example.todoapplication.data.model.TokenManager
import com.example.todoapplication.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val tokenManager: TokenManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("NotificationWorker", "doWork() başladı.") // Worker'ın başladığını görelim
        return try {
            val userId = tokenManager.getUserId()
            Log.d("NotificationWorker", "UserId alındı: $userId")

            if (userId == null) {
                Log.e("NotificationWorker", "Kullanıcı ID'si null geldi. Worker başarısız olacak.")
                return Result.failure()
            }

            Log.d("NotificationWorker", "Backend'e bildirim isteği gönderiliyor...")
            val success = taskRepository.sendNotification()

            if (success) {
                Log.d("NotificationWorker", "Backend isteği başarılı. Worker tamamlandı.")
                Result.success()
            } else {
                Log.e(
                    "NotificationWorker",
                    "Backend isteği başarısız oldu. Worker yeniden deneyecek."
                )
                Result.retry()
            }

        } catch (e: Exception) {
            Log.e("NotificationWorker", "doWork içinde bir HATA oluştu: ${e.message}", e)
            Result.retry()
        }
    }
}