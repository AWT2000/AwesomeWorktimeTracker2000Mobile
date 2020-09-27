package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.startup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.entities.UserInfo
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository

class StartupViewModel : ViewModel() {

    // private val UserRepository = UserRepository(AWTApi.retrofitService)

    private val _hasValidUserInfo = MutableLiveData<Boolean?>()

    val hasValidUserInfo: LiveData<Boolean?>
        get() = _hasValidUserInfo

    fun tryLogin() {
        _hasValidUserInfo.value = false
    }

    private suspend fun onCheckCache() {

    }

    fun onTryLoginComplete() {
        _hasValidUserInfo.value = null
    }
}