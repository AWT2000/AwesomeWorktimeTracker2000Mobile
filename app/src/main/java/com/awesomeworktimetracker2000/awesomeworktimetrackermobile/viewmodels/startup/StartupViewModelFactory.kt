package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApiService
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class StartupViewModelFactory(
    private val userInfoDao: UserInfoDao,
    private val apiService: AWTApiService
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartupViewModel::class.java)) {
            return StartupViewModel(UserRepository.getInstance(userInfoDao, apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}