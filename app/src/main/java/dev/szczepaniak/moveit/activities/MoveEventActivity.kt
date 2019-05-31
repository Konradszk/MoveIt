package dev.szczepaniak.moveit.activities

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import dev.szczepaniak.moveit.R
import dev.szczepaniak.moveit.model.Event
import dev.szczepaniak.moveit.presenters.EventProvider
import dev.szczepaniak.moveit.presenters.GeoDistanceProvider
import dev.szczepaniak.moveit.presenters.LocationProvider.Companion.getPlaceLocation
import dev.szczepaniak.moveit.presenters.LocationProvider.Companion.getUserLocation
import dev.szczepaniak.moveit.presenters.MoveEventI
import dev.szczepaniak.moveit.utils.dateToHourAndMinutes
import kotlinx.android.synthetic.main.activity_move_event.*


class MoveEventActivity : AppCompatActivity(),MoveEventI {

    private val TAG = "MOVE_EVENT_ACT"
    private val eventProvider: EventProvider by lazy { EventProvider(context = this.applicationContext) }
    private val distanceProvider: GeoDistanceProvider by lazy { GeoDistanceProvider(this, this) }


    private lateinit var nextEvent: Event
    private lateinit var event: Event
    private var selectedMinute = 0
    private var selectedHour = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_move_event)
        val eventId = intent.getLongExtra("EVENT_ID", 0)
        val nextEventId = intent.getLongExtra("NEXT_EVENT_ID", 0)
        initView(eventId, nextEventId)
    }

    private fun initView(eventId: Long, nextEventId: Long) {
        event= eventProvider.getEvent(eventId)

        eventName.setText(event.name)
        eventLocation.setText(event.location)
        eventEnd.setText(dateToHourAndMinutes(event.endDate))

        if (nextEventId != 0L) {
            this.nextEvent = eventProvider.getEvent(nextEventId)

            nextEventName.setText(nextEvent.name)
            nextEventStart.setText(dateToHourAndMinutes(nextEvent.startDate))
            nextEventLocation.setText(nextEvent.location)

        } else {
            move_button.isEnabled = false
            nextEventName.visibility = View.GONE
            nextEventStart.visibility = View.GONE
            nextEventLocation.visibility = View.GONE
            nextEventLabel.visibility = View.GONE
        }
    }

    fun clickTimePicker(view: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { _, h, m ->
            this.selectedHour = h
            this.selectedMinute = m
            chooseTime.setText("$h : $m")
        }), hour, minute, false)

        tpd.show()
    }

    fun moveEvent(view: View) {
//        this.eventProvider.moveEvent(this.nextEventId, this.selectedHour, this.selectedMinute)
        getUserLocation(applicationContext) {
            val destination = getPlaceLocation(nextEvent.location, context = applicationContext)

            this.distanceProvider.execute(
                "${it.latitude},${it.longitude}",
                "${destination?.latitude},${destination?.longitude}",
                "driving",
                "${event.endDate.time}"
            )
        }
    }

    override fun moveEventAI(value: Any) {
        Toast.makeText(this, value.toString(),Toast.LENGTH_SHORT).show()
        this.finish()
    }
}


