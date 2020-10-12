package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.requestObjects.worktimeEntries

class SaveWorktimeEntryRequest(
    val project_id: Int? = null,
    val started_at: String,
    val ended_at: String
) {
}