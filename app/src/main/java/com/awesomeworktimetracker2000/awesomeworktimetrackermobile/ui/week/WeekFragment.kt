package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.week


import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.AWTDatabase
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.databinding.WeekFragmentBinding
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.week.WeekViewModel
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.week.WeekViewModelFactory
import kotlinx.android.synthetic.main.week_fragment.*
import kotlinx.coroutines.withTimeout
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

        // observe current weeks worktimeEntries,
        // change color of the corresponding day button if that day has existing entries
        weekViewModel.worktimeEntries.observe(viewLifecycleOwner, Observer {
            setButtonColors(it)
        })

        setOnClickListeners()
        setButtonTexts()



        return binding.root
    }

    /**
     * Get selected date as String and pass that as an argument when navigating to DateFragment
     * requires safeargs enabled in build.gradle
     */
    private fun goToDate(view : View) {
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

    // TODO: use backgroundTint instead of color to not lose animations etc.
    //  make some sort of theme/style? and update colors and whatnot based on that?
    //
    private fun setDefaultButtonColor() {
        binding.btnMonday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_button))
        binding.btnTuesday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_button))
        binding.btnWednesday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_button))
        binding.btnThursday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_button))
        binding.btnFriday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_button))
        binding.btnSaturday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_button))
        binding.btnSunday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_button))

    }

    /**
     * Set button colors to indicate that we have entries
     *
     * TODO: Could we return something else, that we do not have to loop for every entry?
     */
    private fun setButtonColors(entries: List<WorktimeEntry>) {
        for (i in 1..7) {
            val weekDay : String = weekViewModel.getDate(i)
            entries.forEach { entry ->
                val entryDate : String = entry.startedAt.format(weekViewModel.dateTimeFormat2)
                if (weekDay.equals(entryDate)) {
                    if (i == 1) binding.btnMonday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_contains_data_button))
                    if (i == 2) binding.btnTuesday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_contains_data_button))
                    if (i == 3) binding.btnWednesday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_contains_data_button))
                    if (i == 4) binding.btnThursday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_contains_data_button))
                    if (i == 5) binding.btnFriday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_contains_data_button))
                    if (i == 6) binding.btnSaturday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_contains_data_button))
                    if (i == 7) binding.btnSunday.setBackgroundDrawable(resources.getDrawable(R.drawable.weekday_contains_data_button))

                }
                Log.i("entryDate", entryDate)
            }
        }
    }
    /*
     * Set dates and weekdays to buttons
     */
    private fun setButtonTexts() {
        for (i in 1..7) {
            val weekDay: String = weekViewModel.getDate(i)
            binding.btnMonday.setText(weekViewModel.getDate(1) + " " + resources.getString(R.string.monday))
            binding.btnTuesday.setText(weekViewModel.getDate(2) + " " + resources.getString(R.string.tuesday))
            binding.btnWednesday.setText(weekViewModel.getDate(3) + " " + resources.getString(R.string.wednesday))
            binding.btnThursday.setText(weekViewModel.getDate(4) + " " + resources.getString(R.string.thursday))
            binding.btnFriday.setText(weekViewModel.getDate(5) + " " + resources.getString(R.string.friday))
            binding.btnSaturday.setText(weekViewModel.getDate(6) + " " + resources.getString(R.string.saturday))
            binding.btnSunday.setText(weekViewModel.getDate(7) + " " + resources.getString(R.string.sunday))
        }
    }

    /**
     * Set on click listeners for view buttons
     */
    private fun setOnClickListeners() {
        binding.btnMonday.setOnClickListener{ goToDate(it) }
        binding.btnTuesday.setOnClickListener{ goToDate(it) }
        binding.btnWednesday.setOnClickListener{ goToDate(it) }
        binding.btnThursday.setOnClickListener{ goToDate(it) }
        binding.btnFriday.setOnClickListener{ goToDate(it) }
        binding.btnSaturday.setOnClickListener{ goToDate(it) }
        binding.btnSunday.setOnClickListener{ goToDate(it) }

        binding.btnNextWeek.setOnClickListener {
            setDefaultButtonColor()
            weekViewModel.nextWeek()
            setButtonTexts()


            view?.startAnimation(AnimationUtils.makeInAnimation(this.context, false))
        }

        binding.btnPrevWeek.setOnClickListener {
            setDefaultButtonColor()
            weekViewModel.prevWeek()
            setButtonTexts()

            view?.startAnimation(AnimationUtils.makeInAnimation(this.context, true))
        }

    }
}
