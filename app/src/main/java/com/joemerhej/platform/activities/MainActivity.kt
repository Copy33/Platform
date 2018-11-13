package com.joemerhej.platform.activities

import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.joemerhej.androidweekview.*
import com.joemerhej.platform.Event
import com.joemerhej.platform.R
import com.joemerhej.platform.fragments.EditEventDialogFragment
import com.joemerhej.platform.sharedpreferences.SharedPreferencesKey
import com.joemerhej.platform.sharedpreferences.SharedPreferencesManager
import com.joemerhej.platform.utils.DebugUtils
import com.joemerhej.platform.utils.EventUtils
import com.joemerhej.platform.viewmodels.EventsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Joe Merhej on 10/15/18.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, EditEventDialogFragment.EventListener
{
    // TODO: This is not needed for now
    override fun onCertainEvent()
    {
    }

    private var selectedMenuItemId: Int = 0
    private lateinit var eventsViewModel: EventsViewModel


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize the toolbar
        setSupportActionBar(toolbar)

        // initialize shared preferences
        SharedPreferencesManager.initialize(this)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        navigation_drawer.setNavigationItemSelectedListener(this)

        // set the weekview listeners
        weekView.let {
            it.eventClickListener = this
            it.monthChangeListener = this
            it.eventLongPressListener = this
        }

        // set the week view visible days based on user's preferences
        val visibleDaysSaved = SharedPreferencesManager.readInt(SharedPreferencesKey.VISIBLE_DAYS_NUMBER, 3)
        when(visibleDaysSaved)
        {
            1 ->
            {
                selectedMenuItemId = R.id.action_day_view
                weekView.numberOfVisibleDays = 1
            }
            3 ->
            {
                selectedMenuItemId = R.id.action_three_day_view
                weekView.numberOfVisibleDays = 3
            }
            7 ->
            {
                selectedMenuItemId = R.id.action_week_view
                weekView.numberOfVisibleDays = 7
            }
            else -> weekView.numberOfVisibleDays = visibleDaysSaved
        }

        // set the week view starting hour TODO: should be user preference
        weekView.goToHour(8.0)

        // set the week view date and time interpreter to define how to display time and dates
        setupDateTimeInterpreter(selectedMenuItemId)

        // set up the view models
        eventsViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)

        // observe changes to the events view model
        eventsViewModel.events.observe(this, androidx.lifecycle.Observer {
            Log.d(DebugUtils.TAG, "Events Changed! $it")
            weekView.notifyDataSetChanged()
        })

        // set the add event fab click listener
        addEventFab.setOnClickListener()
        {
            EditEventDialogFragment.show(supportFragmentManager, "tag")
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.main, menu)
        menu.findItem(selectedMenuItemId).isChecked = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        setupDateTimeInterpreter(id)

        when(id)
        {
            R.id.action_today ->
            {
                weekView.goToToday()
            }
            R.id.action_day_view ->
            {
                if(selectedMenuItemId != R.id.action_day_view)
                {
                    item.isChecked = !item.isChecked
                    selectedMenuItemId = R.id.action_day_view
                    SharedPreferencesManager.writeInt(SharedPreferencesKey.VISIBLE_DAYS_NUMBER, 1)

                    // Lets change some dimensions to best fit the view.
                    weekView.apply {
                        numberOfVisibleDays = 1
                        columnGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics)
                        eventTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics)
                    }
                }
            }
            R.id.action_three_day_view ->
            {
                if(selectedMenuItemId != R.id.action_three_day_view)
                {
                    item.isChecked = !item.isChecked
                    selectedMenuItemId = R.id.action_three_day_view
                    SharedPreferencesManager.writeInt(SharedPreferencesKey.VISIBLE_DAYS_NUMBER, 3)

                    // Lets change some dimensions to best fit the view.
                    weekView.apply {
                        numberOfVisibleDays = 3
                        columnGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics)
                        eventTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics)
                    }
                }
            }
            R.id.action_week_view ->
            {
                if(selectedMenuItemId != R.id.action_week_view)
                {
                    item.isChecked = !item.isChecked
                    selectedMenuItemId = R.id.action_week_view
                    SharedPreferencesManager.writeInt(SharedPreferencesKey.VISIBLE_DAYS_NUMBER, 7)

                    // Lets change some dimensions to best fit the view.
                    weekView.apply {
                        numberOfVisibleDays = 7
                        columnGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt()
                        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, resources.displayMetrics)
                        eventTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, resources.displayMetrics)
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Navigation drawer item selected listener to handle the menu items clicks.
     *
     * @param item menu item clicked.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.nav_item_schedule ->
            {
                // Handle the schedule action
            }
            R.id.nav_item_clients ->
            {

            }
            R.id.nav_item_owners ->
            {

            }
            R.id.nav_item_share ->
            {

            }
            R.id.nav_item_settings ->
            {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param viewId True if the date values should be short.
     */
    private fun setupDateTimeInterpreter(viewId: Int)
    {
        val normalDateFormat = WeekViewUtil.getWeekdayWithNumericDayAndMonthFormat(this, false)
        val shortDateFormat = WeekViewUtil.getWeekdayWithNumericDayAndMonthFormat(this, true)
        val timeFormat = if(android.text.format.DateFormat.getTimeFormat(this) != null)
            android.text.format.DateFormat.getTimeFormat(this)
        else
            SimpleDateFormat("HH:mm", Locale.getDefault())

        weekView.dateTimeInterpreter = object : DateTimeInterpreter
        {
            override fun getFormattedWeekDayTitle(date: Calendar): String
            {
                return if(viewId == R.id.action_week_view) shortDateFormat.format(date.time) else normalDateFormat.format(date.time)
            }

            override fun getFormattedTimeOfDay(hour: Int, minutes: Int): String
            {
                val calendar = Calendar.getInstance()
                with(calendar)
                {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minutes)
                }

                return timeFormat.format(calendar.time)
            }
        }
    }

    /**
     * Listener to when an event is clicked.
     *
     * @param event week view event clicked.
     * @param eventRect Rectangle of the event clicked.
     */
    override fun onEventClick(event: WeekViewEvent, eventRect: RectF)
    {
        Toast.makeText(this, "Clicked " + event.title!!, Toast.LENGTH_SHORT).show()
    }

    /**
     * Listener to when an event is long pressed.
     *
     * @param event week view event pressed.
     * @param eventRect Rectangle of the event pressed.
     */
    override fun onEventLongPress(event: WeekViewEvent, eventRect: RectF)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete event?")
                .setPositiveButton("Delete") { _, _ ->
                    eventsViewModel.removeEvent(event as Event)
                    weekView.notifyDataSetChanged()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

        val alertDialog = builder.create()
        alertDialog.show()
        Toast.makeText(this, "Long pressed event: " + event.title!!, Toast.LENGTH_SHORT).show()
    }

    /**
     * Listener to when a new half month is scrolled or notifyDataSetChanged() is called on the weekview.
     * This will be called 3 times for current, previous, and next month respectively.
     *
     * @param newYear Year of the month to be loaded.
     * @param newMonth Month to be loaded.
     * @return the list of events for this specific month and year.
     */
    override fun onMonthChange(newYear: Int, newMonth: Int): MutableList<WeekViewEvent>?
    {
        eventsViewModel.events.value?.let {
            return EventUtils.getEventsForMonth(it, newMonth, newYear)
        } ?: return null
    }
}























