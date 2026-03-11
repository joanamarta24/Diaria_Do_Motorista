package com.example.diaria_do_motorista.data.db.domain

import com.example.diaria_do_motorista.data.db.remote.enums.EntityType
import com.example.diaria_do_motorista.data.db.remote.enums.status.SyncPriority
import com.example.diaria_do_motorista.data.db.remote.enums.status.SyncStatus
import java.util.Date

data class SyncStatusInfo(
    val id: String,
    val entityType: EntityType,
    val status: SyncStatus,
    val lastSyncAttempt: Date? = null,
    val lastSuccessfulSync: Date? = null,
    val nextSyncAttempt: Date? = null,
    val retryCount: Int = 0,
    val errorMessage: String? = null,
    val errorCode: String? = null,
    val details: Map<String, Any>? = null,
    val syncVersion: Long = 0,
    val syncPriority: SyncPriority = SyncPriority.NORMAL,
    val syncMetadata: SyncMetadata? = null
)