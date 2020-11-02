package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.date

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.AWTDatabase
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.databinding.DateFragmentBinding
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.date.DateViewModel
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.date.DateViewModelFactory
import kotlinx.android.synthetic.main.date_fragment.*
import java.text.SimpleDateFormat


class DateFragment : Fragment() {

    private lateinit var binding: DateFragmentBinding

    private lateinit var dateViewmModel: DateViewModel

    companion object {
        fun newInstance() = DateFragment()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.date_fragment, container, false)
        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application

        // initialize view model
        val dateViewModelFactory = DateViewModelFactory(
            AWTDatabase.getInstance(application).worktimeEntryDao,
            AWTDatabase.getInstance(application).userDao,
            AWTApi.service,
            ConnectionUtils.getInstance(application)
        )

        dateViewmModel = ViewModelProvider(this, dateViewModelFactory).get(DateViewModel::class.java)

        dateViewmModel.worktimeEntries.observe(viewLifecycleOwner, Observer {
            it.forEach { entry ->
                Log.i("worktimeEntries", "external id: ${entry.externalId}, started_at: "
                        + "${entry.startedAt.format(DateUtils.isoDateFormatter)}, "
                        + "ended_at: ${entry.endedAt.format(DateUtils.isoDateFormatter)}" )
            }
        })

        // Observe currentDate in DateViewModel and update tvDate.text accordingly
        dateViewmModel.currentDateString.observe(viewLifecycleOwner, Observer {
            this.tvDate.text = dateViewmModel.currentDateString.value.toString()
        })

        binding.btnNextDate.setOnClickListener {
            dateViewmModel.nextDate()
        }
        binding.btnPrevDate.setOnClickListener {
            dateViewmModel.prevDate()
        }

        dateViewmModel.getWorkTimeEntries(SimpleDateFormat("yyyy-MM-dd").parse("2020-10-05"))

        return binding.root
    }


}