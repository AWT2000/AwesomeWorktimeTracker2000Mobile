package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.Project

data class ProjectListing (
    val status: ResponseStatus,
    val projects: List<Project>? = null
){}