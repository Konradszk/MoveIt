package dev.szczepaniak.moveit.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.szczepaniak.moveit.R
import dev.szczepaniak.moveit.controller.MoveItPref
import dev.szczepaniak.moveit.controller.AlarmController
import dev.szczepaniak.moveit.presenters.EventProvider
import kotlinx.android.synthetic.main.dashboard_layout.*
import logd
import java.util.*


class DashboardFragment : Fragment() {

    private val TAG = "DASH_FRA"
    private val alarmController: AlarmController by lazy { AlarmController(context!!) }
    private val eventProvider: EventProvider by lazy { EventProvider(context!!) }
    private val moveItPref: MoveItPref by lazy {
        MoveItPref(
            this.activity!!,
            0L
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dashboard_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val time = java.util.Calendar.getInstance().time

        button_start.setOnClickListener {
            eventProvider.getEvents(time)?.forEach {
                alarmController.addAlarm(it.startDate, it.location)
            }
            val maxId: Long? = this.eventProvider.getMaxId(time)
            moveItPref.maxEventId = maxId!!
            button_start.visibility = View.GONE
            move_it_app_info.visibility = View.GONE
        }
    }

    private fun addNewEvents(time: Date) {
        if (moveItPref.maxEventId != 0L) {
            button_start.visibility = View.GONE
            move_it_app_info.visibility = View.GONE
            val maxId: Long? = this.eventProvider.getMaxId(time)

            if (maxId != null && moveItPref.maxEventId < maxId) {
                this.eventProvider.getEvents()!!
                    .filter { event -> event.id > moveItPref.maxEventId }
                    .forEach { event ->
                        this.alarmController.addAlarm(event.startDate, event.location)
                    }
                moveItPref.maxEventId = maxId
                maxId.logd(TAG)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val time = java.util.Calendar.getInstance().time
        addNewEvents(time)
    }

    companion object {
        fun create(): DashboardFragment {
            val fragment = DashboardFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}