package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.edit

import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBinderMapperImpl
import androidx.databinding.DataBindingUtil
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.databinding.EditFragmentBinding
import kotlinx.android.synthetic.main.edit_fragment.*
import java.time.Month


class EditFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: EditFragmentBinding
    private lateinit var application: Application

    private var day = 0;
    private var month = 0;
    private var year = 0;
    private var hour = 0;
    private var minute = 0;
    private var savedDay = 0;
    private var savedMonth = 0;
    private var savedYear = 0;
    private var savedHour = 0;
    private var savedMinute = 0;

    private var start = false
    private var end = false



    companion object {
        fun newInstance() = EditFragment()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_fragment, container, false)
        binding.lifecycleOwner = this

        application = requireNotNull(this.activity).application

        pickDateTime()


        return binding.root
    }

    private fun getDateTimeCalendar() {
        val cal = java.util.Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)

        DatePickerDialog(requireNotNull(this.context), AlertDialog.THEME_HOLO_DARK, this, year, month, day)


    }

    private fun pickDateTime() {
        binding.editStartDatetime.setOnClickListener {
            getDateTimeCalendar()
            start = true
            DatePickerDialog(requireNotNull(this.context), AlertDialog.THEME_HOLO_DARK, this, year, month, day).show()
        }

        binding.editEndDatetime.setOnClickListener {
            getDateTimeCalendar()
            end = true
            DatePickerDialog(requireNotNull(this.context), AlertDialog.THEME_HOLO_DARK, this, year, month, day).show()
        }
    }



    override fun onDateSet(view: DatePicker?, Year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()
        TimePickerDialog(requireNotNull(this.context), AlertDialog.THEME_HOLO_DARK, this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        if (start) {
            edit_start_datetime.text =
                "$savedDay-$savedMonth-$savedYear $savedHour:$savedMinute"
        }

        if (end) {
            edit_end_datetime.text =
                "$savedDay-$savedMonth-$savedYear $savedHour:$savedMinute"
        }

        start = false
        end = false
    }


}