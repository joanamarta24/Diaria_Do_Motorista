package com.example.diaria_do_motorista.data.db.domain

import com.example.diaria_do_motorista.data.db.remote.enums.status.SyncSource
import java.util.Date

data class SyncMetadata(
    val createBy: String? = null,
    val createAt: Date = Date(),
    val modifieBy: String? = null,
    val modifiedAt: Date? = null,
    val deviceId: String? = null,
    val userId: String? = null,
    val appVersion: String? = null,
    val syncSource: SyncSource = SyncSource.LOCAL
)
