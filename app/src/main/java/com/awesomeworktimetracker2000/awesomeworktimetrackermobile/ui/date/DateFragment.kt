package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.date

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

    private lateinit var dateViewModel: DateViewModel

    // get selectedDate string (dd.MM.yyyy) as argument from WeekFragment
    private val args: DateFragmentArgs by navArgs()

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
            AWTDatabase.getInstance(application).projectDao,
            AWTDatabase.getInstance(application).userDao,
            AWTApi.service,
            ConnectionUtils.getInstance(application)
        )

        dateViewModel = ViewModelProvider(this, dateViewModelFactory).get(DateViewModel::class.java)

        // Observe currentDate in DateViewModel and update tvDate.text accordingly
        dateViewModel.currentDateString.observe(viewLifecycleOwner, Observer {
            this.tvDate.text = dateViewModel.currentDateString.value.toString()
        })

        binding.btnNextDate.setOnClickListener {
            dateViewModel.nextDate()
            view?.startAnimation(AnimationUtils.makeInAnimation(this.context, false))
        }
        binding.btnPrevDate.setOnClickListener {
            dateViewModel.prevDate()
            view?.startAnimation(AnimationUtils.makeInAnimation(this.context, true))
        }

        // create an adapter and associate the adapter with recyclerview
        val workTimeEntryAdapter = WorktimeEntryAdapter {
            this.goToEditFragment(it)
        }

        // bind floating action button to navigate to editfragment
        binding.fabAdd.setOnClickListener{goToEditFragment()}
        binding.rvDay.apply {
            adapter = workTimeEntryAdapter
        }

        dateViewModel.worktimeEntries.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                workTimeEntryAdapter.data = list
            }
        })

        dateViewModel.getWorkTimeEntries(SimpleDateFormat("yyyy-MM-dd").parse(args.selectedDate.toString()))

        return binding.root
    }

    // navigate to editfragment
    // TODO: pass current date to editfragment
    private fun goToEditFragment(entryId: Int? = null) {

        val passableWorktimeEntryId = entryId ?: 0
        val passableCurrentDate = dateViewModel.date

        val action = DateFragmentDirections
            .actionDateFragmentToEditFragment()
            .setWorktimeEntryId(passableWorktimeEntryId)
            .setDate(passableCurrentDate)

        this.findNavController().navigate(action);
    }

}