package dev.szczepaniak.moveit.controller

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import beforeAndroid
import dev.szczepaniak.moveit.presenters.LocationProvider
import fromAndroid
import java.util.*

class AlarmController(private val context: Context) {

    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun addAlarm(wakeUpTime: Date, address: String) {
        Intent(context, LocationProvider::class.java)
            .apply { action = address }
            .let {
                PendingIntent.getForegroundService(context, 0, it, 0)
            }
            .let {
                fromAndroid(Build.VERSION_CODES.N) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        wakeUpTime.time,
                        it
                    )
                }
                beforeAndroid(Build.VERSION_CODES.N) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        wakeUpTime.time,
                        it
                    )
                }
            }
    }
}