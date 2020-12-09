package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.services

import android.util.Log
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.worktimeEntries.SaveWorktimeEntryRequest
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
                    val saveWorkTimeEntry = SaveWorktimeEntryRequest(
                        project_id = entry.projectId,
                        started_at = entry.startedAt.format(DateUtils.isoDateFormatter),
                        ended_at = entry.endedAt.format(DateUtils.isoDateFormatter)
                    )

                    if (entry.externalId == null) {


                        if (connectionUtils.hasInternetConnection()) {
                            val saveResponse = worktimeEntryRepository.addWorktimeEntry(saveWorkTimeEntry)

                            if (saveResponse.status == ResponseStatus.OK) {
                                worktimeEntryRepository.addEntryToDb(
                                    WorktimeEntry(
                                        id = entry.id,
                                        externalId = saveResponse.worktimeEntry!!.externalId,
                                        projectId = entry.projectId,
                                        startedAt = entry.startedAt,
                                        endedAt = entry.endedAt,
                                        synced = true
                                    )
                                )
                            }
                        }
                    } else {
                        if (connectionUtils.hasInternetConnection()) {
                            val saveResponse = worktimeEntryRepository.updateWorktimeEntry(entry.externalId, saveWorkTimeEntry)

                            if (saveResponse.status == ResponseStatus.OK) {
                                worktimeEntryRepository.addEntryToDb(
                                    WorktimeEntry(
                                        id = entry.id,
                                        externalId = saveResponse.worktimeEntry!!.externalId,
                                        projectId = entry.projectId,
                                        startedAt = entry.startedAt,
                                        endedAt = entry.endedAt,
                                        synced = true
                                    )
                                )
                            }
                        }
                    }
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