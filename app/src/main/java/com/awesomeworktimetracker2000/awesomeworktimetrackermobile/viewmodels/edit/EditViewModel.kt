package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseWorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.Project
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.worktimeEntries.SaveWorktimeEntryRequest
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.ProjectRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.edit.ProjectSpinnerOption
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils.isoDateFormatter
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils.localOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.collections.HashMap

class EditViewModel(
    private val worktimeEntryRepository: WorktimeEntryRepository,
    private val projectRepository: ProjectRepository,
    private var worktimeEntryId: Int = 0
) : ViewModel() {

    private lateinit var worktimeEntry: WorktimeEntry
    private var externalId: Int = 0

    private var _editTitle = MutableLiveData<String>()
    val editTitle: LiveData<String>
        get() = _editTitle

    var startDate: OffsetDateTime = OffsetDateTime.now(localOffset)
    var endDate: OffsetDateTime = OffsetDateTime.now(localOffset)

    var start = MutableLiveData<OffsetDateTime>()
    var end = MutableLiveData<OffsetDateTime>()

    /**
     * Observable lists of project id's and names for spnrProject
     */
    private var _projectNames = MutableLiveData<List<String>>()

    val projects = MutableLiveData<List<ProjectSpinnerOption>>()

    var selectedProjectId: Int = 0

    var selectedProjectIdLive = MutableLiveData<Int>()

    private var _savedSuccessfully = MutableLiveData<Boolean>(false)

    val savedSuccesfully: LiveData<Boolean>
        get() = _savedSuccessfully

    /**
     * get names and id:s of all projects
     * update _projectsId, _projectsName with them
     */
    fun getProjects() {
        viewModelScope.launch(Dispatchers.IO) {
            val projectsListing = projectRepository.getProjects()

            if (projectsListing.status == ResponseStatus.OK
                || projectsListing.status == ResponseStatus.OFFLINE)
            {
                if (projectsListing.projects != null) {

                    val projectList = mutableListOf<ProjectSpinnerOption>(
                        ProjectSpinnerOption(
                            Project(
                                id = -1,
                                name = "Muu Työ",
                                founder = null,
                                project_manager = null,
                                synced = true
                            ))
                    )

                    projectsListing.projects.forEach { it ->
                        projectList.add(ProjectSpinnerOption(it))
                    }

                    projects.postValue(projectList)
                }
            }
            // TODO: handling other statuses e.g. ResponseStatus.OFFLINE, ResponseStatus.UNAUTHORIZED, ResponseStatus.UNDEFINEDERROR
        }
    }

    /**
     * update or add new workTimeEntry
     */
    fun saveWorkTimeEntry() {

        var projectId: Int? = null

        if (selectedProjectId > 0) {
            projectId = selectedProjectId
        }

        val saveWorkTimeEntry = SaveWorktimeEntryRequest(
            project_id = projectId,
            started_at = startDate.format(isoDateFormatter),
            ended_at = endDate.format(isoDateFormatter)
        )

        /**
         * update workTimeEntry
         */
        if (this@EditViewModel.worktimeEntryId != 0 || this@EditViewModel.externalId != 0) {

            val workTimeEntry = WorktimeEntry(
                id = this@EditViewModel.worktimeEntryId,
                startedAt = startDate,
                endedAt = endDate,
                projectId = projectId,
                externalId = this@EditViewModel.externalId,
                synced = false
            )

            viewModelScope.launch(Dispatchers.IO) {
                var workTimeEntry: WorktimeEntry = worktimeEntryRepository.addEntryToDb(workTimeEntry)
                _savedSuccessfully.postValue(true)
            }

            GlobalScope.launch(Dispatchers.IO) {
                val saveResponse = worktimeEntryRepository.updateWorktimeEntry(this@EditViewModel.externalId, saveWorkTimeEntry)

                if (saveResponse.status == ResponseStatus.OK) {
                    worktimeEntryRepository.addEntryToDb(
                        WorktimeEntry(
                            id = workTimeEntry.id,
                            externalId = saveResponse.worktimeEntry!!.externalId,
                            projectId = projectId,
                            startedAt = workTimeEntry.startedAt,
                            endedAt = workTimeEntry.endedAt,
                            synced = true
                        )
                    )
                }
            }
        }

        /**
         *  new workTimeEntry
         */
        else {
            var workTimeEntry = WorktimeEntry(
                id = 0,
                startedAt = startDate,
                endedAt = endDate,
                projectId = projectId,
                externalId = null,
                synced = false
            )

            viewModelScope.launch(Dispatchers.IO) {
                workTimeEntry = worktimeEntryRepository.addEntryToDb(workTimeEntry)
                _savedSuccessfully.postValue(true)
            }

            GlobalScope.launch(Dispatchers.IO) {
                val saveResponse = worktimeEntryRepository.addWorktimeEntry(saveWorkTimeEntry)

                if (saveResponse.status == ResponseStatus.OK) {
                    worktimeEntryRepository.addEntryToDb(
                        WorktimeEntry(
                            id = workTimeEntry.id,
                            externalId = saveResponse.worktimeEntry!!.externalId,
                            projectId = projectId,
                            startedAt = workTimeEntry.startedAt,
                            endedAt = workTimeEntry.endedAt,
                            synced = true
                        )
                    )
                }
            }
        }
    }

    fun deleteWorkTimeEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            worktimeEntryRepository.deleteEntryFromDb(this@EditViewModel.worktimeEntryId)
        }

        viewModelScope.launch(Dispatchers.IO) {
            worktimeEntryRepository.deleteWorktimeEntry(this@EditViewModel.externalId)
        }
    }

    /**
     *  set startDate and endDate to current date(from DateFragment)
     *  or to a value of worktimeEntry start/end date
     *
     *  get worktimeEntryId, externalId from worktimeEntry,
     *  or set to 0
     *
     *  @param date &
     *  @param worktimeEntryId received from DateFragment
     *
     */
    fun pickDateTime(date: OffsetDateTime, worktimeEntryId: Int) {

        startDate = date
        endDate = date

        if (worktimeEntryId != 0) {

            _editTitle.postValue("Muokkaa")
            viewModelScope.launch(Dispatchers.IO) {
                val worktimeEntryResponse =
                    worktimeEntryRepository.getWorktimeEntryById(worktimeEntryId)

                if (worktimeEntryResponse.status == ResponseStatus.OK
                    || worktimeEntryResponse.status == ResponseStatus.OFFLINE
                ) {

                    this@EditViewModel.worktimeEntry = worktimeEntryResponse.entry!!

                    this@EditViewModel.worktimeEntryId = worktimeEntry.id

                    if (worktimeEntry.externalId != null) {
                        this@EditViewModel.externalId = worktimeEntry.externalId!!
                    }

                    startDate = worktimeEntry.startedAt.withOffsetSameInstant(localOffset)

                    endDate = worktimeEntry.endedAt.withOffsetSameInstant(localOffset)

                    start.postValue(startDate)

                    end.postValue(endDate)

                    if (worktimeEntry.projectId != null) {
                        this@EditViewModel.selectedProjectId = worktimeEntry.projectId!!

                        this@EditViewModel.selectedProjectIdLive.postValue(worktimeEntry.projectId!!)

                    }
                }
            }
        }
        else {
            _editTitle.postValue("Lisää")

            this@EditViewModel.worktimeEntryId = 0

            this@EditViewModel.externalId = 0

            startDate = startDate.withMinute(LocalDateTime.now().minute)
            startDate = startDate.withHour(LocalDateTime.now().hour)

            endDate = endDate.withMinute(LocalDateTime.now().minute)
            endDate = endDate.withHour(LocalDateTime.now().hour)
        }
    }

    /**
     * set startDateTime or endDateTime value
     *
     * @param start = true if editing startDateTime of workTimeEntry
     *
     */
    fun setDate(start: Boolean, day: Int, month: Int, year: Int, hour: Int, minute: Int) {
        val localDateTime = LocalDateTime.of(year, month, day, hour, minute)
        val date: OffsetDateTime = localDateTime.atOffset(localOffset)

        if (start) {
            startDate = date
        } else {
            endDate = date
        }
    }
}