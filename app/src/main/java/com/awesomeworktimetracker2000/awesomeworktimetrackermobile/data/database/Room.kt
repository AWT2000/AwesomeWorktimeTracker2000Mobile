package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database

import android.content.Context
import androidx.room.*
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.ProjectDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.WorktimeEntryDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseProject
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseUserInfo
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseWorktimeEntry

@Database(
    entities = [DatabaseUserInfo::class, DatabaseWorktimeEntry::class, DatabaseProject::class],
    version = 1
)
@TypeConverters(DbTypeConverters::class)
abstract class AWTDatabase: RoomDatabase() {
    abstract val userDao: UserInfoDao
    abstract val worktimeEntryDao: WorktimeEntryDao
    abstract val projectDao: ProjectDao

    companion object {
        @Volatile
        private lateinit var INSTANCE: AWTDatabase

        fun getInstance(context: Context): AWTDatabase {
            synchronized(this) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AWTDatabase::class.java,
                        "awt_database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }


}



