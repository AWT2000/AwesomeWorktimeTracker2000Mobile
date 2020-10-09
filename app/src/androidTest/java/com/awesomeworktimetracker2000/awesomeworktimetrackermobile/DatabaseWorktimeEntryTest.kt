package com.awesomeworktimetracker2000.awesomeworktimetrackermobile

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.*
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.WorktimeEntryDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.Contact
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseProject
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseWorktimeEntry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.lang.Exception
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class DatabaseWorktimeEntryTest {
    private lateinit var worktimeEntryDao: WorktimeEntryDao
    private lateinit var awtDatabase: AWTDatabase

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

    private val projects = listOf<DatabaseProject>(
        DatabaseProject(
            1,
            "test project 1",
            Contact(
                "founder",
                "founder@example.com"
            ),
            Contact(
                "project manager",
                "manager@example.com"
            )
        ),
        DatabaseProject(
            2,
            "test project 2",
            Contact(
                "founder",
                "founder@example.com"
            ),
            Contact(
                "project manager",
                "manager@example.com"
            )
        ),
        DatabaseProject(
            3,
            "test project 3",
            Contact(
                "founder",
                "founder@example.com"
            ),
            Contact(
                "project manager",
                "manager@example.com"
            )
        )
    )

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        awtDatabase = Room.inMemoryDatabaseBuilder(context, AWTDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        worktimeEntryDao = awtDatabase.worktimeEntryDao

        runBlocking {
            projects.forEach { project ->
                awtDatabase.projectDao.addProject(project)
            }
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        awtDatabase.close()
    }

    private val worktimeEntries = listOf<DatabaseWorktimeEntry>(
        DatabaseWorktimeEntry(
                0,
            OffsetDateTime.parse("2020-01-01T09:00:00+02:00"),
            OffsetDateTime.parse("2020-01-01T12:00:00+02:00"),
            projects[0].id,
            null,
            false
        ),
        DatabaseWorktimeEntry(
            0,
            OffsetDateTime.parse("2020-01-01T12:30:00+02:00"),
            OffsetDateTime.parse("2020-01-01T15:00:00+02:00"),
            projects[1].id,
            null,
            false
        ),
        DatabaseWorktimeEntry(
            0,
            OffsetDateTime.parse("2020-01-01T15:00:00+02:00"),
            OffsetDateTime.parse("2020-01-01T17:00:00+02:00"),
            projects[2].id,
            null,
            false
        ),
        DatabaseWorktimeEntry(
            0,
            OffsetDateTime.parse("2020-01-02T09:00:00+02:00"),
            OffsetDateTime.parse("2020-01-02T12:00:00+02:00"),
            projects[0].id,
            null,
            false
        ),
        DatabaseWorktimeEntry(
            0,
            OffsetDateTime.parse("2020-01-02T12:30:00+02:00"),
            OffsetDateTime.parse("2020-01-02T15:00:00+02:00"),
            projects[1].id,
            null,
            false
        ),
        DatabaseWorktimeEntry(
            0,
            OffsetDateTime.parse("2020-01-02T15:00:00+02:00"),
            OffsetDateTime.parse("2020-01-02T17:00:00+02:00"),
            projects[2].id,
            null,
            false
        )
    )

    @Test
    @Throws(Exception::class)
    fun insertAndGetWorktimeEntry() {
        val entry = worktimeEntries[0]

        runBlocking {
            worktimeEntryDao.addWorktimeEntry(entry)

            val date = entry.startedAt.toLocalDate()

            val entriesFromDb = worktimeEntryDao.getWorktimeEntriesByDate(
                start = date.atTime(OffsetTime.MIN).toString(),
                end = date.plusDays(1).atTime(OffsetTime.MIN).toString())

            assertEquals(1, entriesFromDb.count())
            assertEquals("2020-01-01T09:00:00+02:00", formatter.format(entriesFromDb[0].startedAt))
            assertEquals("2020-01-01T12:00:00+02:00", formatter.format(entriesFromDb[0].endedAt))
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertGetWorktimeEntriesByDate() {
        runBlocking {
            worktimeEntries.forEach { entry ->
                worktimeEntryDao.addWorktimeEntry(entry)
            }

            val date = worktimeEntries[0].startedAt.toLocalDate()

            val entriesFromDb = worktimeEntryDao.getWorktimeEntriesByDate(
                start = date.atTime(OffsetTime.MIN).toString(),
                end = date.plusDays(1).atTime(OffsetTime.MIN).toString())

            assertEquals(3, entriesFromDb.count())
            assertEquals("2020-01-01T09:00:00+02:00", formatter.format(entriesFromDb[0].startedAt))
            assertEquals("2020-01-01T12:30:00+02:00", formatter.format(entriesFromDb[1].startedAt))
            assertEquals("2020-01-01T15:00:00+02:00", formatter.format(entriesFromDb[2].startedAt))
        }
    }

    @Test
    @Throws(Exception::class)
    fun updatetWorktimeEntry() {
        val entry = worktimeEntries[0]

        runBlocking {
            worktimeEntryDao.addWorktimeEntry(entry)

            val date = entry.startedAt.toLocalDate()

            val entriesFromDb = worktimeEntryDao.getWorktimeEntriesByDate(
                start = date.atTime(OffsetTime.MIN).toString(),
                end = date.plusDays(1).atTime(OffsetTime.MIN).toString())

            val entryToUpdate = DatabaseWorktimeEntry(
                id = entriesFromDb[0].id,
                startedAt = OffsetDateTime.parse("2020-01-02T12:30:00+02:00"),
                endedAt = OffsetDateTime.parse("2020-01-02T15:00:00+02:00"),
                projectId = projects[1].id,
                externalId = 5,
                synced = true
            )

            worktimeEntryDao.updateWorktimeEntry(entryToUpdate)

            val updatedEntry = worktimeEntryDao.getWorktimeEntryByExternalId(5)

            Assert.assertNotNull(updatedEntry)
            assertEquals("2020-01-02T12:30:00+02:00", formatter.format(updatedEntry?.startedAt))
            assertEquals("2020-01-02T15:00:00+02:00", formatter.format(updatedEntry?.endedAt))
            assertEquals(true, updatedEntry?.synced)
            assertEquals(5, updatedEntry?.externalId)
        }
    }
}