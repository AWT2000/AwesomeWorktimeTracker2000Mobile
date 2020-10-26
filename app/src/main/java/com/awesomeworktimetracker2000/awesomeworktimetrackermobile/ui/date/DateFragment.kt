package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.date

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R


class DateFragment : Fragment() {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.date_fragment, container, false)
    }


}