package dev.szczepaniak.moveit.presenters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.szczepaniak.moveit.R
import dev.szczepaniak.moveit.model.Event

class EventAdapter :RecyclerView.Adapter<EventViewHolder>(){

    var items = emptyList<Event>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_event, parent, false)
        return EventViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) = holder.bind(items[position])

}