package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.startup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.databinding.StartupFragmentBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.AWTDatabase
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.services.AWTApi
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.ConnectionUtils
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.startup.StartupViewModel
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.startup.StartupViewModelFactory

class StartupFragment : Fragment() {
    private val LOG_TAG: String = "StartupFragment";

    private lateinit var viewModel: StartupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(LOG_TAG, "on create view")

        // initialize binding
        val binding = DataBindingUtil.inflate<StartupFragmentBinding>(inflater, R.layout.startup_fragment, container, false)

        val application = requireNotNull(this.activity).application

        // initialize view model
        val viewModelFactory = StartupViewModelFactory(
            AWTDatabase.getInstance(application).userDao,
            AWTApi.service,
            ConnectionUtils.getInstance(application)
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(StartupViewModel::class.java)

        // observe view model's live data
        viewModel.hasValidUserInfo.observe(viewLifecycleOwner, Observer { hasValidUserInfo ->
            if (hasValidUserInfo == true) {
                this.findNavController().navigate(R.id.action_startupFragment_to_mainFragment)
            } else {
                this.findNavController().navigate(R.id.action_startupFragment_to_loginFragment)
            }
        })

        // TODO: start coroutine that will sync work time entries

        viewModel.tryLoginWithCache()

        return binding.root
    }
}