package dev.szczepaniak.moveit.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.szczepaniak.moveit.R


class InfoFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.info_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
