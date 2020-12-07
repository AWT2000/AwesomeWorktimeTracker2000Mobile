package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.DatabaseWorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.worktimeEntries.SaveWorktimeEntryRequest
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.ProjectRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils.ZoneId
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils.isoDateFormatter
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils.localOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.HashMap
import kotlin.time.minutes

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

    /**
     * Observable lists of project id's and names for spnrProject
     */
    private var _projectNames = MutableLiveData<List<String>>()
    val projectNames: LiveData<List<String>>
        get() = _projectNames

    var projectsMap: MutableMap<String, Int> = HashMap()
    var selectedProjectId: Int = 0

    init {
        Log.i("EditViewModel", "EditViewModel created")
    }

    /**
     * get names and id:s of all projects
     * update _projectsId, _projectsName with them
     */
    fun getProjects() {
        viewModelScope.launch(Dispatchers.IO) {

            val projectsListing = projectRepository.getProjects()

            if (projectsListing.status == ResponseStatus.OK || projectsListing.status == ResponseStatus.OFFLINE) {
                if (projectsListing.projects != null) {
                    projectsListing.projects.forEach { it ->
                        projectsMap[it.name] = it.id
                    }
                    _projectNames.postValue(projectsMap.keys.toList())
                    Log.i("projectsMap", "${projectsMap}")
                }
            }
            // TODO: handling other statuses e.g. ResponseStatus.OFFLINE, ResponseStatus.UNAUTHORIZED, ResponseStatus.UNDEFINEDERROR
        }
    }

    /**
     * update or add new workTimeEntry
     */
    fun saveWorkTimeEntry() {

        val saveWorkTimeEntry = SaveWorktimeEntryRequest(
            project_id = selectedProjectId,
            started_at = startDate.format(isoDateFormatter),
            ended_at = endDate.format(isoDateFormatter)
        )

        /**
         * update workTimeEntry
         */
        if (this@EditViewModel.worktimeEntryId != 0 && this@EditViewModel.externalId != 0) {

            // TODO: userId = 0 ?
            val workTimeDatabaseEntry = DatabaseWorktimeEntry(
                id = this@EditViewModel.worktimeEntryId,
                userId = 0,
                startedAt = startDate,
                endedAt = endDate,
                projectId = selectedProjectId,
                externalId = 0
            )
            viewModelScope.launch(Dispatchers.IO) {
                Log.i("EditViewModel", "Updating localDB entry...")
                var workTimeEntry: WorktimeEntry = worktimeEntryRepository.updateEntryToDb(workTimeDatabaseEntry)
                Log.i("EditViewModel", "updated entry id: ${workTimeEntry.id}")
            }
            viewModelScope.launch(Dispatchers.IO) {
                Log.i("EditViewModel", "Updating apiDB entry...")
                worktimeEntryRepository.updateWorktimeEntry(this@EditViewModel.externalId, saveWorkTimeEntry)
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
                projectId = selectedProjectId,
                externalId = null
            )


            viewModelScope.launch(Dispatchers.IO) {
                Log.i("EditViewModel", "Adding localDB entry...")
                workTimeEntry = worktimeEntryRepository.addEntryToDb(workTimeEntry)
                Log.i("EditViewModel", "added entry id: ${workTimeEntry.id}")
            }
            viewModelScope.launch(Dispatchers.IO) {
                Log.i("EditViewModel", "Adding apiDB entry...")
                worktimeEntryRepository.addWorktimeEntry(saveWorkTimeEntry)
            }

        }
        }
    fun deleteWorkTimeEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("EditViewModel", "Deleting localDB entry...")
            worktimeEntryRepository.deleteEntryFromDb(this@EditViewModel.worktimeEntryId)
        }
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("EditViewModel", "Deleting apiDB entry...")
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

            //_editTitle.postValue(R.string.edit.toString())
            _editTitle.postValue("Muokkaa")
            viewModelScope.launch(Dispatchers.IO) {
                val worktimeEntryResponse =
                    worktimeEntryRepository.getWorktimeEntryById(worktimeEntryId)

                if (worktimeEntryResponse.status == ResponseStatus.OK
                    || worktimeEntryResponse.status == ResponseStatus.OFFLINE
                ) {
                    Log.i("editViewModel", worktimeEntryResponse.entry!!.id.toString())

                    this@EditViewModel.worktimeEntry = worktimeEntryResponse.entry!!
                    this@EditViewModel.worktimeEntryId = worktimeEntry.id
                    this@EditViewModel.externalId = worktimeEntry.externalId!!
                    startDate = worktimeEntry.startedAt.withOffsetSameInstant(localOffset)
                    endDate = worktimeEntry.endedAt.withOffsetSameInstant(localOffset)

                    if (worktimeEntry.projectId != null) {
                        this@EditViewModel.selectedProjectId = worktimeEntry.projectId!!
                        Log.i("selectedProjectId", "$selectedProjectId")
                    }

                    Log.i("editViewModel", "worktimeEntryId: ${this@EditViewModel.worktimeEntryId}")
                    Log.i("editViewModel", "externalId: ${this@EditViewModel.externalId}")
                    Log.i("editViewModel", "startDate: ${startDate}")
                    Log.i("editViewModel", "endDate: ${endDate}")
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
        var date: OffsetDateTime = localDateTime.atOffset(localOffset)

        Log.i("editViewModel", "setDate: $date")

        if (start) {
            startDate = date
        }
        else {
            endDate = date
        }
    }
}