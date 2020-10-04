package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseWorktimeEntry

@Dao
interface WorktimeEntryDao {
    @Query("SELECT * FROM worktime_entries WHERE (started_at >= :start AND ended_at <= :end) OR (started_at < :start AND ended_at > :start) OR (started_at < :end AND ended_at > :end)")
    suspend fun getWorktimeEntriesByDate(start: String, end: String): List<DatabaseWorktimeEntry>

    @Query("SELECT * FROM worktime_entries WHERE ((started_at >= :start AND ended_at <= :end) OR (started_at < :start AND ended_at > :start) OR (started_at < :end AND ended_at > :end)) AND project_id = :projectId")
    suspend fun getWorktimeEntriesByDateAndProject(start: String, end: String, projectId: Int): List<DatabaseWorktimeEntry>

    @Query("SELECT * FROM worktime_entries WHERE external_id = :externalId")
    suspend fun getWorktimeEntryByExternalId(externalId: Int): DatabaseWorktimeEntry?

    @Query("SELECT * FROM worktime_entries WHERE synced = 0")
    suspend fun getUnsyncedWorktimeEntries(): List<DatabaseWorktimeEntry>

    @Insert
    suspend fun addWorktimeEntry(worktimeEntry: DatabaseWorktimeEntry)

    @Update
    suspend fun updateWorktimeEntry(worktimeEntry: DatabaseWorktimeEntry)
}