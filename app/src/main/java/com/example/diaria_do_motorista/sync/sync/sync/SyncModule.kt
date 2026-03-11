package com.example.diaria_do_motorista.sync.sync.sync

import SyncManager
import android.util.Log
import com.example.diaria_do_motorista.data.db.di.SyncWorkInterval
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @SyncWorkInterval
    fun provideSyncWorkInterval(): Long = 4 // horas

    @Provides
    @Singleton
    fun provideSyncManager(): SyncManager {
        return object : SyncManager {
            override fun logSyncStart(workerName: String, syncType: String) {
                Log.d("SyncManager", "Iniciando $workerName - Tipo: $syncType")
            }

            override fun logSyncSuccess(workerName: String) {
                Log.d("SyncManager", "Sucesso em $workerName")
            }

            override fun logSyncError(workerName: String, exception: Exception) {
                Log.e("SyncManager", "Erro em $workerName", exception)
            }

            override fun logOperationSuccess(operation: String, count: Int, details: String?) {
                Log.d("SyncManager", "$operation - Sucesso: $count itens")
            }

            override fun logOperationError(operation: String, exception: Exception) {
                Log.e("SyncManager", "Erro em $operation", exception)
            }

            override fun logOperationInfo(operation: String, message: String) {
                Log.d("SyncManager", "$operation - $message")
            }
        }
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(): NetworkMonitor {
        return object : NetworkMonitor {
            override fun isConnected(): Boolean {
                // Implemente com ConnectivityManager
                return true // Temporário
            }
        }
    }
}