package com.example.diaria_do_motorista.sync.exeptions.sync

import android.content.Context
import android.provider.CalendarContract
import androidx.work.WorkManager
import com.example.diaria_do_motorista.data.db.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager  @Inject constructor(
    private  val context: Context,
    private val  apiService:ApiService,
    private val database: AppDatabase,
    private val preference:AppPreferences,
    private  val workManager: WorkManager
) {

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val _syncState = MutableStateFlow<SyncState>(SyncState.IDLE)
    val syncState: StateFlow<CalendarContract.SyncState> = _syncState.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()

    private val _syncProgress = MutableStateFlow(0f)
    val syncProgress: StateFlow<Float> = _syncProgress.asStateFlow()

    private val _syncError = MutableStateFlow<SyncError?>(null)
    val syncError: StateFlow<SyncError?> = _syncError.asStateFlow()

    init {
        _lastSyncTime.value = preferences.getLastSyncTime()
        schedulePeriodicSync()
    }
}