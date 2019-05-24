package dev.szczepaniak.moveit.presenters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dev.szczepaniak.moveit.utils.NotificationFactory
import logd
import dev.szczepaniak.moveit.R
import loge
import java.io.IOException

const val RECORD_REQUEST_CODE = 101
private const val EARTH_RADIUS_KM = 6378.137
private const val NOTIFICATION_LOCATION_ID = R.string.notification_channel_location_id
private const val NOTIFICATION_TITLE = "Location Service"
private const val NOTIFICATION_BODY = "Checking your location..."


class LocationProvider : Service() {
    private val TAG = "MOVE_IT_LOCATION"
    private val notificationFactory by lazy { NotificationFactory() }
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val coder by lazy {
        Geocoder(this)
    }

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
                "EVENTS"
            )
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkIsNextToEvent()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun checkIsNextToEvent() {
        getUserLocation {
            it.logd(TAG)
            val addressLocation = getPlaceLocation("Pizza Hut Zodiak, Widok 26, 00-023 Warszawa, Poland")
            addressLocation?.logd(TAG)
            if (addressLocation != null) {
                if (!checkNearbyCondition(it, addressLocation)) {
                    notificationFactory.show(
                        this,
                        "Ups... Not on time?",
                        "You miss you event, click to move it! ",
                        "EVENTS"
                    )
                }
            }
            stopSelf()
        }
    }

    private fun checkNearbyCondition(userLocation: LatLng, addressLocation: LatLng): Boolean {
        val latDiff = Math.abs(addressLocation.latitude - userLocation.latitude)
        val lngDiff = Math.abs(addressLocation.longitude - userLocation.longitude)
        return latDiff <= this.lat500m() && lngDiff <= lng500(addressLocation.latitude)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    @SuppressLint("MissingPermission")
    fun getUserLocation(callback: (LatLng) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                val wayLatitude = it.latitude
                val wayLongitude = it.longitude
                callback(LatLng(wayLatitude, wayLongitude))
            }
        }
    }

    private fun getPlaceLocation(address: String): LatLng? {
        val addressList: List<Address>
        val placeLocation: LatLng
        try {
            addressList = coder.getFromLocationName(address, 5)
            if (addressList == null) {
                return null
            }
            val location = addressList[0]
            placeLocation = LatLng(location.latitude, location.longitude)
            return placeLocation
        } catch (ex: IOException) {
            ex.loge(TAG)
        }
        return null
    }

    private fun lat500m(): Double {
        val m = (1 / ((2 * Math.PI / 360) * EARTH_RADIUS_KM)) / 1000
        return m * 500
    }

    private fun lng500(latitude: Double): Double {
        val m = (1 / ((2 * Math.PI / 360) * EARTH_RADIUS_KM)) / 1000
        return (500 * m) / Math.cos(latitude * Math.PI / 180)
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