package com.example.diaria_do_motorista.data.db.db.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.diaria_do_motorista.data.db.db.repository.DiariaRepository
import com.example.diaria_do_motorista.data.db.db.repository.TransportadoraRepository
import com.example.diaria_do_motorista.data.db.db.repository.UsuarioRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UsuarioSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val  usuarioRepository:UsuarioRepository,
    private val diariaRepository: DiariaRepository,
    private val transportadoraRepository: TransportadoraRepository,
    private val syncManager: SyncManager,
    private val networkMonitor: NetworkMonitor

):CoroutineWorker(context,params){
    return try{

    }

    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}