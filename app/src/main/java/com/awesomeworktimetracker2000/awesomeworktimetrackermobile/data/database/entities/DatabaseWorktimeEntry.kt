package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    tableName = "worktime_entries"
//    foreignKeys = [ForeignKey(
//        entity = DatabaseProject::class,
//        parentColumns = arrayOf("id"),
//        childColumns = arrayOf("project_id"),
//        onDelete = ForeignKey.CASCADE
//    )]
)
data class DatabaseWorktimeEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "started_at")
    val startedAt: OffsetDateTime,
    @ColumnInfo(name = "ended_at")
    val endedAt: OffsetDateTime,
    @ColumnInfo(name = "synced_at")
    val syncedAt: OffsetDateTime? = null,
    @ColumnInfo(name = "project_id")
    val projectId: Int?,
    @ColumnInfo(name = "external_id")
    val externalId: Int?,
    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)