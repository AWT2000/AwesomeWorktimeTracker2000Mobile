package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.UserInfo

data class LoginResponse(
    val status: ResponseStatus,
    val user: UserInfo? = null
)