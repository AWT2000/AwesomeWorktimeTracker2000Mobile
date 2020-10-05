package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.startup

import android.util.Log
import androidx.lifecycle.*
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.UserInfo
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val user: LiveData<UserInfo?> = userRepository.user

    /**
     * Boolean flag that indicates if we have valid user data in cache or not.
     */
    val hasValidUserInfo: LiveData<Boolean?>
        get() = Transformations.map(user) {
            it != null
        }

    /**
     * Launches coroutine that tries to fetch user info from db and validate it with request to web api.
     * Updates live data.
     */
    fun tryLoginWithCache() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.tryCache()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("login", "StartupViewModel cleared")
    }
}