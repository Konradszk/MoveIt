package dev.szczepaniak.moveit.presenters


import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import dev.szczepaniak.moveit.model.Event
import logd
import me.everything.providers.android.calendar.CalendarProvider
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class EventProvider(context: Context) : ViewModel() {
    private val EXTRA_MINUTES: Int = 5 * 60
    private val TAG = "MOVE_IT_EVENT_PR"
    private val context = context

    private val calendarProvider = CalendarProvider(context)
    private val toast = Toast.makeText(context, "Move it", Toast.LENGTH_SHORT)
    private val userCalendarId = this.calendarProvider.calendars.list
        .find { calendar -> calendar.ownerAccount.endsWith("@gmail.com") }?.id


    fun getEvents(fromTime: Date? = null): List<Event>? {
        return this.userCalendarId?.let {
            val eventList = this.calendarProvider.getEvents(it).list
                .filter { event -> event.eventLocation!="" }
                .filter { event ->
                    if (fromTime != null) event.dTStart > fromTime.time else true
                }
                .map { event ->
                    Event(
                        event.id,
                        event.title,
                        this.getParticipants(event.id),
                        event.eventLocation,
                        Date(event.dTStart),
                        Date(event.dTend),
                        null
                    )
                }.sortedBy { event -> event.startDate }
            for (i in eventList.withIndex()) {
                if (i.index != eventList.lastIndex) {
                    i.value.nextEventId = eventList[i.index + 1].id
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
            Date(event.dTStart),
            Date(event.dTend),
            null
        )
    }

    private fun getParticipants(eventId: Long): List<String> {
        return this.calendarProvider.getAttendees(eventId)
            .list.map { attendee -> attendee.email }
    }

    private fun eventDateUpdate(event: me.everything.providers.android.calendar.Event) {

        var iNumRowsUpdated = 0

        val data = ContentValues()

        data.put("dTStart", event.dTStart)
        data.put("dTend", event.dTend)
        data.put("lastDate", event.dTend)

        val eventsUri = Uri.parse("content://com.android.calendar/events")
        val eventUri = ContentUris.withAppendedId(eventsUri, event.id)

        iNumRowsUpdated = context.contentResolver.update(
            eventUri, data,
            null, null
        )

        Log.i(TAG, "Updated $iNumRowsUpdated calendar entry.")
    }

    fun moveEvent(eventId: Long, hour: Int, minute: Int) {
        this.calendarProvider.getEvent(eventId).let {

            val startTime = Date(it.dTStart)
            val endTime = Date(it.dTend)
            val calStart = Calendar.getInstance()
            val calEnd = Calendar.getInstance()
            calStart.time = startTime
            calEnd.time = endTime
            calStart.set(Calendar.HOUR, hour)
            calStart.set(Calendar.MINUTE, minute)
            calEnd.set(Calendar.HOUR, hour)
            calEnd.set(Calendar.MINUTE, minute)


            val startTimeLDT = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault())
            val endTimeLDT = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault())
            val diff: Duration = Duration.between(startTimeLDT, endTimeLDT)
            val diffMinutes = diff.toMinutes()
            calEnd.add(Calendar.MINUTE, diffMinutes.toInt())
            it.dTStart = (calStart.timeInMillis)
            it.dTend = (calEnd.timeInMillis)
            it.lastDate = (calEnd.timeInMillis)
            this.eventDateUpdate(it)
        }
    }

    fun moveEventAI(event: Event, nextEvent: Event, selectedHour: Int, selectedMinute: Int, timeTravelSec: String) {
        val calEndTimeEvent = Calendar.getInstance()
        calEndTimeEvent.time = event.endDate
        calEndTimeEvent.set(Calendar.HOUR, selectedHour)
        calEndTimeEvent.set(Calendar.MINUTE, selectedMinute)

        val endTimeEventLDT = LocalDateTime.ofInstant(calEndTimeEvent.time.toInstant(), ZoneId.systemDefault())
        val startTimeNextEventLDT = LocalDateTime.ofInstant(nextEvent.startDate.toInstant(), ZoneId.systemDefault())
        val diff = Duration.between(endTimeEventLDT, startTimeNextEventLDT)
        if ((diff.toMinutes() * 60) < timeTravelSec.toInt()) {
            val calStartTimeNextEvent = Calendar.getInstance()
            calStartTimeNextEvent.time = nextEvent.startDate
            calStartTimeNextEvent.add(Calendar.SECOND, timeTravelSec.toInt() + this.EXTRA_MINUTES)
            this.moveEvent(
                nextEvent.id,
                calStartTimeNextEvent.get(Calendar.HOUR),
                calStartTimeNextEvent.get(Calendar.MINUTE)
            )

            Toast.makeText(
                context,
                "Move ${nextEvent.name} on ${calStartTimeNextEvent.get(Calendar.HOUR)}:${calStartTimeNextEvent.get(Calendar.MINUTE)}",
                Toast.LENGTH_LONG
            ).show()

        } else {
            val timeTravelMin = timeTravelSec.toInt() / 60
            Toast.makeText(
                context,
                "The is no reason to move event, travel time: $timeTravelMin min",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    fun getMaxId(time: Date): Long? {
        return this.userCalendarId?.let {
            this.calendarProvider.getEvents(it).list
                .filter { event -> event.dTStart > time.time  }
                .maxBy { event -> event.id }?.id
        }
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
