package com.example.diaria_do_motorista.data.db.domain

import java.util.Date

data class SyncSummary(
    val totalItems:Int,
    val  syncedItems: Int,
    val pendingItems: Int,
    val failedItems: Int,
    val inProgress: Int,
    val syncStartTime: Date? = null,
    val syncEndTime: Date? = null,
    val duration: Long? = null, // em milissegundos
    val dataTransferred: Long? = null, // em bytes
    val successRate: Double = 0.0
){
    val isComplete: Boolean
        get() = pendingItems == 0 && inProgress == 0

    val progress: Float
        get() = if (totalItems > 0) {
            (syncedItems + failedItems).toFloat() / totalItems
        } else 0f
}
sealed class SyncResult{
    object Success : SyncResult()
    data class PartialSuccess(val synced: Int, val failed: Int) : SyncResult()
    data class Failure(val error: SyncEror) : SyncResult()
    object Cancelled : SyncResult()
    data class Conflict(val conflictingItems: List<String>) : SyncResult()
}

