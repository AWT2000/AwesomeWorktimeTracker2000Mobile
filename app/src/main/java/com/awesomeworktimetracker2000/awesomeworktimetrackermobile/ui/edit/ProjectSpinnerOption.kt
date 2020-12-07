package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.edit

import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.Project

class ProjectSpinnerOption(
    val project: Project
) {
    override fun toString(): String {
        return this.project.name
    }
}