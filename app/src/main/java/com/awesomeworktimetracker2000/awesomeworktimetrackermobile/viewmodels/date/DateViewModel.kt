package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.date

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.AWTDatabase
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.UserRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.WorktimeEntryListing
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

// alternatively use AndroidViewModel as Application context aware ViewModel
class DateViewModel(date: Date, private val userRepository: UserRepository, private val application: Application) : ViewModel() {

    //val sdf = SimpleDateFormat("yyyy-mm-dd")

    // TODO: Databinding, getters for Mutable Data?
    private val date = MutableLiveData<Date>(date)
    private lateinit var _worktimeEntries : MutableLiveData<List<WorktimeEntry>>


    init {
        getWorkTimeEntries(date)
        Log.i("DateViewModel", "DateViewModel created")
    }

    private fun getWorkTimeEntries(date : Date) {
        var weRepo: WorktimeEntryRepository

        runBlocking {
            val db = AWTDatabase.getInstance(application)

            weRepo = WorktimeEntryRepository.getInstance(
                AWTApi.service,
                db.worktimeEntryDao,
                db.userDao,
                ConnectionUtils.getInstance(application)
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            val entryListing = weRepo.getWorktimeEntriesByDate(date)

            _worktimeEntries.value = entryListing.worktimeEntries

            if (entryListing.status == ResponseStatus.OK && entryListing.worktimeEntries != null) {
                val entries = entryListing.worktimeEntries
                if (entries.count() == 0) {
                    Log.i("worktimeEntries", "entries.count() == 0")
                }

                entries.forEach { entry ->
                    Log.i("worktimeEntries", "external id: ${entry.externalId}, started_at: "
                            + "${entry.startedAt.format(DateUtils.isoDateFormatter)}, "
                            + "ended_at: ${entry.endedAt.format(DateUtils.isoDateFormatter)}" )
                }
            }
        }
    }
}