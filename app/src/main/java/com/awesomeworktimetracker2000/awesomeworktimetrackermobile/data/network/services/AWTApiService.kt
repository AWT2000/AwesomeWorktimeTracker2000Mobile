package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.Credentials
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.auth.LoginResponseDto
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.auth.UserDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://awesome-worktime-tracker-2000.herokuapp.com/api/v1/"

interface AWTApiService {
    @Headers("Accepts: Application/json")
    @POST("auth/login")
    suspend fun postLogin(@Body credentials: Credentials): Response<LoginResponseDto>

    @Headers("Accepts: Application/json")
    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): Response<UserDto>
}

object AWTApi {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val service: AWTApiService = retrofit.create(AWTApiService::class.java)
}