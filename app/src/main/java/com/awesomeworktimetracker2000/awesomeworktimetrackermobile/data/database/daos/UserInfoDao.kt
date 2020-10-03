package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseUserInfo

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM user_info LIMIT 1")
    suspend fun getUser(): DatabaseUserInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: DatabaseUserInfo)

    @Query("DELETE FROM user_info")
    suspend fun removeUser()
}