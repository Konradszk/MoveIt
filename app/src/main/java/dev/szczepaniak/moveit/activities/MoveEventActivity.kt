package dev.szczepaniak.moveit.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import dev.szczepaniak.moveit.R
import dev.szczepaniak.moveit.model.Event
import dev.szczepaniak.moveit.presenters.EventProvider
import kotlinx.android.synthetic.main.activity_move_event.*
import java.util.*

class MoveEventActivity : AppCompatActivity() {

    private val TAG = "MOVE_EVENT"

    private val eventProvider: EventProvider by lazy { EventProvider(context = this.applicationContext) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_move_event)
        val eventId = intent.getLongExtra("EVENT_ID", 0)
        val nextEventId = intent.getLongExtra("NEXT_EVENT_ID", 0)
        initView(eventId, nextEventId)
    }

    private fun initView(eventId: Long, nextEventId: Long) {
        val event: Event = eventProvider.getEvent(eventId)

        eventName.setText(event.name)
        eventLocation.setText(event.location)

        val calendar = Calendar.getInstance()
        calendar.time = event.endDate
        val timeString: String = calendar.get(Calendar.HOUR).toString() + ":" + calendar.get(Calendar.MINUTE).toString()
        eventEnd.setText(timeString)

        if (nextEventId != 0L) {
            val nextEvent: Event = eventProvider.getEvent(nextEventId)

            nextEventName.setText(nextEvent.name)
            nextEventStart.setText(nextEvent.startDate.time.toString())
            nextEventLocation.setText(nextEvent.location)
        } else {
            nextEventName.visibility = View.GONE
            nextEventStart.visibility = View.GONE
            nextEventLocation.visibility = View.GONE
            nextEventLabel.visibility = View.GONE
        }
    }
}
