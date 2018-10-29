package com.joemerhej.platform.activities

import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.joemerhej.androidweekview.*
import com.joemerhej.platform.*
import com.joemerhej.platform.R
import com.joemerhej.platform.fragments.EditEventDialogFragment
import com.joemerhej.platform.sharedpreferences.SharedPreferencesKey
import com.joemerhej.platform.sharedpreferences.SharedPreferencesManager
import com.joemerhej.platform.viewmodels.EditEventViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Joe Merhej on 10/15/18.
 */
class MainActivity : AppCompatActivity(), WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, EditEventDialogFragment.EventListener
{
    override fun onCertainEvent()
    {

    }

    private var selectedMenuItemId: Int = 0
    private var myEvents: MutableList<WeekViewEvent> = mutableListOf()
    private lateinit var editEventViewModel: EditEventViewModel


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize shared preferences
        SharedPreferencesManager.initialize(this)

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

        // set up the edit event view model
        editEventViewModel = ViewModelProviders.of(this).get(EditEventViewModel::class.java)

        // observe changes to the edit event live data
        editEventViewModel.event.observe(this, androidx.lifecycle.Observer {
            Log.d(DebugUtils.TAG, "Event Changed! $it")
            myEvents.add(it)
            weekView.notifyDataSetChanged()
        })

        // set the add event fab click listener TODO: this creates a new event now and passes it, should pass null and dialog will take care of populating event in view controller
        addEventFab.setOnClickListener()
        {
            EditEventDialogFragment.show(supportFragmentManager, "tag")
        }
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
                    myEvents.remove(event)
                    weekView.notifyDataSetChanged()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    myEvents.remove(event)
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
        return EventUtils.getEventsForMonth(myEvents, newMonth, newYear)
    }
}























