package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.viewmodels.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.Project
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.ProjectRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.WorktimeEntryRepository
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditViewModel(
    private val worktimeEntryRepository: WorktimeEntryRepository,
    private val projectRepository: ProjectRepository,
    private val worktimeEntryId: Int = 0
): ViewModel() {

    private lateinit var worktimeEntry: WorktimeEntry

    init {
        if (worktimeEntryId != 0) {
            viewModelScope.launch(Dispatchers.IO) {
                val worktimeEntryResponse = worktimeEntryRepository.getWorktimeEntryById(this@EditViewModel.worktimeEntryId)

                if (worktimeEntryResponse.status == ResponseStatus.OK
                    || worktimeEntryResponse.status == ResponseStatus.OFFLINE) {
                    Log.i("editViewModel", "It works!")
                    Log.i("editViewModel", worktimeEntryResponse.entry!!.id.toString())

                    this@EditViewModel.worktimeEntry = worktimeEntryResponse.entry!!
                }
            }
        }
    }

}