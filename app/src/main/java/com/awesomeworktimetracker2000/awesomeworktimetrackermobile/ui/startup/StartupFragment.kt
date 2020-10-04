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
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.startup.StartupViewModel

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

        // initialize view model
        viewModel = ViewModelProvider(this).get(StartupViewModel::class.java)

        // observe view model's live data
        viewModel.hasValidUserInfo.observe(viewLifecycleOwner, Observer { hasValidUserInfo ->
            // TODO: check if has valid user info
            // for now just navigate
            this.findNavController().navigate(R.id.action_startupFragment_to_loginFragment)
        })

        tryLogin()

        return binding.root
    }

    private fun tryLogin() {
        viewModel.tryLogin()
    }

}