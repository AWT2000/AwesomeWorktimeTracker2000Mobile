package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseProject

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects")
    suspend fun getProjects(): List<DatabaseProject>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Int): DatabaseProject?

    @Insert
    suspend fun addProject(project: DatabaseProject): Long

    @Query("DELETE FROM projects")
    suspend fun clearProjects()
}