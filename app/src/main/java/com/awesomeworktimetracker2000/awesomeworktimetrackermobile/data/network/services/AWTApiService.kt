package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.auth.Credentials
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.worktimeEntries.SaveWorktimeEntryRequest
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.auth.LoginResponseDto
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.auth.UserDto
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.worktimeEntries.listing.worktimeEntryListingResponseDto
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.worktimeEntries.save.SaveWorktimeEntryResponseDto
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://awesome-worktime-tracker-2000.herokuapp.com/api/v1/"

interface AWTApiService {
    @Headers("Accepts: Application/json")
    @POST("auth/login")
    /**
     * Makes a HTTP POST request to web api for authentication.
     * Returns User info from server if request is successful.
     *
     * For example:
     * ```
     * val response = service.postLogin(Credentials("my@e.mail", "password"))
     * ```
     */
    suspend fun postLogin(@Body credentials: Credentials): Response<LoginResponseDto>

    @Headers("Accepts: Application/json")
    @GET("user")
    /**
     * Makes a HTTP GET request to web api for getting user info.
     * Returns User info from server if request is successful.
     *
     * For example:
     * ```
     * val response = service.getUser(token = "Bearer token.from.api")
     * ```
     */
    suspend fun getUser(@Header("Authorization") token: String): Response<UserDto>

    @Headers("Accepts: Application/json")
    @GET("worktime-entries")
    /**
     * Makes a HTTP GET request to web api for work time entries between dates.
     *
     * For example:
     * ```
     * val response = service.getWorktimeEntries(
     *      token = "Bearer token.from.api",
     *      startedAt = "2020-01-01",
     *      endedAt = "2020-12-31"
     * )
     * ```
     */
    suspend fun getWorktimeEntries(
        @Header("Authorization") token: String,
        @Query("started_at") startedAt: String,
        @Query("ended_at") endedAt: String
    ): Response<worktimeEntryListingResponseDto>

    @Headers("Accepts: Application/json")
    @POST("worktime-entries")
    /**
     * Makes a HTTP POST request to web api for creating new work time entry.
     * Return SaveWorktimeEntryResponseDto object if request is successful.
     *
     * For example:
     * ```
     * val response = service.addWorktimeEntry(
     *      token = "Bearer token.from.api",
     *      worktimeEntry = SaveWorktimeEntryRequest(
     *          project_id = 1,
     *          started_at = "2020-01-01T08:00:00+02:00",
     *          ended_at = "2020-01-01T16:00:00+02:00"
     *      )
     * )
     * ```
     */
    suspend fun addWorktimeEntry(
        @Header("Authorization") token: String,
        @Body worktimeEntry: SaveWorktimeEntryRequest
    ): Response<SaveWorktimeEntryResponseDto>

    @Headers("Accepts: Application/json")
    @PUT("worktime-entries/{id}")
    /**
     * Makes a HTTP PUT request to web api to update work time entry with specified id
     * Returns SaveWorktimeEntryResponseDto with updated values if request is successful
     *
     * For example:
     * ```
     * val response = apiService.updateWorktimeEntry(
     *   token = "Bearer token.from.api",
     *   id = 111,
     *   worktimeEntry = SaveWorktimeEntryRequest(
     *     project_id = 1,
     *     started_at = "2020-01-01T08:00:00+02:00",
     *     ended_at = "2020-01-01T16:00:00+02:00"
     *   )
     * )
     * ```
     */
    suspend fun updateWorktimeEntry(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body worktimeEntry: SaveWorktimeEntryRequest
    ): Response<SaveWorktimeEntryResponseDto>

    @Headers("Accepts: Application/json")
    @DELETE("worktime-entries/{id}")
    /**
     * Makes a HTTP DELETE request to api to delete work time entry with specified id.
     *
     * For example
     * ```
     * val response = apiService.deleteWorktimeEntry(
     *   token = "Bearer token.from.api",
     *   id = 111
     * )
     * ```
     */
    suspend fun deleteWorktimeEntry(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}

object AWTApi {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val service: AWTApiService = retrofit.create(AWTApiService::class.java)
}