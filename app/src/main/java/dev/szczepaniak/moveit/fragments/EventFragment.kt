package dev.szczepaniak.moveit.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.szczepaniak.moveit.presenters.EventProvider
import dev.szczepaniak.moveit.presenters.LocationProvider
import logd
import me.everything.providers.android.calendar.CalendarProvider


const val RECORD_REQUEST_CODE = 101


class EventFragment : Fragment() {

    private val TAG = "EVENT_FRAGMENT"


    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(dev.szczepaniak.moveit.R.layout.event_layout, container, false)

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LocationProvider.setUpPermission(context!!,activity!!)
        EventProvider.setUpPermission(context!!,activity!!)
        LocationProvider().getUserLocation()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                val wayLatitude = it.latitude
                val wayLongitude = it.longitude
                Toast.makeText(context, wayLatitude.toString() + wayLongitude, Toast.LENGTH_SHORT).show()
            }
        }
        val provider = CalendarProvider(context)
        val calendarId = provider.calendars.list.forEach { it.name.logd(TAG) }
        provider.getEvents(3).list.forEach{ it.logd(TAG)}

    }

    private fun setupPermissions() {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), RECORD_REQUEST_CODE
            )
        } else {
            logd("TAG", format = { "PERMISSION GRANTED" })
        }
    }


    companion object {
        fun create(): EventFragment {
            val fragment = EventFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
