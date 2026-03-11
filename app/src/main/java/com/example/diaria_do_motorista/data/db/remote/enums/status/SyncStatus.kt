package com.example.diaria_do_motorista.data.db.remote.enums.status

enum class SyncStatus {
    SYNCED,
    PENDING,      // Aguardando sincronização
    SYNCING,      // Em processo de sincronização
    FAILED,       // Falha na sincronização
    PARTIAL,      // Sincronizado parcialmente
    CONFLICT,     // Conflito detectado
    CANCELLED,    // Sincronização cancelada
    RETRYING,     // Tentando novamente
    OFFLINE,      // Offline - não sincronizado
    DELETED;      // Marcado para exclusão


    companion object {
        fun fromString(status: String): SyncStatus {
            return values().find { it.name == status.uppercase() } ?: PENDING
        }

        fun getActiveStatuses(): List<SyncStatus> {
            return listOf(PENDING, SYNCING, RETRYING, PARTIAL)
        }

        fun getCompletedStatuses(): List<SyncStatus> {
            return listOf(SYNCED, FAILED, CONFLICT, CANCELLED, DELETED)
        }
    }

    fun isActive(): Boolean {
        return this in listOf(PENDING, SYNCING, RETRYING, PARTIAL)
    }

    fun isCompleted(): Boolean {
        return this in listOf(SYNCED, FAILED, CONFLICT, CANCELLED, DELETED)
    }

    fun isSuccessful(): Boolean {
        return this == SYNCED
    }

    fun needsRetry(): Boolean {
        return this in listOf(FAILED, CONFLICT, PARTIAL)
    }
}