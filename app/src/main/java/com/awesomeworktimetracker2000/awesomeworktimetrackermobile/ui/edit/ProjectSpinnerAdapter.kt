package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.edit

import android.content.Context
import android.widget.ArrayAdapter

class ProjectSpinnerAdapter(
    context: Context,
    resource: Int,
    items: List<ProjectSpinnerOption>
): ArrayAdapter<ProjectSpinnerOption>(
    context,
    resource,
    items
) {
    fun getItemById(id: Int): ProjectSpinnerOption? {
        for (i in 0 until count) {
            val item = getItem(i) as ProjectSpinnerOption

            if (item.project.id == id) {
                return item
            }
        }
        return null
    }

    fun getIndexOfItemById(id: Int): Int {
        for (i in 0 until count) {
            val item = getItem(i) as ProjectSpinnerOption

            if (item.project.id == id) {
                return i
            }
        }
        return -1
    }
}