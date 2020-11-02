package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.edit

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

    var day = 0;
    var month = 0;
    var year = 0;
    var hour = 0;
    var minute = 0;

    var savedDay = 0;
    var savedMonth = 0;
    var savedYear = 0;
    var savedHour = 0;
    var savedMinute = 0;



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

    }

    private fun pickDateTime() {
        binding.editStartDatetime.setOnClickListener {
            getDateTimeCalendar()

            DatePickerDialog(requireNotNull(this.context), this, year, month, day).show()
        }

        binding.editEndDatetime.setOnClickListener {
            getDateTimeCalendar()

            DatePickerDialog(requireNotNull(this.context), this, year, month, day).show()
        }
    }



    override fun onDateSet(view: DatePicker?, Year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()
        TimePickerDialog(requireNotNull(this.context), this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        edit_start_datetime.text = "$savedDay-$savedMonth-$savedYear $savedHour:$savedMinute" // TODO: This updates only the value of the start time atm. Need to fix.
    }


}