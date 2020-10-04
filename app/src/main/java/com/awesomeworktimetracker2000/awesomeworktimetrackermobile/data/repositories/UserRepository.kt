package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.UserInfo
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.Credentials
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.auth.LoginResponseDto
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class UserRepository() {

    private val _user = MutableLiveData<UserInfo>()

    val user: LiveData<UserInfo>
        get() = _user

    suspend fun fetchUser() {

    }

    suspend fun login(credentials: Credentials) {
        Log.i("login", "UserRepository@login")
        withContext(Dispatchers.IO) {
            try {
                val loginResponseDto: LoginResponseDto = AWTApi.service.postLogin(credentials)
                _user.postValue(UserInfo(
                    loginResponseDto.user.name,
                    loginResponseDto.user.email,
                    loginResponseDto.access_token
                ))
                Log.i("login", "UserRepository, loginResponseDto.access_token: " + loginResponseDto.access_token)
            } catch (e: Exception) {
                Log.i("login", "UserRepository, error: " + e.message.toString())
            }
        }
    }
}