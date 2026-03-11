package com.example.diaria_do_motorista.data.db.worker

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.example.diaria_do_motorista.R
import com.example.diaria_do_motorista.data.db.repository.DiariaRepository
import com.example.diaria_do_motorista.data.db.repository.TransportadoraRepository
import com.example.diaria_do_motorista.data.db.repository.UsuarioRepository
import com.example.diaria_do_motorista.sync.sync.SyncException
import com.example.diaria_do_motorista.sync.sync.sync.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout


@HiltWorker
class UsuarioSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val usuarioRepository: UsuarioRepository,
    private val diariaRepository: DiariaRepository,
    private val transportadoraRepository: TransportadoraRepository,
    private val syncManager: SyncManager,
    private val networkMonitor: NetworkMonitor
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "UsuarioSyncWorker"
        const val WORK_NAME = "usuario_sync_work"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "sync_channel"

        // Constantes para configuração do Worker
        const val KEY_FORCE_SYNC = "force_sync"
        const val KEY_SYNC_TYPE = "sync_type"

        // Tipos de sincronização
        const val SYNC_ALL = "all"
        const val SYNC_USERS_ONLY = "users_only"
        const val SYNC_PENDING = "pending_only"

        fun createWorkRequest(
            forceSync: Boolean = false,
            syncType: String = SYNC_ALL,
            delayMinutes: Long = 0
        ): OneTimeWorkRequest {
            val inputData = Data.Builder()
                .putBoolean(KEY_FORCE_SYNC, forceSync)
                .putString(KEY_SYNC_TYPE, syncType)
                .build()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            return OneTimeWorkRequestBuilder<UsuarioSyncWorker>()
                .setInputData(inputData)
                .setConstraints(constraints)
                .addTag(TAG)
                .apply {
                    if (delayMinutes > 0) {
                        setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                    }
                }
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .build()
        }
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Verificar conexão com internet
                if (!networkMonitor.isConnected()) {
                    return@withContext Result.retry()
                }

                val forceSync = inputData.getBoolean(KEY_FORCE_SYNC, false)
                val syncType = inputData.getString(KEY_SYNC_TYPE) ?: SYNC_ALL

                // Registrar início da sincronização
                syncManager.logSyncStart("UsuarioSyncWorker", syncType)

                // Executar sincronizações baseadas no tipo
                when (syncType) {
                    SYNC_USERS_ONLY -> syncUsuariosPendentes(forceSync)
                    SYNC_PENDING -> syncAllPending(forceSync)
                    else -> syncAll(forceSync)
                }

                // Registrar sucesso
                syncManager.logSyncSuccess("UsuarioSyncWorker")
                createNotification("Sincronização concluída", "Dados sincronizados com sucesso")

                Result.success()

            } catch (e: Exception) {
                // Registrar erro
                syncManager.logSyncError("UsuarioSyncWorker", e)

                // Verificar se é um erro de rede
                if (isNetworkError(e)) {
                    return@withContext Result.retry()
                }

                // Para outros erros, marcar como falha após 3 tentativas
                if (runAttemptCount >= 3) {
                    createNotification("Erro na sincronização", "Falha ao sincronizar dados")
                    Result.failure()
                } else {
                    Result.retry()
                }
            }
        }
    }

    private suspend fun syncAll(forceSync: Boolean = false) {
        // 1. Sincroniza usuários pendentes
        syncUsuariosPendentes(forceSync)

        // 2. Sincroniza diárias pendentes
        syncDiariasPendentes(forceSync)

        // 3. Sincroniza transportadoras e veículos
        syncTransportadorasEVeiculos(forceSync)

        // 4. Sincroniza outras entidades relacionadas
        syncEntidadesRelacionadas(forceSync)
    }

    private suspend fun syncAllPending(forceSync: Boolean = false) {
        val pendingOperations = listOf(
            { syncUsuariosPendentes(forceSync) },
            { syncDiariasPendentes(forceSync) },
            { syncTransportadorasEVeiculos(forceSync) }
        )

        // Executa operações pendentes em paralelo
        pendingOperations.map { operation ->
            kotlinx.coroutines.async { operation() }
        }.forEach { it.await() }
    }

    private suspend fun syncUsuariosPendentes(forceSync: Boolean = false) {
        try {
            // Busca usuários pendentes de sincronização
            val usuariosPendentes = usuarioRepository.getPendingSyncUsers()

            if (usuariosPendentes.isNotEmpty() || forceSync) {
                // Sincroniza com API
                val result = usuarioRepository.syncWithApi(usuariosPendentes, forceSync)

                if (result.isSuccess) {
                    // Atualiza status local
                    usuarioRepository.markAsSynced(usuariosPendentes.map { it.id })

                    // Log de sucesso
                    syncManager.logOperationSuccess("usuarios", usuariosPendentes.size)
                } else {
                    throw SyncException("Falha ao sincronizar usuários: ${result.errorMessage}")
                }
            }
        } catch (e: Exception) {
            syncManager.logOperationError("usuarios", e)
            throw e
        }
    }

    private suspend fun syncDiariasPendentes(forceSync: Boolean = false) {
        try {
            // 1. Verificar conexão antes de começar
            if (!networkMonitor.isConnected()) {
                throw SyncException("Sem conexão com a internet")
            }

            // 2. Busca diárias pendentes com timeout
            val diariasPendentes = withTimeout(30_000L) {
                diariaRepository.getPendingDiarias()
            }

            // 3. Lógica melhorada para forceSync
            val diariasParaSincronizar = when {
                diariasPendentes.isNotEmpty() -> diariasPendentes
                forceSync -> {
                    // Se for forceSync mas não há pendentes, busca todas
                    diariaRepository.getAllDiariasForSync()
                }
                else -> {
                    // Nada para sincronizar
                    syncManager.logOperationInfo("diarias", "Nenhuma diária pendente para sincronizar")
                    return
                }
            }

            if (diariasParaSincronizar.isEmpty()) {
                syncManager.logOperationInfo("diarias", "Nenhuma diária para sincronizar")
                return
            }

            // 4. Sincroniza com timeout
            val result = withTimeout(60_000L) {
                diariaRepository.syncDiarias(diariasParaSincronizar, forceSync)
            }

            // 5. Tratamento mais robusto do resultado
            when {
                result.isSuccess && result.syncedCount > 0 -> {
                    // Marca APENAS as que foram sincronizadas com sucesso
                    val idsSincronizados = result.getSyncedIds() ?: diariasParaSincronizar.map { it.id }
                    diariaRepository.markDiariasAsSynced(idsSincronizados)

                    syncManager.logOperationSuccess(
                        "diarias",
                        result.syncedCount,
                        "Diárias sincronizadas: ${result.syncedCount}"
                    )
                }

                result.isSuccess && result.syncedCount == 0 -> {
                    syncManager.logOperationInfo(
                        "diarias",
                        "Nenhuma diária necessitava sincronização"
                    )
                }

                else -> {
                    // Log detalhado do erro
                    val errorMsg = result.errorMessage ?: "Erro desconhecido ao sincronizar diárias"
                    syncManager.logSyncError("diarias", SyncException(errorMsg))

                    // Se for erro específico, marca para retentar depois
                    if (result.shouldRetry()) {
                        diariaRepository.markForRetry(diariasParaSincronizar.map { it.id })
                    }

                    throw SyncException("Falha ao sincronizar diárias: $errorMsg")
                }
            }

        } catch (e: TimeoutCancellationException) {
            syncManager.logOperationError("diarias", SyncException("Timeout na sincronização de diárias"))
            throw SyncException("Timeout ao sincronizar diárias: ${e.message}")

        } catch (e: Exception) {
            syncManager.logOperationError("diarias", e)

            // Se for erro de rede, relança para o worker tratar
            if (isNetworkError(e)) {
                throw e
            }

            // Outros erros: log e relança com mensagem específica
            throw SyncException("Erro ao sincronizar diárias: ${e.localizedMessage ?: "Erro desconhecido"}")
        }
    }

    private suspend fun syncTransportadorasEVeiculos(forceSync: Boolean = false) {
        try {
            // Sincroniza transportadoras
            val transportadorasResult = transportadoraRepository.syncTransportadoras(forceSync)
            if (!transportadorasResult.isSuccess) {
                throw SyncException("Falha transportadoras: ${transportadorasResult.errorMessage}")
            }

            // Sincroniza veículos
            val veiculosResult = transportadoraRepository.syncVeiculos(forceSync)
            if (!veiculosResult.isSuccess) {
                throw SyncException("Falha veículos: ${veiculosResult.errorMessage}")
            }

            syncManager.logOperationSuccess("transportadoras_veiculos",
                transportadorasResult.syncedCount + veiculosResult.syncedCount)
        } catch (e: Exception) {
            syncManager.logOperationError("transportadoras_veiculos", e)
            throw e
        }
    }

    private suspend fun syncEntidadesRelacionadas(forceSync: Boolean = false) {
        // Implementação opcional para outras entidades
    }

    private fun createNotification(title: String, message: String) {
        // Implementação de notificação (opcional)
    }

    private fun isNetworkError(exception: Exception): Boolean {
        return exception is java.net.UnknownHostException ||
                exception is java.net.SocketTimeoutException ||
                exception is java.io.IOException
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        // Implementação para foreground service (Android 12+)
        return ForegroundInfo(
            NOTIFICATION_ID,
            createSyncNotification("Sincronizando dados...")
        )
    }

    private fun createSyncNotification(message: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Sincronização em andamento")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_sync)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}