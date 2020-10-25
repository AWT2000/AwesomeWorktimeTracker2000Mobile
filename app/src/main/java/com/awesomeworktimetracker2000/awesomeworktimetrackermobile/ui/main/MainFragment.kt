package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.AWTDatabase
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val application = requireNotNull(this.activity).application

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

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val entryListing = weRepo.getWorktimeEntriesByDate(SimpleDateFormat("yyyy-MM-dd").parse("2020-10-05"))

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