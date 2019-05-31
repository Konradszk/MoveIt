package dev.szczepaniak.moveit.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.szczepaniak.moveit.controller.AlarmController
import dev.szczepaniak.moveit.presenters.EventAdapter
import dev.szczepaniak.moveit.presenters.EventProvider
import dev.szczepaniak.moveit.presenters.LocationProvider
import kotlinx.android.synthetic.main.event_layout.*


const val RECORD_REQUEST_CODE = 101


class EventFragment : Fragment() {

    private val TAG = "EVENT_FRAGMENT"

    private val adapter = EventAdapter()
    private val alarmController: AlarmController by lazy { AlarmController(context!!) }
    private val eventProvider: EventProvider by lazy { EventProvider(context!!) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(dev.szczepaniak.moveit.R.layout.event_layout, container, false)

    override fun onResume() {
        val time = java.util.Calendar.getInstance().time
        adapter.items = eventProvider.getEvents(time)!!
        super.onResume()
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LocationProvider.setUpPermission(context!!, activity!!)
        EventProvider.setUpPermission(context!!, activity!!)
        recyclerView.adapter = adapter
        super.onActivityCreated(savedInstanceState)
    }

    companion object {
        fun create(): EventFragment {
            val fragment = EventFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
