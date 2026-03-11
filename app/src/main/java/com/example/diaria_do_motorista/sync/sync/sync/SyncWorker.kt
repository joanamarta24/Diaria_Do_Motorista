package com.example.diaria_do_motorista.sync.sync.sync

import SyncManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.diaria_do_motorista.data.db.remote.enums.status.SyncStatus
import javax.inject.Inject

class SyncWorker @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val syncManager: SyncManager
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            syncManager.startSync()

            // Loop de verificação baseado no seu Enum personalizado
            // Supondo que syncManager.syncState seja um StateFlow ou LiveData
            while (syncManager.syncState.value == SyncStatus.SYNCING) {
                kotlinx.coroutines.delay(500)
            }

            if (syncManager.syncState.value == SyncStatus.COMPLETED) {
                Result.success()
            } else {
                // Se falhou ou caiu em erro, tenta novamente mais tarde
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }

    }
}