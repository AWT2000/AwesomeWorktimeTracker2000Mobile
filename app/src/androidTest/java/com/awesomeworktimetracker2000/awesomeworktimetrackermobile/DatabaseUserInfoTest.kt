package com.awesomeworktimetracker2000.awesomeworktimetrackermobile

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.AWTDatabase
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseUserInfo
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

@RunWith(AndroidJUnit4::class)
class DatabaseUserInfoTest {

    private lateinit var userInfoDao: UserInfoDao
    private lateinit var awtDatabase: AWTDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        awtDatabase = Room.inMemoryDatabaseBuilder(context, AWTDatabase::class.java)
                            .allowMainThreadQueries()
                            .build()

        userInfoDao = awtDatabase.userDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        awtDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetUser() {
        val userInfo = DatabaseUserInfo(
            "user@example.com",
            "User",
            "access_token",
            0
        )

        runBlocking {
            userInfoDao.upsertUser(userInfo)

            val user = userInfoDao.getUser()

            assertNotNull("user should not be null", user)
            assertEquals("User", user?.name)
            assertEquals("user@example.com", user?.email)
            assertEquals("access_token", user?.token)
        }
    }

    @Test
    @Throws(Exception::class)
    fun updateUser() {
        val userInfo = DatabaseUserInfo(
            email ="user@example.com",
            name = "User",
            token = "access_token",
            id = 0
        )

        runBlocking {
            userInfoDao.upsertUser(userInfo)

            userInfoDao.upsertUser(
                DatabaseUserInfo(
                    "user@example.com",
                    "another name",
                    "access_token",
                    0
                )
            )

            val user = userInfoDao.getUser()

            assertEquals("another name", user?.name)
            assertEquals("user@example.com", user?.email)
            assertEquals("access_token", user?.token)
        }
    }
}