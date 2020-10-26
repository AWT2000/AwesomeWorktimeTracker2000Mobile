package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApiService
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val userInfoDao: UserInfoDao,
    private val apiService: AWTApiService,
    private val connectionUtils: ConnectionUtils
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(UserRepository.getInstance(userInfoDao, apiService, connectionUtils)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}