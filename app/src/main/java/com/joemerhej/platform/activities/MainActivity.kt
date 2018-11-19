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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.joemerhej.platform.R
import com.joemerhej.platform.dialogfragments.EditEventDialogFragment
import com.joemerhej.platform.mainfragments.ClientsFragment
import com.joemerhej.platform.mainfragments.OwnersFragment
import com.joemerhej.platform.mainfragments.ScheduleFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_drawer_header_main.view.*

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
        setSupportActionBar(edit_event_toolbar)

        // initialize toolbar toggle and navigation drawer
        val toggle = ActionBarDrawerToggle(this, drawer_layout, edit_event_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        navigation_drawer.setNavigationItemSelectedListener(this)

        // load logo in navigation drawer header image
        val navigationDrawerHeader = navigation_drawer.getHeaderView(0)
        Glide.with(this)
                .load(R.mipmap.logo)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(navigationDrawerHeader.nav_header_imageview)

        // if saved instance contains a fragment id (screen rotation) then load it
        savedInstanceState?.let {
            val fragmentIdInstance = savedInstanceState.getInt(SAVE_INSTANCE_FRAGMENT_KEY)
            navigation_drawer.menu.findItem(fragmentIdInstance).isChecked = true
            fragmentId = fragmentIdInstance
        } ?: run {
            //  else load the default (schedule) fragment
            supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContent, ScheduleFragment.newInstance()).commit()
            fragmentId = R.id.nav_item_schedule
            supportActionBar?.title = resources.getString(R.string.nav_item_schedule)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?)
    {
        super.onSaveInstanceState(outState)

        // save fragment id on save instance state (on screen rotation)
        outState?.putInt(SAVE_INSTANCE_FRAGMENT_KEY, fragmentId)
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























