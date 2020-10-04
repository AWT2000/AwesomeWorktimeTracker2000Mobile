package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities

import androidx.lifecycle.Transformations.map
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.UserInfo

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

//fun DatabaseUserInfo.asDomainModel(): UserInfo {
//    return UserInfo(
//        name = name,
//        email = email,
//        accessToken = token
//    )
//}