package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.worktimeEntries.listing

data class worktimeEntryListingResponseDto(
    val data: List<WorktimeEntryDto>,
    val links: Links,
    val meta: Meta
)