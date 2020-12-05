package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.date

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.databinding.ListItemWorktimeEntryBinding
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils.DateUtils

class WorktimeEntryAdapter (val onClicListener: (Int) -> Unit): RecyclerView.Adapter<WorktimeEntryAdapter.ViewHolder>() {
    var data = listOf<WorktimeEntry>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val resources = holder.itemView.context.resources

        // TODO: change to show project name
        holder.projectNameTextView.text = if (item.project != null) {
            "${item.project!!.name}"
            //resources.getString(R.string.worktime_entry_list_item_project, "Projekti ${item.projectId}")
        } else {
            resources.getString(R.string.worktime_entry_list_item_project_no_project)
        }

        holder.startAndEndTimeTextView.text = resources.getString(
            R.string.worktime_entry_list_item_start_and_end_times,
            DateUtils.convertOffsetDateTimeToLocalTimeString(item.startedAt),
            DateUtils.convertOffsetDateTimeToLocalTimeString(item.endedAt)
        )

        holder.itemView.setOnClickListener {
            onClicListener(item.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = ListItemWorktimeEntryBinding
            .inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    class ViewHolder(binding: ListItemWorktimeEntryBinding) : RecyclerView.ViewHolder(binding.root){
        val projectNameTextView: TextView = binding.textViewProjectName
        val startAndEndTimeTextView: TextView = binding.textViewStartAndEndDatetime
    }

}