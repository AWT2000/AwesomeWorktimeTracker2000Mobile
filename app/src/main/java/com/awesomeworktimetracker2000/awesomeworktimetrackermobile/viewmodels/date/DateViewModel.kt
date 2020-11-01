package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.date

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class DateViewModel(private val worktimeEntryRepository: WorktimeEntryRepository) : ViewModel() {

    //val sdf = SimpleDateFormat("yyyy-MM-dd")

    // Placeholder until date gets passed from week vm
    private var inputDate: LocalDate = LocalDate.of(2020, 4, 15)
    private var date: Date = Date.from(inputDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    //private val date = MutableLiveData<Date>()

    // For updating DateFragment text view to current date
    var currentDate = MutableLiveData<String>()
    private var finnish: Locale? = Locale("fi", "FI")
    private val sdf = SimpleDateFormat("EE dd.MM.yyyy", finnish)

    // Observed in DateFragment
    var worktimeEntries = MutableLiveData<List<WorktimeEntry>>()

    init {
        Log.i("DateViewModel", "DateViewModel created")
        // Placeholder until getWorkTimeEntries gets called from week
        getWorkTimeEntries(date)
    }


    private fun getWorkTimeEntries(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            val entryListing = worktimeEntryRepository.getWorktimeEntriesByDate(date)
            Log.i("entryListing", entryListing.worktimeEntries?.count().toString())
            Log.i("entryListing", entryListing.status.toString())
            Log.i("date", date.toString())
            if (entryListing.status == ResponseStatus.OK) {
                currentDate.postValue(sdf.format(date))
            }

            if (entryListing.worktimeEntries != null) {
                worktimeEntries.postValue(entryListing.worktimeEntries)
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

}