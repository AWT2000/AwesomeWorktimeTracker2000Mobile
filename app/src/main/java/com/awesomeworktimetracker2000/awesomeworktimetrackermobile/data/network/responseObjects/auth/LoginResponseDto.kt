package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.auth

data class LoginResponseDto(
    val access_token: String,
    val user: UserDto
)