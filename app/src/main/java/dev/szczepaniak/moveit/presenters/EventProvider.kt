package dev.szczepaniak.moveit.presenters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import logd

class EventProvider {


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
