package dev.szczepaniak.moveit.activities

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
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
import logd


class MoveEventActivity : AppCompatActivity(), MoveEventI {

    private val TAG = "MOVE_EVENT_ACT"
    private val eventProvider: EventProvider by lazy { EventProvider(context = this.applicationContext) }
    private val distanceProvider: GeoDistanceProvider by lazy { GeoDistanceProvider(this, this) }
    private var mode: Mode = Mode.NORMAL
    private lateinit var travelMode: TravelMode
    private lateinit var travelIcons: Array<ImageButton>

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
        travelIcons = arrayOf(imageWalk, imageBike, imageCar, imageTransit)
    }

    private fun initView(eventId: Long, nextEventId: Long) {
        initSwitch()
        event = eventProvider.getEvent(eventId)

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

    private fun initSwitch() {
        switch1.setOnCheckedChangeListener { _, b: Boolean ->
            b.logd(TAG)
            if (b) {
                imageBike.visibility = View.VISIBLE
                imageWalk.visibility = View.VISIBLE
                imageCar.visibility = View.VISIBLE
                imageTransit.visibility = View.VISIBLE
                chooseTime.hint = this.getString(R.string.choose_the_time_AI)
                this.mode = Mode.AI
            } else {
                imageBike.visibility = View.GONE
                imageWalk.visibility = View.GONE
                imageCar.visibility = View.GONE
                imageTransit.visibility = View.GONE
                chooseTime.hint = this.getString(R.string.choose_the_time)
                this.mode = Mode.NORMAL
            }
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
        if (this.mode == Mode.NORMAL) {
            this.eventProvider.moveEvent(nextEvent.id, this.selectedHour, this.selectedMinute)
            this.finish()
        } else {
            if (::travelMode.isInitialized) {
                val source = getPlaceLocation(event.location, context = applicationContext)
                val destination = getPlaceLocation(nextEvent.location, context = applicationContext)

                this.distanceProvider.execute(
                    "${source?.latitude},${source?.longitude}",
                    "${destination?.latitude},${destination?.longitude}",
                    this.travelMode.value,
                    "${event.endDate.time}"
                )
            } else {
                Toast.makeText(applicationContext, "Select travel mode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun moveEventAI(value: String) {
        this.eventProvider.moveEventAI(this.event, this.nextEvent, this.selectedHour, this.selectedMinute, value)
        this.finish()
    }

    fun selectTravelMode(view: View) {
        this.travelIcons.forEach {
            it.background = null
        }
        view.background = this.getDrawable(R.drawable.highlight)
        travelMode = when (view.tag) {
            "1" -> TravelMode.WALKING
            "2" -> TravelMode.BICYCLING
            "3" -> TravelMode.TRANSIT
            "4" -> TravelMode.DRIVING
            else -> TravelMode.DRIVING
        }
    }


}


enum class Mode {
    NORMAL, AI
}

enum class TravelMode(val value: String) {
    DRIVING("driving"),
    WALKING("walking"),
    BICYCLING("bicycling"),
    TRANSIT("transit")
}