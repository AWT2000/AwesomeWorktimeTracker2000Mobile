package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "projects")
data class DatabaseProject(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @Embedded(prefix = "founder_")
    val founder: Contact?,
    @Embedded(prefix = "project_manager_")
    val projectManager: Contact?,
    @ColumnInfo(name = "synced")
    val synced: Boolean = false,
    @ColumnInfo(name = "synced_at")
    val syncedAt: OffsetDateTime? = null
)
