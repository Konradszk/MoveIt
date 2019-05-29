package dev.szczepaniak.moveit.presenters

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import dev.szczepaniak.moveit.model.Event
import logd
import me.everything.providers.android.calendar.CalendarProvider

import java.util.*

class EventProvider(context: Context) : ViewModel() {

    private val calendarProvider = CalendarProvider(context)
    private val toast = Toast.makeText(context, "Move it", Toast.LENGTH_SHORT)
    private val userCalendarId = this.calendarProvider.calendars.list
        .find { calendar -> calendar.ownerAccount.endsWith("@gmail.com") }?.id


    fun getEvents(fromTime: Date? = null): List<Event>? {
        return this.userCalendarId?.let {
            val eventList = this.calendarProvider.getEvents(it).list
                .filter { event ->
                    if (fromTime != null) event.dTStart > fromTime.time else true
                }
                .map { event ->
                    Event(
                        event.id,
                        event.title,
                        this.getParticipants(event.id),
                        event.eventLocation,
                        Date(event.dTStart * 1000L),
                        Date(event.dTend * 1000L),
                        null
                    )
                }

            for (i in eventList.withIndex() ){
                if(i.index!= eventList.lastIndex){
                    i.value.nextEventId= eventList[i.index+1].id
                }
            }
            eventList
        }
    }

    fun getEvent(idEvent: Long): Event {
        val event = this.calendarProvider.getEvent(idEvent)

        return Event(
            event.id,
            event.title,
            emptyList(),
            event.eventLocation,
            Date(event.dTStart * 1000L),
            Date(event.dTend * 1000L),
            null
        )
    }

    private fun getParticipants(eventId: Long): List<String> {
        return this.calendarProvider.getAttendees(eventId)
            .list.map { attendee -> attendee.email }
    }

    fun test() {
        toast.show()
    }


    companion object {
        fun setUpPermission(context: Context, activity: Activity) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.WRITE_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                    ), RECORD_REQUEST_CODE
                )
            } else {
                logd("TAG", format = { "CALENDAR PERMISSION GRANTED" })
            }
        }
    }
}
