package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.edit

import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.AWTDatabase
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.databinding.EditFragmentBinding
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils.convertOffsetDateTimeToString
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils.localOffset
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.edit.EditViewModel
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.edit.EditViewModelFactory
import kotlinx.android.synthetic.main.edit_fragment.*
import java.lang.Exception
import java.time.OffsetDateTime
import java.util.*


class EditFragment : Fragment() {

    private lateinit var binding: EditFragmentBinding
    private lateinit var application: Application

    private var savedDay = 0;
    private var savedMonth = 0;
    private var savedYear = 0;

    private var start: Boolean = false

    private val args: EditFragmentArgs by navArgs()

    companion object {
        fun newInstance() = EditFragment()
    }

    private lateinit var editViewModel: EditViewModel

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

        val worktimeEntryId = args.worktimeEntryId
        val passedDate: Date? = args.date
        Log.i("passedDate", "$passedDate")
        Log.i("passedEntryId", "$worktimeEntryId")

        val editViewMOdelFactory = EditViewModelFactory(
            AWTDatabase.getInstance(application).worktimeEntryDao,
            AWTDatabase.getInstance(application).projectDao,
            AWTDatabase.getInstance(application).userDao,
            AWTApi.service,
            ConnectionUtils.getInstance(application),
            worktimeEntryId
        )

        editViewModel = ViewModelProvider(this, editViewMOdelFactory).get(EditViewModel::class.java)

        editViewModel.editTitle.observe(viewLifecycleOwner, Observer {
            tvEditTitle.text = editViewModel.editTitle.value.toString()
        })

        editViewModel.projectNames.observe(viewLifecycleOwner, Observer { projects ->
            val spinnerAdapter = ArrayAdapter(
                application,
                android.R.layout.simple_spinner_item,
                projects
            )
            spnrProject.adapter = spinnerAdapter
        })

        val selectedProjectId: Int = editViewModel.selectedProjectId
        Log.i("selectedProjectId", "${selectedProjectId}")

        setEditViews()
        setOnClickListeners()

        return binding.root
    }


    private fun setEditViews() {
            try {
                val offsetDateTime: OffsetDateTime = args.date!!
                    .toInstant()
                    .atOffset(localOffset)
                editViewModel.getProjects()
                editViewModel.pickDateTime(offsetDateTime, args.worktimeEntryId)

                // TODO: set element returned to keys variable as spnrProject value
                //  At the moment projectsMap returns null.
                //  suspend this until editViewModel.getProjects() has been run?
                Log.i("EditViewModel", "editViewModel.projectsMap size: ${editViewModel.projectsMap.size}")
                val keys =
                    editViewModel.projectsMap.filterValues { it == editViewModel.selectedProjectId }.keys
                Log.i("EditViewModel", "Selected project: ${keys.elementAt(0).toInt()}")
                spnrProject.setSelection(keys.elementAt(0).toInt())

                // TODO: startDate, endDate null at this point
                edit_start_datetime.text =
                    convertOffsetDateTimeToString(editViewModel.startDate)
                edit_end_datetime.text =
                    convertOffsetDateTimeToString(editViewModel.endDate)


            } catch (e: Exception) {
                Log.i("EditFragment", "Exception: ${e.message}")
            } finally {

            }
    }

    private fun setOnClickListeners() {

        var startDateListener: OnDateSetListener
        var endDateListener: OnDateSetListener

        binding.editStartDatetime.setOnClickListener {
            val editStartDateTime = DatePickerDialog(
                requireNotNull(this.context),
                AlertDialog.THEME_HOLO_DARK,
                startDateSetListener,
                editViewModel.startDate.year,
                editViewModel.startDate.monthValue - 1,
                editViewModel.startDate.dayOfMonth
            ).show()
        }

        binding.editEndDatetime.setOnClickListener {
            val editEndDateTime = DatePickerDialog(
                requireNotNull(this.context),
                AlertDialog.THEME_HOLO_DARK,
                endDateSetListener,
                editViewModel.endDate.year,
                editViewModel.endDate.monthValue - 1,
                editViewModel.endDate.dayOfMonth
            ).show()
        }

        binding.btnSave.setOnClickListener {
            Log.i("buttonSave", "${editViewModel.startDate}")
            Log.i("buttonSave", "${editViewModel.endDate}")

            editViewModel.selectedProjectId =
                editViewModel.projectsMap[spnrProject.selectedItem.toString()]!!
            Log.i("selectedProjectId", "${editViewModel.selectedProjectId}")
            editViewModel.saveWorkTimeEntry()
        }

        binding.btnDelete.setOnClickListener {
            editViewModel.deleteWorkTimeEntry()
        }
    }

    private val startDateSetListener =
        OnDateSetListener { _, year, month, day ->
            savedYear = year
            savedMonth = month + 1
            savedDay = day
            TimePickerDialog(
                requireNotNull(this.context),
                AlertDialog.THEME_HOLO_DARK,
                startTimeSetListener,
                editViewModel.startDate.hour,
                editViewModel.startDate.minute,
                true
            ).show()
        }

    private val endDateSetListener =
        OnDateSetListener { _, year, month, day ->
            savedYear = year
            savedMonth = month + 1
            savedDay = day
            TimePickerDialog(
                requireNotNull(this.context),
                AlertDialog.THEME_HOLO_DARK,
                endTimeSetListener,
                editViewModel.endDate.hour,
                editViewModel.endDate.minute,
                true
            ).show()
        }

    private val startTimeSetListener =
        OnTimeSetListener { _, hour, minute ->
            start = true
            editViewModel.setDate(start, savedDay, savedMonth, savedYear, hour, minute)
            edit_start_datetime.text =
                convertOffsetDateTimeToString(editViewModel.startDate)
        }
    private val endTimeSetListener =
        OnTimeSetListener { _, hour, minute ->
            start = false
            editViewModel.setDate(start, savedDay, savedMonth, savedYear, hour, minute)
            edit_end_datetime.text =
                convertOffsetDateTimeToString(editViewModel.endDate)
        }
}