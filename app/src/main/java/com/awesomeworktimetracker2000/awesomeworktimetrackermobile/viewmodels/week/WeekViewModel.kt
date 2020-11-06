package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.week

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters.nextOrSame
import java.time.temporal.TemporalAdjusters.previousOrSame
import java.time.temporal.WeekFields
import java.util.*

class WeekViewModel(private val worktimeEntryRepository: WorktimeEntryRepository) : ViewModel() {

    // dateTimeFormat used to display date in WeekFragment
    private val dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    // dateTimeFormat used to pass date to DateFragment (getWorkTimeEntries) & getCachedWorkTimeEntries
    val dateTimeFormat2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // initialize current date, and then initialize first and last day of the current week based on it
    private var currentDate : LocalDate = LocalDate.now()
    private var firstDayOfCurrentWeek : LocalDate  = currentDate.with(previousOrSame(DayOfWeek.MONDAY))
    private var lastDayOfCurrentWeek : LocalDate = currentDate.with(nextOrSame(DayOfWeek.SUNDAY))

    // The ISO-8601 definition, where a week starts on Monday and the first week has a minimum of 4 days.
    private var weekFields: WeekFields = WeekFields.ISO
    private var weekNumber: Int = currentDate.get(weekFields.weekOfWeekBasedYear())

    private val _currentWeekNumber = MutableLiveData<Int>(weekNumber)
    val currentWeekNumber: LiveData<Int>
        get() = _currentWeekNumber

    // worktimeEntries for the current week, observed in WeekFragment
    private var _worktimeEntries = MutableLiveData<List<WorktimeEntry>>()
    val worktimeEntries: LiveData<List<WorktimeEntry>>
        get() = _worktimeEntries

    // first and last date of current week dd.MM.yyyy - dd.MM.yyyy, observed in WeekFragment
    private var _datesBetween = MutableLiveData<String>(firstDayOfCurrentWeek.format(dateTimeFormat) +
            " - " + lastDayOfCurrentWeek.format(dateTimeFormat))
    val datesBetween: LiveData<String>
        get() = _datesBetween


    init {
        Log.i("WeekViewModel", "WeekViewModel created")
        Log.i("firstDayOfCurrentWeek", firstDayOfCurrentWeek.format(dateTimeFormat).toString())
        Log.i("lastDayOfCurrentWeek", lastDayOfCurrentWeek.format(dateTimeFormat).toString())
    }

    /**
     * get a selected date in string format
     *
     * @param int - representing the day in week (1=monday, 2=tuesday, etc..)
     * @return string - current weeks date in string format (dd.MM.yyyy) based on the received integer
     */
    fun getDate(day : Int): String{
        val newLocalDate : LocalDate = firstDayOfCurrentWeek.with(nextOrSame(DayOfWeek.of(day)))
        val dateString : String = newLocalDate.format(dateTimeFormat2)
        return dateString
    }

    /**
     *  update _worktimeEntries with a list of current weeks worktimeEntries
     *
     *  @param localDateFrom - first date of the week
     *  @param localDateTo - last date of the week
     */
    fun getCachedWorkTimeEntries(localDateFrom: LocalDate, localDateTo: LocalDate) {

        viewModelScope.launch(Dispatchers.IO) {

            val entryListing = worktimeEntryRepository.getCachedWorktimeEntriesBetweenDateTimes(localDateFrom.format(dateTimeFormat2).toString(), localDateTo.format(dateTimeFormat2).toString())
            Log.i("WeekWorkTimeEntries", entryListing.worktimeEntries?.count().toString())
            Log.i("dateFrom", localDateFrom.format(dateTimeFormat2))
            Log.i("dateTo", localDateTo.format(dateTimeFormat2))

            if (entryListing.worktimeEntries != null) {
                _worktimeEntries.postValue(entryListing.worktimeEntries)
            }
        }
    }

    fun nextWeek() {
        firstDayOfCurrentWeek = firstDayOfCurrentWeek.plusWeeks(1)
        lastDayOfCurrentWeek = lastDayOfCurrentWeek.plusWeeks(1)
        weekNumber = firstDayOfCurrentWeek.get(weekFields.weekOfWeekBasedYear())
        _currentWeekNumber.postValue(weekNumber)

        _datesBetween.postValue(firstDayOfCurrentWeek.format(dateTimeFormat) +
                " - " + lastDayOfCurrentWeek.format(dateTimeFormat))
        Log.i("Week", weekNumber.toString())
        getCachedWorkTimeEntries(firstDayOfCurrentWeek, lastDayOfCurrentWeek)
    }
    fun prevWeek() {
        firstDayOfCurrentWeek = firstDayOfCurrentWeek.minusWeeks(1)
        lastDayOfCurrentWeek = lastDayOfCurrentWeek.minusWeeks(1)
        weekNumber = firstDayOfCurrentWeek.get(weekFields.weekOfWeekBasedYear())
        _currentWeekNumber.postValue(weekNumber)

        _datesBetween.postValue(firstDayOfCurrentWeek.format(dateTimeFormat) +
                " - " + lastDayOfCurrentWeek.format(dateTimeFormat))
        Log.i("Week", weekNumber.toString())
        getCachedWorkTimeEntries(firstDayOfCurrentWeek, lastDayOfCurrentWeek)
    }
}