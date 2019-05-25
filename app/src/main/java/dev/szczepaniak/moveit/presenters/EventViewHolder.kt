package dev.szczepaniak.moveit.presenters

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import dev.szczepaniak.moveit.model.Event
import kotlinx.android.synthetic.main.viewholder_event.view.*
import java.text.SimpleDateFormat
import java.util.*

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val eventProvider: EventProvider by lazy { EventProvider(itemView.context) }

    fun bind(event: Event) = with(itemView) {
        txtTitle.text = event.name
        txtStart.text = dateToHourAndMinutes(event.startDate)
        txtLocation.text = event.location
        moveItBtn.setOnClickListener {
            eventProvider.test()
        }
        sendEmailBtn.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:" + event.participants.joinToString(separator = ";"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, event.name)
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
    }

    private fun dateToHourAndMinutes(date: Date): String {
        val dateFormat = SimpleDateFormat("hh:mm")
        return dateFormat.format(date)
    }
}