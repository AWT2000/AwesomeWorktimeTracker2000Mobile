package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models

import java.time.OffsetDateTime

data class WorktimeEntry(
    val id: Int,
    val startedAt: OffsetDateTime,
    val endedAt: OffsetDateTime,
    val projectId: Int?,
    val externalId: Int?,
    val synced: Boolean = false
) {
}