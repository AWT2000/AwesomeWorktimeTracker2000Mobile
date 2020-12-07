package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseWorktimeEntry

@Dao
interface WorktimeEntryDao {
    @Query("SELECT * FROM worktime_entries WHERE user_id = :userId AND (started_at >= :start AND ended_at <= :end) OR (started_at < :start AND ended_at > :start) OR (started_at < :end AND ended_at > :end)")
    suspend fun getWorktimeEntriesBetweenDates(userId: Int, start: String, end: String): List<DatabaseWorktimeEntry>

    @Query("SELECT * FROM worktime_entries WHERE user_id = :userId AND ((started_at >= :start AND ended_at <= :end) OR (started_at < :start AND ended_at > :start) OR (started_at < :end AND ended_at > :end)) AND project_id = :projectId")
    suspend fun getWorktimeEntriesBetweenDatesByProject(userId: Int, start: String, end: String, projectId: Int): List<DatabaseWorktimeEntry>

    @Query("SELECT * FROM worktime_entries WHERE user_id = :userId AND external_id = :externalId")
    suspend fun getWorktimeEntryByExternalId(userId: Int, externalId: Int): DatabaseWorktimeEntry?

    @Query("SELECT * FROM worktime_entries WHERE user_id = :userId AND id = :id")
    suspend fun getWorktimeEntryById(userId: Int, id: Int): DatabaseWorktimeEntry?

    @Query("SELECT * FROM worktime_entries WHERE user_id = :userId AND synced = 0")
    suspend fun getUnsyncedWorktimeEntries(userId: Int): List<DatabaseWorktimeEntry>

    @Insert
    suspend fun addWorktimeEntry(worktimeEntry: DatabaseWorktimeEntry): Long

    @Update
    suspend fun updateWorktimeEntry(worktimeEntry: DatabaseWorktimeEntry)

    @Query("DELETE FROM worktime_entries WHERE user_id = :userId AND started_at >= :start AND ended_at <= :end")
    suspend fun clearWorktimeEntriesBetweenDates(userId: Int, start: String, end: String)

    @Query ("DELETE FROM worktime_entries WHERE id = :id")
    suspend fun deleteWorktimeEntry(id: Int)

}
