package dev.szczepaniak.moveit.presenters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import dev.szczepaniak.moveit.model.Event
import logd
import me.everything.providers.android.calendar.CalendarProvider

import java.util.*

class EventProvider(context: Context) {

    private val calendarProvider = CalendarProvider(context)
    private val userCalendarId = this.calendarProvider.calendars.list
        .find { calendar -> calendar.ownerAccount.endsWith("@gmail.com") }?.id


    fun getEvents(fromTime: Date? = null): List<Event>? {
        return this.userCalendarId?.let {
            this.calendarProvider.getEvents(it).list
                .filter {
                    event -> if(fromTime!= null) event.dTStart > fromTime.time else true
                }
                .map { event -> Event(
                    event.id,
                    event.title,
                    listOf("test1", "test2"),  // todo podmmienic na metode
                    event.eventLocation,
                    Date(event.dTStart* 1000L),
                    Date(event.dTend* 1000L) ) }
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
