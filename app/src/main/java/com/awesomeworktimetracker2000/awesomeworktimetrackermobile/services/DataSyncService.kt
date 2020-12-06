package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.services

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.ProjectRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import java.time.OffsetTime
import java.util.*

class DataSyncService private constructor(
    private val worktimeEntryRepository: WorktimeEntryRepository,
    private val projectRepository: ProjectRepository,
    private val connectionUtils: ConnectionUtils
) {

    companion object {

        @Volatile private lateinit var instance: DataSyncService

        fun getInstance(
            worktimeEntryRepository: WorktimeEntryRepository,
            projectRepository: ProjectRepository,
            connectionUtils: ConnectionUtils
        ): DataSyncService {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = DataSyncService(
                        worktimeEntryRepository,
                        projectRepository,
                        connectionUtils
                    )
                }
                return instance
            }
        }
    }

    /**
     * Get unsynced worktime entries from local db and syn them to API.
     */
    suspend fun syncUnsyncedWorktimeEntries() {
        val unsyncedWorktimeEntryListing = worktimeEntryRepository.getUnsyncedWorktimeEntries()

        if (unsyncedWorktimeEntryListing.status == ResponseStatus.OK
            && connectionUtils.hasInternetConnection())
        {
            unsyncedWorktimeEntryListing.worktimeEntries?.let {
                it.forEach { entry ->
                    // TODO: use worktime entry repo to send unsynced entry to api and update db
                }
            }
        }
    }

    /**
     * Sync worktime entries from start date to end date. Default from 30 days ago to today.
     *
     * @param startDate start date object
     * @param endDate end date object
     */
    suspend fun syncWorktimeEntriesFromApiToLocalDb(
        startDate: Date = DateUtils.todayAddDays(-30),
        endDate: Date = Date()
    ) {
        if (connectionUtils.hasInternetConnection()) {
            val localStartDate = startDate.toInstant().atZone(DateUtils.ZoneId).toLocalDate()
            val startDateString = localStartDate.atTime(OffsetTime.MIN).toString()

            val localEndDate = startDate.toInstant().atZone(DateUtils.ZoneId).toLocalDate()
            val endDateString = localEndDate.plusDays(1).atTime(OffsetTime.MIN).toString()

            worktimeEntryRepository.syncWorktimeEntriesBetweenDateTimes(
                startDate = startDate,
                endDate = endDate,
                startDateTimeString = startDateString,
                endDateTimeString = endDateString
            )
        }
    }

    /**
     * Fetch projects from API to local db.
     */
    suspend fun syncProjects() {
        projectRepository.syncProjects()
    }
}