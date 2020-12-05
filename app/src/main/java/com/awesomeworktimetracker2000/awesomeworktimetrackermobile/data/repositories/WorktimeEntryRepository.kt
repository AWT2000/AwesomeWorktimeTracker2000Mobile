package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories

import android.util.Log
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.WorktimeEntryDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseWorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApiService
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.WorktimeEntryListing
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.WorktimeEntryResponse
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils.localOffset
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Repository for work time entries.
 */
class WorktimeEntryRepository private constructor (
    private val apiService: AWTApiService,
    private val worktimeEntryDao: WorktimeEntryDao,
    private val token: String,
    private val connectionUtils: ConnectionUtils
) {

    companion object {
        @Volatile private lateinit var instance: WorktimeEntryRepository
        @Volatile private lateinit var token: String
        @Volatile private var userId: Int = 0

        /**
         * Returns singleton instance of WorktimeEntryRepository.
         */
        suspend fun getInstance(
            apiService: AWTApiService,
            worktimeEntryDao: WorktimeEntryDao,
            userInfoDao: UserInfoDao,
            connectionUtils: ConnectionUtils): WorktimeEntryRepository {

            Mutex().withLock {
                if (!::token.isInitialized) {
                    val user = userInfoDao.getUser()!!
                    token = user.token
                    userId = user.id
                }

                if (!::instance.isInitialized) {
                    instance = WorktimeEntryRepository(
                        apiService,
                        worktimeEntryDao,
                        token,
                        connectionUtils)
                }

                return instance
            }
        }
    }

    // formatter for iso dates
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")

    // formatter for query param dates
    private val simpleDateFormatter = SimpleDateFormat("yyyy-MM-dd")

    /**
     * Get work time entry listing by date.
     *
     * @param date date object
     *
     * @return WorktimeEntryListing with list of entries and response status
     */
    suspend fun getWorktimeEntriesByDate(date: Date = Date()): WorktimeEntryListing {
        val localDate = date.toInstant().atZone(DateUtils.ZoneId).toLocalDate()
        val start = localDate.atTime(OffsetTime.MIN).toString()
        val end = localDate.plusDays(1).atTime(OffsetTime.MIN).toString()

        return if (connectionUtils.hasInternetConnection()) {
            syncWorktimeEntriesBetweenDateTimes(date, start, end)
        } else {
            getCachedWorktimeEntriesBetweenDateTimes(start, end)
        }
    }

    /**
     * Fetch work time entries from database between given date times.
     *
     * @param start date time string in yyyy-MM-dd'T'HH:mm:ssxxx format
     * @param end date time string in yyyy-MM-dd'T'HH:mm:ssxxx format
     *
     * @return WorktimeEntryListing with list of entries and response status
     */
    suspend fun getCachedWorktimeEntriesBetweenDateTimes(start: String, end: String): WorktimeEntryListing {
        return try {
            WorktimeEntryListing(
                status = ResponseStatus.OFFLINE,
                worktimeEntries = worktimeEntryDao
                    .getWorktimeEntriesBetweenDates(userId = userId, start = start, end = end)
                    .map { entryFromDb ->
                        WorktimeEntry(
                            id = entryFromDb.id,
                            startedAt =  entryFromDb.startedAt,
                            endedAt = entryFromDb.endedAt,
                            externalId = entryFromDb.externalId,
                            synced = entryFromDb.synced,
                            projectId = entryFromDb.projectId
                        )
                    })
        } catch (e: Exception) {
            return WorktimeEntryListing(ResponseStatus.DBERROR)
        }
    }

    /**
     * Fetches work time entries from api between given date times.
     *
     * @param start date time string in yyyy-MM-dd'T'HH:mm:ssxxx format
     * @param end date time string in yyyy-MM-dd'T'HH:mm:ssxxx format
     *
     * @return WorktimeEntryListing with list of entries and response status
     */
    private suspend fun syncWorktimeEntriesBetweenDateTimes(date: Date, start: String, end: String): WorktimeEntryListing {
        Log.i("worktimeEntries", "Bearer $token")

        Log.i("worktimeEntries", "start: $start, end: $end")

        val response = apiService
            .getWorktimeEntries("Bearer $token",
                simpleDateFormatter.format(date),
                simpleDateFormatter.format(date))

        if (response.isSuccessful) {
            response.body()?.let {
                val entriesFromApi = it.data

                worktimeEntryDao.clearWorktimeEntriesBetweenDates(userId, start, end);

                try {
                    val syncedEntries = entriesFromApi.map { dto ->
                        addEntryToDb(
                            WorktimeEntry(
                                id = 0,
                                startedAt = formatter.parse(dto.started_at, OffsetDateTime::from),
                                endedAt = formatter.parse(dto.ended_at, OffsetDateTime::from),
                                projectId = dto.project_id,
                                externalId = dto.id,
                                synced = true
                            )
                        )
                    }
                    return WorktimeEntryListing(ResponseStatus.OK, syncedEntries)
                } catch (e: Exception) {
                    return WorktimeEntryListing(ResponseStatus.DBERROR)
                }
            }
            WorktimeEntryListing(ResponseStatus.UNDEFINEDERROR)
        } else {
            Log.i("worktimeEntries", "WorktimeEntryRepository@fetchTodaysWorktimeEntries response was not successful, status code: " + response.code())
            return if (response.code() == 401) {
                WorktimeEntryListing(ResponseStatus.UNAUTHORIZED)
            } else {
                WorktimeEntryListing(ResponseStatus.UNDEFINEDERROR)
            }
        }
        return WorktimeEntryListing(ResponseStatus.UNDEFINEDERROR)
    }

    suspend fun getWorktimeEntryById(id: Int): WorktimeEntryResponse {
        val databaseWorktimeEntry = worktimeEntryDao.getWorktimeEntryById(userId, id)
            ?: return WorktimeEntryResponse(ResponseStatus.NOTFOUND)

        val worktimeEntry = WorktimeEntry(
            id = databaseWorktimeEntry!!.id,
            externalId = databaseWorktimeEntry.externalId,
            startedAt = databaseWorktimeEntry.startedAt,
            endedAt = databaseWorktimeEntry.endedAt,
            synced = databaseWorktimeEntry.synced,
            projectId = databaseWorktimeEntry.projectId
        )

        return if (connectionUtils.hasInternetConnection() && worktimeEntry.externalId != null) {
            getSyncedWorktimeEntry(worktimeEntry)
        } else {
            WorktimeEntryResponse(ResponseStatus.OFFLINE, worktimeEntry)
        }
    }

    private suspend fun getSyncedWorktimeEntry(worktimeEntry: WorktimeEntry): WorktimeEntryResponse {
        val responseFromApi = apiService.getWorktimeEntryById(
            token = "Bearer $token",
            id = worktimeEntry.externalId!!)

        if (responseFromApi.isSuccessful) {
            val entryFromApi = responseFromApi.body()!!

            return WorktimeEntryResponse(
                status = ResponseStatus.OK,
                entry = updateEntryToDb(
                    DatabaseWorktimeEntry(
                        id = worktimeEntry.id,
                        externalId = worktimeEntry.externalId,
                        startedAt = formatter.parse(entryFromApi.started_at, OffsetDateTime::from),
                        endedAt = formatter.parse(entryFromApi.ended_at, OffsetDateTime::from),
                        projectId = entryFromApi.project_id,
                        synced = true,
                        userId = userId,
                        syncedAt = OffsetDateTime.now()
                    )
                )
            )
        } else {
            return WorktimeEntryResponse(ResponseStatus.NOTFOUND)
        }
    }

//    val tempEntry = MutableLiveData<WorktimeEntry>(null)
//
//    suspend fun addWorktimeEntry() {
//        Log.i("worktimeEntries", "WorktimeEntryRepository@addWorktimeEntry")
//        val response = apiService.addWorktimeEntry(
//            "Bearer $token",
//            SaveWorktimeEntryRequest(
//                started_at = "2020-01-01T08:00:00+02:00",
//                ended_at = "2020-01-01T16:00:00+02:00"
//            )
//        )
//        handleSaveWorktimeEntryResponse(response)
//    }
//
//    suspend fun deleteWorktimeEntry() {
//        Log.i("worktimeEntries", "WorktimeEntryRepository@deleteWorktimeEntry")
//        if (tempEntry.value != null) {
//            val entry = tempEntry.value!!
//
//            val response = apiService.deleteWorktimeEntry(
//                token = "Bearer $token",
//                id = entry.externalId!!
//            )
//
//            if (response.isSuccessful) {
//                Log.i("worktimeEntries", "WorktimeEntryRepository@deleteWorktimeEntry response.isSuccessful!")
//            } else {
//                Log.i("worktimeEntries", "WorktimeEntryRepository@deleteWorktimeEntry response.code: " + response.code())
//                if (response.code() != 404 && response.code() != 401) {
//                    response.errorBody()?.let {
//                        val errorJson = JSONObject(it.toString())
//                        val error = errorJson.getJSONObject("errors").toString()
//                        Log.i(
//                            "worktimeEntries",
//                            "WorktimeEntryRepository@deleteWorktimeEntry, response error body: $error"
//                        )
//                    }
//                }
//            }
//            tempEntry.postValue(null)
//        }
//    }
//
//    suspend fun updateWorktimeEntry() {
//        Log.i("worktimeEntries", "WorktimeEntryRepository@updateWorktimeEntry")
//        if (tempEntry.value != null) {
//            val entry = tempEntry.value!!
//            val response = apiService.updateWorktimeEntry(
//                token = "Bearer $token",
//                id = entry.externalId!!,
//                worktimeEntry = SaveWorktimeEntryRequest(
//                    started_at = entry.startedAt.plusHours(5).format(formatter),
//                    ended_at = entry.endedAt.plusHours(5).format(formatter)
//                )
//            )
//            handleSaveWorktimeEntryResponse(response);
//        }
//    }
//
//    private fun handleSaveWorktimeEntryResponse(response: Response<SaveWorktimeEntryResponseDto>) {
//        if (response.isSuccessful) {
//            val entryFromApi = response.body()!!
//            Log.i("worktimeEntries", "WorktimeEntryRepository@handleSaveWorktimeEntryResponse response.isSuccessful!! external_id: ${entryFromApi.id}")
//
//            Log.i("worktimeEntries", "entryFromApi.started_at: ${entryFromApi.started_at}")
//            Log.i("worktimeEntries", "entryFromApi.ended_at: ${entryFromApi.ended_at}")
//
//            tempEntry.postValue(WorktimeEntry(
//                    id =0,
//                    startedAt = OffsetDateTime
//                        .parse(entryFromApi.started_at)
//                        .withOffsetSameInstant(localOffset),
//                    endedAt = OffsetDateTime
//                        .parse(entryFromApi.ended_at)
//                        .withOffsetSameInstant(localOffset),
//                    projectId = entryFromApi.project_id,
//                    externalId = entryFromApi.id,
//                    synced = true
//                ))
//        } else {
//            Log.i("worktimeEntries", "WorktimeEntryRepository@handleSaveWorktimeEntryResponse response.code: " + response.code())
//            if (response.code() != 404 && response.code() != 401) {
//                response.errorBody()?.let {
//                    val errorJson = JSONObject(it.toString())
//                    val error = errorJson.getJSONObject("errors").toString()
//                    Log.i(
//                        "worktimeEntries",
//                        "WorktimeEntryRepository@deleteWorktimeEntry, response error body: $error"
//                    )
//                }
//            }
//        }
//    }

    /**
     * Checks if entry already exists, and adds or updates entry to db.
     *
     * @param entry work time entry to add or update.
     */
    private suspend fun addEntryToDb(entry: WorktimeEntry): WorktimeEntry {
        val existingEntry = worktimeEntryDao.getWorktimeEntryByExternalId(userId, entry.externalId!!)
        if (existingEntry != null) {
            return updateEntryToDb(DatabaseWorktimeEntry(
                    id = existingEntry.id,
                    userId = userId,
                    startedAt = entry.startedAt,
                    endedAt = entry.endedAt,
                    projectId = entry.projectId,
                    externalId = entry.externalId,
                    synced = true,
                    syncedAt = OffsetDateTime.now()
                ))
        }

        try {
            val id = worktimeEntryDao.addWorktimeEntry(
                DatabaseWorktimeEntry(
                    id = 0,
                    userId = userId,
                    startedAt = entry.startedAt,
                    endedAt = entry.endedAt,
                    projectId = entry.projectId,
                    externalId = entry.externalId,
                    synced = true,
                    syncedAt = OffsetDateTime.now()
                )
            )

            return WorktimeEntry(
                id = id.toInt(),
                startedAt = entry.startedAt,
                endedAt = entry.endedAt,
                projectId = entry.projectId,
                externalId = entry.externalId,
                synced = true
            )
        } catch (e: Exception) {
            Log.i("WorktimeEntryRepository", "@addEntryToDb exception: " + e.message)
            throw e
        }
    }

    private suspend fun updateEntryToDb(entry: DatabaseWorktimeEntry): WorktimeEntry {
        worktimeEntryDao.updateWorktimeEntry(entry)
        return WorktimeEntry(
            id = entry.id,
            startedAt = entry.startedAt,
            endedAt = entry.endedAt,
            projectId = entry.projectId,
            externalId = entry.externalId,
            synced = true
        )
    }
}