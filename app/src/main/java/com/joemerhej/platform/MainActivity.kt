package com.joemerhej.platform

import android.graphics.RectF
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.joemerhej.androidweekview.*
import com.joemerhej.platform.sharedpreferences.SharedPreferencesKey
import com.joemerhej.platform.sharedpreferences.SharedPreferencesManager
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Joe Merhej on 10/15/18.
 */
class MainActivity : AppCompatActivity(), WeekView.EventClickListener, MonthLoader.MonthChangeListener,
        WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener, WeekView.EmptyViewClickListener
{
    private var selectedMenuItemId: Int = 0
    private var myEvents: MutableList<WeekViewEvent> = mutableListOf()


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
            it.emptyViewLongPressListener = this
            it.emptyViewClickListener = this
        }

        // set the week view visible days based on user's preferences
        val visibleDaysSaved = SharedPreferencesManager.readInt(SharedPreferencesKey.VISIBLE_DAYS_NUMBER, 3)
        when (visibleDaysSaved)
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

        // set the add event fab click listener
        addEventFab.setOnClickListener {

            val today = WeekViewUtil.today()

            val startTime = today.clone() as Calendar
            startTime.set(Calendar.HOUR_OF_DAY, 11)
            startTime.set(Calendar.MINUTE, 0)

            val endTime = startTime.clone() as Calendar
            endTime.add(Calendar.HOUR, 1)

            val event = WeekViewEvent("1", getEventTitle(startTime), "subtitle", startTime, endTime, ContextCompat.getColor(this, R.color.event_color_01))
            myEvents.add(event)
            weekView.notifyDataSetChanged()
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

        when (id)
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

    private fun getEventTitle(time: Calendar): String
    {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH))
    }

    // listener for single event click
    override fun onEventClick(event: WeekViewEvent, eventRect: RectF)
    {
        Toast.makeText(this, "Clicked " + event.title!!, Toast.LENGTH_SHORT).show()
    }

    // listener for event long press
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

    override fun onEmptyViewClicked(date: Calendar)
    {
        Toast.makeText(this, "Empty view clicked: " + getEventTitle(date), Toast.LENGTH_SHORT).show()
    }

    override fun onEmptyViewLongPress(time: Calendar)
    {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show()
    }

    // listener for when a new month is scrolled
    override fun onMonthChange(newYear: Int, newMonth: Int): MutableList<WeekViewEvent>?
    {
        return EventUtils.getEventsForMonth(myEvents, newMonth, newYear)
    }
}























