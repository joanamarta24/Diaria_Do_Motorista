import android.content.Context
import android.provider.ContactsContract
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.diaria_do_motorista.data.db.remote.enums.ErrorType
import com.example.diaria_do_motorista.data.db.worker.UsuarioSyncWorker
import com.example.diaria_do_motorista.data.preferences.AppPreferences
import com.google.firebase.appdistribution.gradle.ApiService
import com.seuapp.data.local.AppDatabase
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Singleton
class SyncManager @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val preferences: AppPreferences,  // Renomeado para preferences (plural)
    private val workManager: WorkManager
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())  // SupervisorJob é importante

    private val _syncState = MutableStateFlow<ContactsContract.SyncState>(ContactsContract.SyncState.IDLE)
    val syncState: StateFlow<ContactsContract.SyncState> = _syncState.asStateFlow()  // Corrigido o tipo

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()

    private val _syncProgress = MutableStateFlow(0f)
    val syncProgress: StateFlow<Float> = _syncProgress.asStateFlow()

    private val _syncError = MutableStateFlow<SyncError?>(null)
    val syncError: StateFlow<SyncError?> = _syncError.asStateFlow()

    init {
        // Inicializa com o último sync salvo
        _lastSyncTime.value = preferences.getLastSyncTime()

        // Agenda sincronização periódica
        schedulePeriodicSync()
    }

    /**
     * Inicia sincronização manual
     */
    fun startSync(force: Boolean = false) {
        scope.launch {
            try {
                _syncState.value = ContactsContract.SyncState.SYNCING
                _syncError.value = null
                _syncProgress.value = 0f

                // Executa sincronização
                performSync(force)

                _syncState.value = ContactsContract.ProfileSyncState.SUCCESS
                _lastSyncTime.value = System.currentTimeMillis()
                preferences.saveLastSyncTime(_lastSyncTime.value!!)

            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    /**
     * Executa a sincronização propriamente dita
     */
    private suspend fun performSync(force: Boolean) {
        // Atualiza progresso
        updateProgress(0.2f)

        // Sincroniza usuários
        syncUsuarios()
        updateProgress(0.4f)

        // Sincroniza diárias
        syncDiarias()
        updateProgress(0.6f)

        // Sincroniza transportadoras
        syncTransportadoras()
        updateProgress(0.8f)

        // Sincroniza veículos
        syncVeiculos()
        updateProgress(1.0f)
    }

    private suspend fun syncUsuarios() {
        try {
            val response = apiService.getUsuarios()
            if (response.isSuccessful) {
                response.body()?.let { usuarios ->
                    database.usuarioDao().inserirTodos(usuarios)
                }
            }
        } catch (e: Exception) {
            throw SyncException("Erro ao sincronizar usuários", e)
        }
    }

    private suspend fun syncDiarias() {
        // Implementar
    }

    private suspend fun syncTransportadoras() {
        // Implementar
    }

    private suspend fun syncVeiculos() {
        // Implementar
    }

    private fun updateProgress(value: Float) {
        _syncProgress.value = value
    }

    private fun handleError(e: Exception) {
        val error = when (e) {
            is java.net.UnknownHostException -> SyncError(
                message = "Sem conexão com a internet",
                type = ErrorType.NETWORK
            )
            is java.net.SocketTimeoutException -> SyncError(
                message = "Tempo limite excedido",
                type = ErrorType.NETWORK
            )
            is retrofit2.HttpException -> {
                when (e.code()) {
                    401 -> SyncError("Não autorizado", ErrorType.AUTH)
                    403 -> SyncError("Acesso negado", ErrorType.AUTH)
                    500 -> SyncError("Erro no servidor", ErrorType.SERVER)
                    else -> SyncError("Erro HTTP: ${e.code()}", ErrorType.UNKNOWN)
                }
            }
            else -> SyncError(
                message = e.message ?: "Erro desconhecido",
                type = ErrorType.UNKNOWN
            )
        }

        _syncError.value = error
        _syncState.value = ContactsContract.SyncState.ERROR
    }

    /**
     * Agenda sincronização periódica usando WorkManager
     */
    fun schedulePeriodicSync(intervalHours: Long = 4) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<UsuarioSyncWorker>(
            intervalHours, TimeUnit.HOURS,
            15, TimeUnit.MINUTES // Flexibilidade
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "periodic_sync",
            ExistingPeriodicWorkPolicy.KEEP, // ou UPDATE se quiser atualizar
            syncRequest
        )
    }

    /**
     * Cancela sincronizações agendadas
     */
    fun cancelScheduledSync() {
        workManager.cancelUniqueWork("periodic_sync")
    }

    /**
     * Força sincronização imediata via Worker
     */
    fun forceImmediateSync() {
        val workRequest = OneTimeWorkRequestBuilder<UsuarioSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                workDataOf("force_sync" to true)
            )
            .build()

        workManager.enqueue(workRequest)
    }

    /**
     * Limpa recursos quando não for mais necessário
     */
    fun cleanup() {
        scope.cancel()
    }
}

// Exceção personalizada para sincronização
class SyncException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)