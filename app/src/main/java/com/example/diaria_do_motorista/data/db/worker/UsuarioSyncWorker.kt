package com.example.diaria_do_motorista.data.db.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import com.example.diaria_do_motorista.data.db.repository.DiariaRepository
import com.example.diaria_do_motorista.data.db.repository.TransportadoraRepository
import com.example.diaria_do_motorista.data.db.repository.UsuarioRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UsuarioSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val  usuarioRepository: UsuarioRepository,
    private val diariaRepository: DiariaRepository,
    private val transportadoraRepository: TransportadoraRepository,
    private val syncManager: SyncManager,
    private val networkMonitor: NetworkMonitor

):CoroutineWorker(context,params){
  companion object{
      private const val TAG ="UsuarioSyncWorker"
      const val WORK_NAME = "usuario_sync_work"

      //COnstates para configuração do Worker
      const val KEY_FORCE_SYNC = "force_sync"
      const val KEY_SYNC_TYPE = "sync-type"


      // Tipos de sincronização
      const val SYNC_ALL = "all"
      const val SYNC_USERS_ONLY = "users_only"
      const val SYNC_PENDING = "pending_only"

      fun creteWorkRequest(
          forceSync:Boolean = false,
          syncType:String = SYNC_ALL,
          delayMinutes:Long = 0
      ): OneTimeWorkRequest {
          val inputData = Data.Builder()
              .putBoolean(KEY_FORCE_SYNC, forceSync)
              .putString(KEY_SYNC_TYPE, syncType)
              .build()
  }
}