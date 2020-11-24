package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.network.responseObjects.projects.listing
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.database.entities.Contact
data class ProjectDto (
    val id: Int,
    val name: String,
    val founder: Contact?,
    val project_manager: Contact?
)