package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.login

import android.util.Log
import androidx.lifecycle.*
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.UserInfo
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.auth.Credentials
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginButtonEnabled = MutableLiveData<Boolean?>()

    /**
     * Boolean flag indicating if we should enable login button.
     */
    val loginButtonEnabled: LiveData<Boolean?>
        get() = _loginButtonEnabled

    private val _responseStatus = MutableLiveData<ResponseStatus?>()

    /**
     * Response status from login request.
     *
     * Returns: ResponseStatus.OK|ResponseStatus.UNAUTHORIZED|ResponseStatus.UNDEFINEDERROR
     */
    val responseStatus: LiveData<ResponseStatus?>
        get() = _responseStatus

    /**
     * Launches coroutine that makes HTTP POST request to web API. Updates response status live data.
     */
    fun tryLogin(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("login", "LoginViewModel@tryLogin")
            val loginResponse = userRepository.login(
                Credentials(
                    email,
                    password
                )
            )

            _responseStatus.postValue(loginResponse.status)
        }
    }

    fun enableLoginButton() {
        _loginButtonEnabled.value = true
    }

    fun onLoginComplete() {
        _loginButtonEnabled.value = null
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("login", "LoginViewModel cleared")
    }
}