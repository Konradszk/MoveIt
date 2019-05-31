package dev.szczepaniak.moveit.presenters

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import dev.szczepaniak.moveit.activities.MoveEventActivity
import dev.szczepaniak.moveit.model.Event
import dev.szczepaniak.moveit.utils.dateToHourAndMinutes
import kotlinx.android.synthetic.main.viewholder_event.view.*

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun bind(event: Event) = with(itemView) {
        txtTitle.text = event.name
        txtStart.text = dateToHourAndMinutes(event.startDate)
        txtLocation.text = event.location
        moveItBtn.setOnClickListener {
            val intent = Intent(itemView.context, MoveEventActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            intent.putExtra("NEXT_EVENT_ID", event.nextEventId)
            this.context.startActivity(intent)
        }
        sendEmailBtn.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:" + event.participants.joinToString(separator = ";"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, event.name)
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
    }

}