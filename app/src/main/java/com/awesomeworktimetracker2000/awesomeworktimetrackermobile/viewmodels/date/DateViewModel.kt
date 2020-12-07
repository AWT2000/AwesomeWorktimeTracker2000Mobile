package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.date

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.Project
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.ProjectRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class DateViewModel(
    private val worktimeEntryRepository: WorktimeEntryRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    // initialize inputDate and date variables
    private var inputDate: LocalDate = LocalDate.now()

    var date: Date = Date.from(inputDate.atStartOfDay(DateUtils.ZoneId).toInstant())

    // For updating DateFragment text view to current date
    private var _currentDateString = MutableLiveData<String>()

    val currentDateString: LiveData<String>
        get() = _currentDateString

    private var finnish: Locale? = Locale("fi", "FI")

    private val simpleDateFormat = SimpleDateFormat("EE dd.MM.yyyy", finnish)

    private var _worktimeEntries = MutableLiveData<List<WorktimeEntry>>()

    /**
     * Observable list of work time entries
     */
    val worktimeEntries: LiveData<List<WorktimeEntry>>
        get() = _worktimeEntries

    init {
        Log.i("DateViewModel", "DateViewModel created")
    }

    /**
     * Update list of work time entries by date.
     *
     * @param date a date that work time entries are fetched from
     */
    fun getWorkTimeEntries(date: Date) {

        viewModelScope.launch(Dispatchers.IO) {
            this@DateViewModel.date = date
            this@DateViewModel.inputDate = date
                                            .toInstant()
                                            .atZone(DateUtils.ZoneId)
                                            .toLocalDate()

            val entryListing = worktimeEntryRepository.getWorktimeEntriesByDate(date)
            Log.i("entryListing", entryListing.worktimeEntries?.count().toString())
            Log.i("entryListing", entryListing.status.toString())
            Log.i("date", date.toString())

            // TODO: handling other statuses e.g. ResponseStatus.OFFLINE, ResponseStatus.UNAUTHORIZED, ResponseStatus.UNDEFINEDERROR
            //      - e.g. if device in offline mode, status is OFFLINE, not OK
            //      - maybe transforming errors to live data and showing it to the user?

            if (entryListing.status == ResponseStatus.OK || entryListing.status == ResponseStatus.OFFLINE) {
                _currentDateString.postValue(simpleDateFormat.format(date))
            }

            if (entryListing.worktimeEntries != null) {
                val projects = this@DateViewModel.getProjects()

                entryListing.worktimeEntries.forEach { entry ->
                    entry.project = projects.firstOrNull { x -> x.id == entry.projectId }
                }

                _worktimeEntries.postValue(entryListing.worktimeEntries)
            }
        }
    }
    
    /* TODO: in case call to worktimeEntryRepository fails with nextDate() or prevDate(), pass some temporary date object instead,
       and update the date LiveData object only after successful response
     */
    fun nextDate() {
        inputDate = inputDate.plusDays(1)

        date = Date.from(inputDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        getWorkTimeEntries(date)
    }

    fun prevDate() {
        inputDate = inputDate.minusDays(1)

        date = Date.from(inputDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        getWorkTimeEntries(date)
    }

    suspend fun getProjects(): List<Project> {
        val projectsListing = this.projectRepository.getProjects()

        if (projectsListing.status == ResponseStatus.OK || projectsListing.status == ResponseStatus.OFFLINE) {
            return if (projectsListing.projects != null) {
                projectsListing.projects!!
            } else {
                listOf()
            }
        }

        // TODO: handling other statuses e.g. ResponseStatus.OFFLINE, ResponseStatus.UNAUTHORIZED, ResponseStatus.UNDEFINEDERRO

        return listOf()
    }
}