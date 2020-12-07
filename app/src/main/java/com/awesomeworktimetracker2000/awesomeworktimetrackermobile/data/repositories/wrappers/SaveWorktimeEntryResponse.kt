package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry

class SaveWorktimeEntryResponse(
    val status: ResponseStatus,
    val worktimeEntry: WorktimeEntry? = null
) {
}