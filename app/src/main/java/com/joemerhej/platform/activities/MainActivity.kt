package com.joemerhej.platform.activities

/**
 * Created by Joe Merhej on 10/15/18.
 */
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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

private const val SAVED_INSTANCE_FRAGMENT_ID_KEY = "activeFragmentId"
private const val SAVED_INSTANCE_FRAGMENT_TITLE_KEY = "activeFragmentTitle"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, EditEventDialogFragment.EventListener
{
    //TODO: This is not needed for now
    override fun onCertainEvent()
    {
    }

    // fragment to load and its id
    private lateinit var fragment: Fragment
    private lateinit var fragmentTitle: String
    private var fragmentId = 0
    private var clickedNavItem = false


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize the toolbar
        setSupportActionBar(edit_event_toolbar)

        // initialize the toolbar toggle
        val toggle = ActionBarDrawerToggle(this, drawer_layout, edit_event_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        // set up navigation drawer and drawer layout listeners
        navigation_drawer.setNavigationItemSelectedListener(this)
        drawer_layout.addDrawerListener(this)

        // load logo in navigation drawer header image
        val navigationDrawerHeader = navigation_drawer.getHeaderView(0)
        Glide.with(this)
                .load(R.mipmap.logo)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(navigationDrawerHeader.nav_header_imageview)

        // if saved instance contains a fragment id (screen rotation) then fetch it and update UI elements (title, nav drawer checked item)
        savedInstanceState?.let {
            fragmentId = savedInstanceState.getInt(SAVED_INSTANCE_FRAGMENT_ID_KEY)
            navigation_drawer.menu.findItem(fragmentId).isChecked = true
            fragmentTitle = savedInstanceState.getString(SAVED_INSTANCE_FRAGMENT_TITLE_KEY, resources.getString(R.string.nav_item_schedule))
            supportActionBar?.title = fragmentTitle
        } ?: run {
            //  else load the default (schedule) fragment
            supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContent, ScheduleFragment.newInstance()).commit()
            fragmentId = R.id.nav_item_schedule
            navigation_drawer.menu.findItem(fragmentId).isChecked = true
            fragmentTitle = resources.getString(R.string.nav_item_schedule)
            supportActionBar?.title = fragmentTitle
        }
    }

    override fun onSaveInstanceState(outState: Bundle?)
    {
        super.onSaveInstanceState(outState)

        // save fragment title and id on save instance state (on screen rotation)
        outState?.putString(SAVED_INSTANCE_FRAGMENT_TITLE_KEY, fragmentTitle)
        outState?.putInt(SAVED_INSTANCE_FRAGMENT_ID_KEY, fragmentId)
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
     * Navigation drawer item selected listener to handle the drawer items clicks.
     * This call will only create the fragment instance based on drawer item click but will not replace it.
     * The fragment will be replaced in the DrawerLayoutListener.onDrawerClosed to avoid the lagging
     * and stuttering that happens if we try to close the drawer and load the fragment at the same time.
     *
     * @param item menu item clicked.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        if(item.itemId == fragmentId)
        {
            drawer_layout.closeDrawer(GravityCompat.START)
            return true
        }

        fragmentId = item.itemId
        fragmentTitle = item.title.toString()
        clickedNavItem = true

        when(fragmentId)
        {
            R.id.nav_item_schedule ->
            {
                fragment = ScheduleFragment.newInstance()
            }
            R.id.nav_item_clients ->
            {
                fragment = ClientsFragment.newInstance()
            }
            R.id.nav_item_owners ->
            {
                fragment = OwnersFragment.newInstance()
            }
            R.id.nav_item_share ->
            {

            }
            R.id.nav_item_settings ->
            {

            }
            else ->
                fragment = ScheduleFragment.newInstance()
        }

        // change the toolbar title
        supportActionBar?.title = item.title

        // mark nav item checked
        item.isChecked = true

        // close the navigation drawer
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDrawerStateChanged(newState: Int)
    {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float)
    {
    }

    /**
     * Load the fragment instance created earlier (in the drawer navigation on item click listener)
     * into the activity's frame layout. This is done here to avoid lagging the drawer when clicking
     * an item inside it since it won't close it and load the fragment at the same time.
     *
     * @param drawerView view of the drawer (not used).
     */
    override fun onDrawerClosed(drawerView: View)
    {
        if(clickedNavItem)
        {
            clickedNavItem = false

            // Insert the fragment by replacing the existing fragment
            supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutContent, fragment).commit()
        }
    }

    override fun onDrawerOpened(drawerView: View)
    {
    }
}























