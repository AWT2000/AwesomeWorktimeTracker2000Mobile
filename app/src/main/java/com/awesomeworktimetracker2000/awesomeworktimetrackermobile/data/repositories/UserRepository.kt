package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseUserInfo
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.UserInfo
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.auth.Credentials
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.auth.LoginResponseDto
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApiService
import kotlinx.coroutines.*
import java.lang.Exception

class UserRepository private constructor(
    private val userInfoDao: UserInfoDao,
    private val apiService: AWTApiService
) {

    companion object {
        private lateinit var instance: UserRepository

        fun getInstance(userInfoDao: UserInfoDao, apiService: AWTApiService): UserRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = UserRepository(userInfoDao, apiService)
                }
                return instance
            }
        }
    }

    private val _user = MutableLiveData<UserInfo?>()

    val user: LiveData<UserInfo?>
        get() = _user

    /**
     * Tries to fetch user info from db and validate it by making api request.
     * Updates live data.
     */
    suspend fun tryCache() {
        Log.i("login", "UserRepository@tryCache")
        val userFromCache = getUserFromCache()
        if (userFromCache != null) {
            val userFromApi = tryTokenValidity(userFromCache)
            if (userFromApi != null) {
                if (userFromApi.name != userFromCache.name
                    || userFromApi.email != userFromCache.email) {
                    updateCachedUser(userFromApi)
                } else {
                    _user.postValue(userFromCache)
                }
            } else {
                Log.i("login", "UserRepository@tryCache, userFromCache = null")
                _user.postValue(null)
            }
        } else {
            Log.i("login", "UserRepository@tryCache, userFromCache = null")
            _user.postValue(null)
        }
    }

    /**
     * Tries to get user info from database.
     * @return UserInfo from db | null
     */
    private suspend fun getUserFromCache(): UserInfo? {
        Log.i("login", "UserRepository@getUserFromCache")
        try {
            val userFromDb = userInfoDao.getUser()
            return if (userFromDb != null) {
                UserInfo(
                    name = userFromDb.name,
                    email = userFromDb.email,
                    accessToken = userFromDb.token,
                    id = userFromDb.id
                )
            } else {
                Log.i("login", "UserRepository@getUserFromCache user from db = null")
                null;
            }
        } catch (e: Exception) {
            Log.i("login", "UserRepository@getUserFromCache, error: " + e.message.toString())
            return null
        }
    }

    /**
     * Makes a request to web api to check if cached user info is valid.
     * If user from db was not valid, removes cached user info.
     * @return UserInfo from web api | null
     */
    private suspend fun tryTokenValidity(user: UserInfo, firstTry: Boolean = true): UserInfo? {
        Log.i("login", "UserRepository@tryTokenValidity")
        try {
            val response = apiService.getUser("Bearer " + user.accessToken)
            when {
                response.isSuccessful -> {
                    val userFromApi = response.body()!!
                    return UserInfo(
                        name = userFromApi.name,
                        email = userFromApi.email,
                        accessToken = user.accessToken,
                        id = userFromApi.id
                    )
                }
                response.code() == 401 -> {
                    Log.i("login", "UserRepository@tryTokenValidity, response.code() == 401")
                    userInfoDao.removeUser()
                    return null
                }
                firstTry -> {
                    delay(3000L)
                    return tryTokenValidity(user, false)
                }
                else -> {
                    return null
                }
            }
        } catch (e: Exception) {
            Log.i("login", "UserRepository@tryTokenValidity, error: " + e.message.toString())
            userInfoDao.removeUser()
            return null
        }
    }

    /**
     * Makes a HTTP POST request to web api and if api responses with user, caches user info.
     */
    suspend fun login(credentials: Credentials, firstTry: Boolean = true) {
        Log.i("login", "UserRepository@login")
        try {
            val response = apiService.postLogin(credentials);
            when {
                response.isSuccessful -> {
                    val loginResponseDto: LoginResponseDto = response.body()!!
                    updateCachedUser(UserInfo(
                        name = loginResponseDto.user.name,
                        email = loginResponseDto.user.email,
                        accessToken = loginResponseDto.access_token,
                        id = loginResponseDto.user.id
                    ))
                    Log.i("login", "UserRepository, loginResponseDto.user.name: " + loginResponseDto.user.name)
                }
                response.code() != 401 && firstTry -> {
                    delay(3000L)
                    login(credentials, false)
                }
                else -> {
                    _user.postValue(null)
                }
            }
        } catch (e: Exception) {
            Log.i("login", "UserRepository, error: " + e.message.toString())
            _user.postValue(null)
        }
    }

    /**
     * Updates user info in database and sets live data value
     */
    private suspend fun updateCachedUser(user: UserInfo) {
        Log.i("login", "UserRepository@updateCachedUser")
        try {
            userInfoDao.upsertUser(
                DatabaseUserInfo(
                    name = user.name,
                    email = user.email,
                    token = user.accessToken,
                    id = user.id
                )
            )
            _user.postValue(user)
        } catch (e: Exception) {
            Log.i("login", "UserRepository@updateCachedUser, error: " + e.message.toString())
            _user.postValue(null)
        }
    }

    fun onGetUserComplete() {
        _user.value = null
    }
}