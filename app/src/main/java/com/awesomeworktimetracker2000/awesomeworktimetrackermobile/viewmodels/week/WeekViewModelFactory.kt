package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.WorktimeEntryDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApiService
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import kotlinx.coroutines.runBlocking

@Suppress("UNCHECKED_CAST")
class WeekViewModelFactory (
    private val worktimeEntryDao: WorktimeEntryDao,
    private val userInfoDao: UserInfoDao,
    private val apiService: AWTApiService,
    private val connectionUtils: ConnectionUtils

): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeekViewModel::class.java)) {
            var worktimeEntryRepository: WorktimeEntryRepository

            runBlocking {
                worktimeEntryRepository = WorktimeEntryRepository.getInstance(
                    apiService,
                    worktimeEntryDao,
                    userInfoDao,
                    connectionUtils
                )
            }
            return WeekViewModel(worktimeEntryRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}