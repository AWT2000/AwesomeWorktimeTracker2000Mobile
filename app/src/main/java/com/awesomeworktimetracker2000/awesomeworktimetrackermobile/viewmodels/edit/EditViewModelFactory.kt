package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.ProjectDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.UserInfoDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.daos.WorktimeEntryDao
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApiService
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.ProjectRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.date.DateViewModel
import kotlinx.coroutines.runBlocking

class EditViewModelFactory (
    private val worktimeEntryDao: WorktimeEntryDao,
    private val projectDao: ProjectDao,
    private val userInfoDao: UserInfoDao,
    private val apiService: AWTApiService,
    private val connectionUtils: ConnectionUtils,
    private val worktimeEntryId: Int = 0
): ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
                var worktimeEntryRepository: WorktimeEntryRepository
                var projectRepository: ProjectRepository

                runBlocking {
                    worktimeEntryRepository = WorktimeEntryRepository.getInstance(
                        apiService,
                        worktimeEntryDao,
                        userInfoDao,
                        connectionUtils
                    )

                    projectRepository = ProjectRepository.getInstance(
                        apiService,
                        projectDao,
                        userInfoDao,
                        connectionUtils
                    )
                }
                return EditViewModel(worktimeEntryRepository, projectRepository, worktimeEntryId) as T

            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }