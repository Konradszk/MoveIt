package dev.szczepaniak.moveit.controller

import android.app.AlarmManager
import android.content.Context

class AlarmController(context: Context) {

    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

}