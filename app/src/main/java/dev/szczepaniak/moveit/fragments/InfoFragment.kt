package dev.szczepaniak.moveit.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dev.szczepaniak.moveit.R
import dev.szczepaniak.moveit.controller.AlarmController
import dev.szczepaniak.moveit.utils.NotificationFactory
import kotlinx.android.synthetic.main.info_layout.*
import java.util.*
import java.util.concurrent.TimeUnit


class InfoFragment : Fragment() {

    private val notificationFactory by lazy { NotificationFactory() }
    private val alarmController: AlarmController by lazy { AlarmController(context!!) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.info_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        push_not.setOnClickListener {
            val toast = Toast.makeText(context, "Tost", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,200)
            toast.show()
            notificationFactory.show(context!!, "test", "srawdzam", "EVENTS")
        }
        set_alarm.setOnClickListener {
            alarmController.addAlarm(Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(20)))
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
