package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.worktimeEntries.single

data class GetWorktimeEntryResponseDto(
    val collides_with_other_entries: Boolean,
    val created_at: String,
    val ended_at: String,
    val id: Int,
    val project_id: Int?,
    val started_at: String,
    val updated_at: String
)