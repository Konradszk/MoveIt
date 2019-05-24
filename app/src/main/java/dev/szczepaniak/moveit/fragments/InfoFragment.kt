package dev.szczepaniak.moveit.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import dev.szczepaniak.moveit.controller.AlarmController
import dev.szczepaniak.moveit.utils.NotificationFactory
import kotlinx.android.synthetic.main.info_layout.*
import logd
import loge
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class InfoFragment : Fragment() {

    private val notificationFactory by lazy { NotificationFactory() }
    private val alarmController: AlarmController by lazy { AlarmController(context!!) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(dev.szczepaniak.moveit.R.layout.info_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        push_not.setOnClickListener {
            val toast = Toast.makeText(context, "Tost", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM, 0, 200)
            toast.show()
            notificationFactory.show(context!!, "test", "srawdzam", "EVENTS")
        }
        set_alarm.setOnClickListener {
            alarmController.addAlarm(Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(20)),
                "Pizza Hut Zodiak, Widok 26, 00-023 Warszawa, Poland")
        }

        get_location.setOnClickListener {
            val coder = Geocoder(context)
            val address: List<Address>
            try {
                address = coder.getFromLocationName("Pizza Hut Zodiak, Widok 26, 00-023 Warszawa, Poland", 5)
                if (address != null) {
                    val location = address[0]
                    LatLng(location.latitude, location.longitude).logd("INFO_FRA")
                }
            } catch (ex: IOException) {
                ex.loge("INFO_FRA")
            }
        }
    }

    companion object {
        fun create(): InfoFragment {
            val fragment = InfoFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
