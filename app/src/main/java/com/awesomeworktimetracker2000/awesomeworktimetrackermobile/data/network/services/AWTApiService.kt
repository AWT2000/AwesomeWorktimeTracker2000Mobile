package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.Credentials
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.auth.LoginResponseDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

private const val BASE_URL = "https://awesome-worktime-tracker-2000.herokuapp.com/api/v1/"
// private const val BASE_URL = "http://awesomeworktimetracker.test/api/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface AWTApiService {
    @Headers("Accepts: Application/json")
    @POST("auth/login")
    suspend fun postLogin(@Body credentials: Credentials): LoginResponseDto
}

object AWTApi {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val service = retrofit.create(AWTApiService::class.java)
}