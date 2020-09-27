package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.login

import android.util.Log
import androidx.lifecycle.*
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.entities.UserInfo
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.Credentials
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepository()

    val userInfo: LiveData<UserInfo>
        get() = userRepository.user

    val canContinue = Transformations.map(userInfo) {
        it != null
    }

    private val _loginButtonEnabled = MutableLiveData<Boolean?>()

    val loginButtonEnabled: LiveData<Boolean?>
        get() = _loginButtonEnabled


    fun tryLogin(email: String, password: String) {
        viewModelScope.launch {
            Log.i("login", "LoginViewModel@tryLogin")
            userRepository.login(Credentials(email, password))
        }
    }

    fun enableLoginButton() {
        _loginButtonEnabled.value = true
    }

    fun onLoginComplete() {
        _loginButtonEnabled.value = null
    }
}