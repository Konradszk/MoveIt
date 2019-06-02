package dev.szczepaniak.moveit.controller

import android.content.Context
import android.support.v4.app.FragmentActivity

class MoveItPref(activity: FragmentActivity, private val defValue: Long) {
    private val sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)

    var maxEventId: Long
        get() = sharedPreferences.getLong(MAX_EVENT_ID, defValue)
        set(value) = sharedPreferences.edit().putLong(MAX_EVENT_ID, value).apply()

    companion object {
        private const val MAX_EVENT_ID = "max_event_id"
    }
}