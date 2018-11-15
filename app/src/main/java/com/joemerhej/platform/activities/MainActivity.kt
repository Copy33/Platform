package com.joemerhej.platform.activities

/**
 * Created by Joe Merhej on 10/15/18.
 */
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

private const val SAVE_INSTANCE_FRAGMENT_KEY = "activeFragment"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, EditEventDialogFragment.EventListener
{
    //TODO: This is not needed for now
    override fun onCertainEvent()
    {
    }

    private var fragmentId = 0

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

        // if saved instance contains a fragment id (screen rotation) then load it
        //  else load the default (schedule) fragment
        savedInstanceState?.let {
            val fragmentIdInstance = savedInstanceState.getInt(SAVE_INSTANCE_FRAGMENT_KEY)
            navigation_drawer.menu.findItem(fragmentIdInstance).isChecked = true
            fragmentId = fragmentIdInstance
        } ?: run {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContent, ScheduleFragment.newInstance()).commit()
            fragmentId = R.id.nav_item_schedule
        }

        // change the toolbar title
        supportActionBar?.title = resources.getString(R.string.nav_item_schedule)
    }

    override fun onSaveInstanceState(outState: Bundle?)
    {
        super.onSaveInstanceState(outState)
        outState?.let {
            it.putInt(SAVE_INSTANCE_FRAGMENT_KEY, fragmentId)
        }
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

        fragmentId = item.itemId

        when(fragmentId)
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

        // mark nav item checked
        item.isChecked = true

        // close the navigation drawer
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}























