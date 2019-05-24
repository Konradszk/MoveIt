package dev.szczepaniak.moveit.presenters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import dev.szczepaniak.moveit.model.Event
import kotlinx.android.synthetic.main.viewholder_event.view.*
import java.text.SimpleDateFormat
import java.util.*

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun bind(event: Event) = with(itemView) {
        txtTitle.text = event.name
        txtStart.text = dateToHourAndMinutes(event.startDate)
        txtLocation.text = event.location
        moveItBtn.setOnClickListener {
            Toast.makeText(context, "Move it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dateToHourAndMinutes(date: Date): String {
        val dateFormat = SimpleDateFormat("hh:mm")
        return dateFormat.format(date)
    }
}