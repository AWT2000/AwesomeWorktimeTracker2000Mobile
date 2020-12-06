package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.ProjectRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.services.DataSyncService
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val worktimeEntryRepository: WorktimeEntryRepository,
    private val projectRepository: ProjectRepository,
    private val connectionUtils: ConnectionUtils
) : ViewModel() {

    private var dataSyncService: DataSyncService = DataSyncService.getInstance(
        worktimeEntryRepository,
        projectRepository,
        connectionUtils)

    /**
     * Call data sync service to sync data between local db and cloud service.
     */
    fun syncData() {
        viewModelScope.launch(Dispatchers.IO) {
            dataSyncService.syncUnsyncedWorktimeEntries()
            dataSyncService.syncWorktimeEntriesFromApiToLocalDb()
            dataSyncService.syncProjects()
        }
    }
}