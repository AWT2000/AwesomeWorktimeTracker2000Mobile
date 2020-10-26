package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.date

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
<<<<<<< HEAD
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.AWTDatabase
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.WorktimeEntryDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApiService
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import kotlinx.coroutines.runBlocking
=======
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApiService
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
>>>>>>> created DateViewModel, DateViewModelFactory
import java.util.*

@Suppress("UNCHECKED_CAST")
class DateViewModelFactory (
<<<<<<< HEAD
    private val worktimeEntryDao: WorktimeEntryDao,
    private val userInfoDao: UserInfoDao,
    private val apiService: AWTApiService,
    private val connectionUtils: ConnectionUtils

=======
    private val date: Date,
    private val userInfoDao: UserInfoDao,
    private val apiService: AWTApiService,
    private val connectionUtils: ConnectionUtils,
    private val application: Application
>>>>>>> created DateViewModel, DateViewModelFactory
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DateViewModel::class.java)) {
<<<<<<< HEAD
            var weRepo: WorktimeEntryRepository

            runBlocking {
                weRepo = WorktimeEntryRepository.getInstance(
                    apiService,
                    worktimeEntryDao,
                    userInfoDao,
                    connectionUtils
                )
            }
            return DateViewModel(weRepo) as T

=======
            return DateViewModel(date, UserRepository.getInstance(userInfoDao, apiService, connectionUtils), application) as T
>>>>>>> created DateViewModel, DateViewModelFactory
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}