package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.week

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.AWTDatabase
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.databinding.WeekFragmentBinding
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.week.WeekViewModel
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.week.WeekViewModelFactory
import kotlinx.android.synthetic.main.week_fragment.*
import java.util.*


class WeekFragment : Fragment() {

    private lateinit var binding: WeekFragmentBinding
    private lateinit var weekViewModel: WeekViewModel
    private val color : String = "#c2daad"

    companion object {
        fun newInstance() = WeekFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // initialize data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.week_fragment, container, false)
        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application

        // initialize view model
        val weekViewModelFactory = WeekViewModelFactory(
            AWTDatabase.getInstance(application).worktimeEntryDao,
            AWTDatabase.getInstance(application).userDao,
            AWTApi.service,
            ConnectionUtils.getInstance(application)
        )
        /**
         * Get selected date as String and pass that as an argument when navigating to DateFragment
         * requires safeargs enabled in build.gradle
         */
        fun goToDate(view : View) {
            lateinit var selectedDate : String
            when (view) {
                binding.btnMonday -> selectedDate = weekViewModel.getDate(1)
                binding.btnTuesday -> selectedDate = weekViewModel.getDate(2)
                binding.btnWednesday -> selectedDate = weekViewModel.getDate(3)
                binding.btnThursday -> selectedDate = weekViewModel.getDate(4)
                binding.btnFriday -> selectedDate = weekViewModel.getDate(5)
                binding.btnSaturday-> selectedDate = weekViewModel.getDate(6)
                binding.btnSunday -> selectedDate = weekViewModel.getDate(7)
            else -> {
                Log.i("selectedDate", "Failed to get selected date")
            }
            }
            val action = WeekFragmentDirections.actionWeekFragmentToDateFragment(selectedDate)
            this.findNavController().navigate(action)
            Log.i("weekday", selectedDate.toString())

        }

        weekViewModel = ViewModelProvider(this, weekViewModelFactory).get(WeekViewModel::class.java)


        weekViewModel.getCachedWorkTimeEntries(weekViewModel.firstDayOfCurrentWeek, weekViewModel.lastDayOfCurrentWeek)

        // observe currentWeekNumber int in WeekViewModel and update tvWeekNumber.text accordingly
        weekViewModel.currentWeekNumber.observe(viewLifecycleOwner, Observer {
            this.tvWeekNumber.text = resources.getString(R.string.week, weekViewModel.currentWeekNumber.value.toString())
        })

        // observe datesBetween string (first and last date of current week dd.MM.yyyy - dd.MM.yyyy)
        weekViewModel.datesBetween.observe(viewLifecycleOwner, Observer {
            this.tvDatesBetween.text = weekViewModel.datesBetween.value.toString()
        })

        /**
         * observe current weeks worktimeEntries,
         * change color of the corresponding day button if that day has existing entries
         * @weekDay = iterate weekdays from monday to sunday
         * @entrydate = date that has a worktimeEntry
          */
        weekViewModel.worktimeEntries.observe(viewLifecycleOwner, Observer {
            for (i in 1..7) {
                val weekDay : String = weekViewModel.getDate(i)
                it.forEach { entry ->
                    val entryDate : String = entry.startedAt.format(weekViewModel.dateTimeFormat2)
                    if (weekDay.equals(entryDate)) {
                        if (i == 1) binding.btnMonday.setBackgroundColor(Color.parseColor(color))
                        if (i == 2) binding.btnTuesday.setBackgroundColor(Color.parseColor(color))
                        if (i == 3) binding.btnWednesday.setBackgroundColor(Color.parseColor(color))
                        if (i == 4) binding.btnThursday.setBackgroundColor(Color.parseColor(color))
                        if (i == 5) binding.btnFriday.setBackgroundColor(Color.parseColor(color))
                        if (i == 6) binding.btnSaturday.setBackgroundColor(Color.parseColor(color))
                        if (i == 7) binding.btnSunday.setBackgroundColor(Color.parseColor(color))
                    }
                    Log.i("entryDate", entryDate)
                }
            }
        })

        // TODO: use backgroundTint instead of color to not lose animations etc.
        //  make some sort of theme/style? and update colors and whatnot based on that?
        //
        fun setDefaultButtonColor() {
            binding.btnMonday.setBackgroundColor(Color.LTGRAY)
            binding.btnTuesday.setBackgroundColor(Color.LTGRAY)
            binding.btnWednesday.setBackgroundColor(Color.LTGRAY)
            binding.btnThursday.setBackgroundColor(Color.LTGRAY)
            binding.btnFriday.setBackgroundColor(Color.LTGRAY)
            binding.btnSaturday.setBackgroundColor(Color.LTGRAY)
            binding.btnSunday.setBackgroundColor(Color.LTGRAY)
        }
        binding.btnNextWeek.setOnClickListener {
            setDefaultButtonColor()
            weekViewModel.nextWeek()
        }
        binding.btnPrevWeek.setOnClickListener {
            setDefaultButtonColor()
            weekViewModel.prevWeek()
        }

        binding.btnMonday.setOnClickListener{ goToDate(it) }
        binding.btnTuesday.setOnClickListener{ goToDate(it) }
        binding.btnWednesday.setOnClickListener{ goToDate(it) }
        binding.btnThursday.setOnClickListener{ goToDate(it) }
        binding.btnFriday.setOnClickListener{ goToDate(it) }
        binding.btnSaturday.setOnClickListener{ goToDate(it) }
        binding.btnSunday.setOnClickListener{ goToDate(it) }

        return binding.root
    }

}
