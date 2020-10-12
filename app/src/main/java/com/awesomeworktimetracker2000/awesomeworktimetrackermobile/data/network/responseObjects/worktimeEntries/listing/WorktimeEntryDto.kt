package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.worktimeEntries.listing

data class WorktimeEntryDto(
    val created_at: String,
    val ended_at: String,
    val id: Int,
    val project_id: Int?,
    val started_at: String,
    val updated_at: String
)