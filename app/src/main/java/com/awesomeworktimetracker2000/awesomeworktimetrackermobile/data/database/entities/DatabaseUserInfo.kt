package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class DatabaseUserInfo constructor(
    @PrimaryKey
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "token")
    val token: String
)