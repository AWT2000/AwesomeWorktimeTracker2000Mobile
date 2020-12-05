package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories

import android.util.Log
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.ProjectDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseProject
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.Project
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApiService
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ProjectListing
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.OffsetDateTime

class ProjectRepository private constructor (
    private val apiService: AWTApiService,
    private val projectDao: ProjectDao,
    private val token: String,
    private val connectionUtils: ConnectionUtils
) {
    companion object {
        @Volatile
        private lateinit var instance: ProjectRepository
        @Volatile
        private lateinit var token: String
        @Volatile
        private var userId: Int = 0

        /**
         * Returns singleton instance of ProjectRepository.
         */
        suspend fun getInstance(
            apiService: AWTApiService,
            projectDao: ProjectDao,
            userInfoDao: UserInfoDao,
            connectionUtils: ConnectionUtils
        ): ProjectRepository {

            Mutex().withLock {
                if (!ProjectRepository.Companion::token.isInitialized) {
                    val user = userInfoDao.getUser()!!
                    ProjectRepository.token = user.token
                    ProjectRepository.userId = user.id
                }
                if (!ProjectRepository.Companion::instance.isInitialized) {
                    ProjectRepository.instance = ProjectRepository(
                        apiService,
                        projectDao,
                        ProjectRepository.token,
                        connectionUtils
                    )
                }

                return ProjectRepository.instance
            }
        }
    }

    /**
     * Get projects
     *
     * @return ProjectListing with list of projects and response status
     */
    suspend fun getProjects(): ProjectListing {
        return getCachedProjects()
//        return if (connectionUtils.hasInternetConnection()) {
//            syncProjects()
//        } else {
//            getCachedProjects()
//        }
    }

    /**
     * Fetches projects from database.
     *
     * @return ProjectListing with list of projects and response status
     */
    private suspend fun getCachedProjects(): ProjectListing {
        return try {
            ProjectListing(
                status = ResponseStatus.OFFLINE,
                projects = projectDao

                    .getProjects()
                    .map { projectFromDb ->
                        Project(
                            id = projectFromDb.id,
                            name = projectFromDb.name,
                            founder = projectFromDb.founder,
                            project_manager = projectFromDb.projectManager,
                            synced = projectFromDb.synced
                        )
                    })
        } catch (e: Exception) {
            return ProjectListing(ResponseStatus.DBERROR)
        }
    }

    /**
     * Fetches projects from api.
     *
     * @return ProjectListing with list of projects and response status
     */
    suspend fun syncProjects(): ProjectListing {
        Log.i("projects", "Bearer $token")
        val response = apiService
            .getProjects("Bearer $token")

        if (response.isSuccessful) {
            response.body()?.let {
                val projectsFromApi = it

                projectDao.clearProjects()

                try {
                    val syncedProjects = projectsFromApi.map { dto ->
                        addProjectToDb(
                            Project(
                                id = dto.id,
                                name = dto.name,
                                founder = dto.founder,
                                project_manager = dto.project_manager,
                                synced = true
                            )
                        )
                    }
                    return ProjectListing(ResponseStatus.OK, syncedProjects)
                } catch (e: java.lang.Exception) {
                    return ProjectListing(ResponseStatus.DBERROR)
                }
            }
            ProjectListing(ResponseStatus.UNDEFINEDERROR)
        } else {
            Log.i(
                "projects",
                "ProjectRepository@fetchProjects response was not successful, status code: " + response.code()
            )
            return if (response.code() == 401) {
                ProjectListing(ResponseStatus.UNAUTHORIZED)
            } else {
                ProjectListing(ResponseStatus.UNDEFINEDERROR)
            }
        }
        return ProjectListing(ResponseStatus.UNDEFINEDERROR)
    }

    private suspend fun addProjectToDb(project: Project): Project {
        try {
            val id = projectDao.addProject(
                DatabaseProject(
                    id = project.id,
                    name = project.name,
                    founder = project.founder,
                    projectManager = project.project_manager,
                    synced = true,
                    syncedAt = OffsetDateTime.now()

                )
            )
            return Project(
                id = id.toInt(),
                name = project.name,
                founder = project.founder,
                project_manager = project.project_manager,
                synced = true

            )
        } catch (e: Exception) {
            Log.i("ProjectRepository", "@addEntryToDb exception: " + e.message)
            throw e
        }
    }


}