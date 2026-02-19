package com.example.diaria_do_motorista.sync.exeptions.sync

import android.content.Context
import android.provider.CalendarContract
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import javax.inject.Inject

class SyncWorker @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val syncManager: SyncManager
): CoroutineWorker (context,params){
    override suspend fun doWork(): Result {
        return try {
            syncManager.startSync()

            // Aguardar um pouco para garantir que a sincronização iniciou
            kotlinx.coroutines.delay(1000)

            // Aguardar até que a sincronização termine
            while (syncManager.syncState.value == CalendarContract.SyncState.SYNCING) {
                kotlinx.coroutines.delay(500)
            }

            if (syncManager.syncState.value == CalendarContract.SyncState.COMPLETED) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}