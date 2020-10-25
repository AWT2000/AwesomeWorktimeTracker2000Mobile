package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.startup

import androidx.lifecycle.*
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _hasValidUserInfo = MutableLiveData<Boolean>()

    /**
     * Boolean flag that indicates if we have valid user data in cache or not.
     */
    val hasValidUserInfo: LiveData<Boolean?>
        get() = _hasValidUserInfo

    /**
     * Launches coroutine that tries to fetch user info from db and validate it with request to web api.
     * Updates live data.
     */
    fun tryLoginWithCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val loginResponse = userRepository.login()
            if (loginResponse.status == ResponseStatus.OK
                || loginResponse.status == ResponseStatus.OFFLINE)
            {
                _hasValidUserInfo.postValue(true)
            } else {
                _hasValidUserInfo.postValue(false)
            }
        }
    }

    // TODO: create fun that starts coroutine that will sync work time entries
}