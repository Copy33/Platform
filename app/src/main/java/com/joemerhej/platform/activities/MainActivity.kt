package com.joemerhej.platform.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.joemerhej.platform.R
import com.joemerhej.platform.dialogfragments.EditEventDialogFragment
import com.joemerhej.platform.fragments.ClientsFragment
import com.joemerhej.platform.fragments.OwnersFragment
import com.joemerhej.platform.fragments.ScheduleFragment
import com.joemerhej.platform.sharedpreferences.SharedPreferencesKey
import com.joemerhej.platform.sharedpreferences.SharedPreferencesManager
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Joe Merhej on 10/15/18.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, EditEventDialogFragment.EventListener
{
    //TODO: This is not needed for now
    override fun onCertainEvent()
    {
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize the toolbar
        setSupportActionBar(toolbar)

        // initialize toggle and navigation drawer
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        navigation_drawer.setNavigationItemSelectedListener(this)

        // App always opens on schedule fragment
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContent, ScheduleFragment.newInstance()).commit()

        // change the toolbar title
        supportActionBar?.title = resources.getString(R.string.nav_item_schedule)
    }

    override fun onBackPressed()
    {
        // handle navigation view closing on back
        if(drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    /**
     * Navigation drawer item selected listener to handle the menu items clicks.
     *
     * @param item menu item clicked.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        lateinit var fragment: Fragment
        lateinit var fragmentClass: Class<*>

        item.isChecked = true

        when(item.itemId)
        {
            R.id.nav_item_schedule ->
            {
                fragmentClass = ScheduleFragment::class.java
            }
            R.id.nav_item_clients ->
            {
                fragmentClass = ClientsFragment::class.java
            }
            R.id.nav_item_owners ->
            {
                fragmentClass = OwnersFragment::class.java
            }
            R.id.nav_item_share ->
            {

            }
            R.id.nav_item_settings ->
            {

            }
            else ->
                fragmentClass = ScheduleFragment::class.java

        }

        try
        {
            fragment = fragmentClass.newInstance() as Fragment
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }

        // Insert the fragment by replacing the existing fragment
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContent, fragment).commit()

        // change the toolbar title
        supportActionBar?.title = item.title

        // close the navigation drawer
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}























