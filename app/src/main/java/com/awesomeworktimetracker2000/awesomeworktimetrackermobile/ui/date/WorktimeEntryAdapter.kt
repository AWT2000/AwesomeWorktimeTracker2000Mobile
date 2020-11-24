package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.ui.date

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.R
import com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.models.WorktimeEntry


class WorktimeEntryAdapter: RecyclerView.Adapter<WorktimeEntryAdapter.ViewHolder>() {
    var data = listOf<WorktimeEntry>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val res = holder.itemView.context.resources
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.list_item_worktime_entry, parent, false)

        return ViewHolder(view)

    }

    // create a viewholder
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val projectName: TextView = itemView.findViewById(R.id.project_name)
        val startAndEndTime: TextView = itemView.findViewById(R.id.start_and_end_datetime)

    }

}