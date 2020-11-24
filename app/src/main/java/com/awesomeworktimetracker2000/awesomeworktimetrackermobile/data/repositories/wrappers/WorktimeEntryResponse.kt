package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry

class WorktimeEntryResponse(
    val status: ResponseStatus,
    val entry: WorktimeEntry? = null
) {
}