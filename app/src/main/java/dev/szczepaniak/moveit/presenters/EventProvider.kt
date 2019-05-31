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
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId


import java.util.*
import android.provider.SyncStateContract.Helpers.update
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.util.Log


class EventProvider(context: Context) : ViewModel() {
    private val TAG = "MOVE_IT_EVENT_PR"
    private val context = context

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


            val startTimeLDT = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.of("UTC"))
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
