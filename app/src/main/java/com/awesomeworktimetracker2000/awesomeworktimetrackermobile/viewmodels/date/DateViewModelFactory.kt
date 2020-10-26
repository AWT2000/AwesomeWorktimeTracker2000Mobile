package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.date

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApiService
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import java.util.*

@Suppress("UNCHECKED_CAST")
class DateViewModelFactory (
    private val date: Date,
    private val userInfoDao: UserInfoDao,
    private val apiService: AWTApiService,
    private val connectionUtils: ConnectionUtils,
    private val application: Application
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DateViewModel::class.java)) {
            return DateViewModel(date, UserRepository.getInstance(userInfoDao, apiService, connectionUtils), application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}