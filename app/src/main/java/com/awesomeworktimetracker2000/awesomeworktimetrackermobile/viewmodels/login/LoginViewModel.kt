package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.login

import android.util.Log
import androidx.lifecycle.*
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.UserInfo
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.auth.Credentials
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val userInfo: LiveData<UserInfo?>
        get() = userRepository.user

    val canContinue = Transformations.map(userInfo) {
        it != null
    }

    private val _loginButtonEnabled = MutableLiveData<Boolean?>()

    val loginButtonEnabled: LiveData<Boolean?>
        get() = _loginButtonEnabled

    /**
     * Launches coroutine that makes HTTP POST request to web API
     */
    fun tryLogin(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("login", "LoginViewModel@tryLogin")
            userRepository.login(
                Credentials(
                    email,
                    password
                )
            )
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