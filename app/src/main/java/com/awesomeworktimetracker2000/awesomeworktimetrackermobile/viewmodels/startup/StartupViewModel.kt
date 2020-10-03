package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.startup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StartupViewModel() : ViewModel() {

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