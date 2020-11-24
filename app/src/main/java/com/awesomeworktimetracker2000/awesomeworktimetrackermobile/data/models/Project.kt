package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.Contact

data class Project (
    val id: Int,
    val name: String,
    val founder: Contact?,
    val project_manager: Contact?,
    val synced: Boolean = false
)