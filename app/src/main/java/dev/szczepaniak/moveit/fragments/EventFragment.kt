package dev.szczepaniak.moveit.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.szczepaniak.moveit.R

class EventFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.event_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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