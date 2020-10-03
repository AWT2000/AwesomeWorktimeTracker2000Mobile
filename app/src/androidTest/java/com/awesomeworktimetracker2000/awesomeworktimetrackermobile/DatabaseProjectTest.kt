//package com.awesomeworktimetracker2000.awesomeworktimetrackermobile
//
//import androidx.room.Room
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.*
//import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.ProjectDao
//import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.Contact
//import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseProject
//import org.junit.After
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.io.IOException
//import java.lang.Exception
//
//@RunWith(AndroidJUnit4::class)
//class DatabaseProjectTest {
//
//    private lateinit var projectDao: ProjectDao
//    private lateinit var awtDatabase: AWTDatabase
//
//    @Before
//    fun createDb() {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//
//        awtDatabase = Room.inMemoryDatabaseBuilder(context, AWTDatabase::class.java)
//            .allowMainThreadQueries()
//            .build()
//
//        projectDao = awtDatabase.projectDao
//    }
//
//    @After
//    @Throws(IOException::class)
//    fun closeDb() {
//        awtDatabase.close()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun insertAndGetProject() {
//        val project = DatabaseProject(
//            1,
//            "test project",
//            Contact(
//                "founder",
//                "founder@example.com"
//            ),
//            Contact(
//                "project manager",
//                "manager@example.com"
//            )
//        )
//
//        projectDao.addProject(project)
//
//        val projectsFromDb = projectDao.getProjects()
//
//        Assert.assertEquals(1, projectsFromDb.count())
//
//        val projectFromDb = projectsFromDb[0]
//
//        Assert.assertEquals(1, projectFromDb.id)
//        Assert.assertEquals("test project", projectFromDb.name)
//        Assert.assertNotNull(projectFromDb.founder)
//        Assert.assertEquals("founder", projectFromDb.founder?.name)
//        Assert.assertNotNull(projectFromDb.projectManager)
//        Assert.assertEquals("project manager", projectFromDb.projectManager?.name)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun clearProjectsFromDb() {
//        projectDao.addProject(DatabaseProject(
//            1,
//            "test project",
//            Contact(
//                "founder",
//                "founder@example.com"
//            ),
//            Contact(
//                "project manager",
//                "manager@example.com"
//            )
//        ))
//
//        projectDao.clearProjects()
//
//        Assert.assertEquals(0, projectDao.getProjects().count())
//    }
//
//}