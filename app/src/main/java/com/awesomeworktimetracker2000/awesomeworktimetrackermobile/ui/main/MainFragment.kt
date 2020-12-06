package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.AWTDatabase
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.databinding.MainFragmentBinding
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.main.MainViewModel
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.main.MainViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: MainFragmentBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.lifecycleOwner = this


        val application = requireNotNull(this.activity).application

        // binding buttons to navigation paths
        binding.btnWeekFragment.setOnClickListener{goToWeekFragment()}
        binding.btnMessages.setOnClickListener{
            Toast.makeText(
                application,
                getString(R.string.feature_not_implemented),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnContacts.setOnClickListener{
            Toast.makeText(
                application,
                getString(R.string.feature_not_implemented),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnProjects.setOnClickListener{
            Toast.makeText(
                application,
                getString(R.string.feature_not_implemented),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnStatistics.setOnClickListener{
            Toast.makeText(
                application,
                getString(R.string.feature_not_implemented),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnSettings.setOnClickListener{
            Toast.makeText(
                application,
                getString(R.string.feature_not_implemented),
                Toast.LENGTH_SHORT
            ).show()
        }

        val awtDatabase = AWTDatabase.getInstance(application)

        val viewModelFactory = MainViewModelFactory(
            worktimeEntryDao = awtDatabase.worktimeEntryDao,
            projectDao = awtDatabase.projectDao,
            userInfoDao = awtDatabase.userDao,
            apiService = AWTApi.service,
            connectionUtils = ConnectionUtils.getInstance(application)
        )

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.syncData()

        return binding.root
    }

    // navigate to weekFragment
    private fun goToWeekFragment() {

        val action = MainFragmentDirections.actionMainFragmentToWeekFragment()

        this.findNavController().navigate(action)
    }

}