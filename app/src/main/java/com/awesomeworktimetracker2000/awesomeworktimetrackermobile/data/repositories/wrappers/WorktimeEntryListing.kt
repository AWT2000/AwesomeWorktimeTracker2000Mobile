package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry

data class WorktimeEntryListing(
    val status: ResponseStatus,
    val worktimeEntries: List<WorktimeEntry>? = null
) {
}