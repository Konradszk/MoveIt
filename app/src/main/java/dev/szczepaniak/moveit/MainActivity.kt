package dev.szczepaniak.moveit

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import dev.szczepaniak.moveit.fragments.DashboardFragment
import dev.szczepaniak.moveit.fragments.EventFragment
import dev.szczepaniak.moveit.fragments.InfoFragment
import dev.szczepaniak.moveit.utils.NotificationFactory
import kotlinx.android.synthetic.main.activity_main.*
import logd

class MainActivity : AppCompatActivity() {

    private val notificationFactory by lazy { NotificationFactory() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { changePage(item = it) }
        notificationFactory.createNotificationChannels(context = this)
        cardNavigation(savedInstanceState)
    }

    private fun cardNavigation(savedInstanceState: Bundle?) {
        if (savedInstanceState == null && intent.getStringExtra("CARD_NAME") == null) {
            changePage(navigation.menu.findItem(R.id.navigation_dashboard))
            navigation.selectedItemId = R.id.navigation_dashboard
        } else if (intent.getStringExtra("CARD_NAME") == "EVENTS") {
            changePage(navigation.menu.findItem(R.id.navigation_event))
            navigation.selectedItemId = R.id.navigation_event
        }
    }

    private fun changePage(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_info -> {
                supportActionBar!!.title = "Move It - Info"
                showPage(InfoFragment.create())
            }
            R.id.navigation_dashboard -> {
                supportActionBar!!.title = "Move It - Dashboard"
                showPage(DashboardFragment.create())
            }
            R.id.navigation_event -> {
                supportActionBar!!.title = "Move It - Events"
                showPage(EventFragment.create())
            }
            else -> return false
        }
        return true
    }

    private fun showPage(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
