package dev.szczepaniak.moveit.presenters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import logd

const val RECORD_REQUEST_CODE = 101


class LocationProvider {

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Activity())


    @SuppressLint("MissingPermission")
    fun getUserLocation(){
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                val wayLatitude = it.latitude
                val wayLongitude = it.longitude
                wayLatitude.toString() + wayLongitude.logd("TAG")
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
                requestPermissions(activity,
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