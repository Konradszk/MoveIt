package dev.szczepaniak.moveit

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import dev.szczepaniak.moveit.fragments.DashboardFragment
import dev.szczepaniak.moveit.fragments.EventFragment
import dev.szczepaniak.moveit.fragments.InfoFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { changePage(item = it) }

        if (savedInstanceState == null) {
            changePage(navigation.menu.findItem(R.id.navigation_dashboard))
            navigation.selectedItemId = R.id.navigation_dashboard
        }
    }

    private fun changePage(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_info -> {
                message.setText(R.string.title_home)
                supportActionBar!!.title = "Move It - Info"
                showPage(InfoFragment.create())
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                supportActionBar!!.title = "Move It - Dashboard"
                showPage(DashboardFragment.create())
            }
            R.id.navigation_event -> {
                supportActionBar!!.title = "Move It - Events"
                message.setText(R.string.title_notifications)
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
