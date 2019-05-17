package dev.szczepaniak.moveit.presenters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.szczepaniak.moveit.utils.NotificationFactory
import logd
import dev.szczepaniak.moveit.R

const val RECORD_REQUEST_CODE = 101

private const val NOTIFICATION_LOCATION_ID = R.string.notification_channel_location_id
private const val NOTIFICATION_TITLE = "Location Service"
private const val NOTIFICATION_BODY = "Checking your location..."


class LocationProvider : Service() {
    private val TAG = "MOVE_IT_LOCATION"
    private val notificationFactory by lazy { NotificationFactory() }


    override fun onCreate() {
        super.onCreate()
        "onCreate".logd(TAG)
        startForeground(
            NOTIFICATION_LOCATION_ID,
            notificationFactory.create(
                this,
                NOTIFICATION_TITLE,
                NOTIFICATION_BODY,
                NOTIFICATION_LOCATION_ID,
                null
            )
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getUserLocation()
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }


    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                val wayLatitude = it.latitude
                val wayLongitude = it.longitude
                (wayLatitude.toString() + wayLongitude).logd(TAG)
            }
        }
    }

    companion object {
        fun setUpPermission(context: Context, activity: Activity) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    activity,
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), RECORD_REQUEST_CODE
                )
            } else {
                logd("TAG", format = { "LOCATION PERMISSION GRANTED" })
            }
        }
    }
}