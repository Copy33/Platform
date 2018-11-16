package com.joemerhej.platform.mainfragments

import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.ViewModelProviders
import com.joemerhej.androidweekview.MonthLoader
import com.joemerhej.androidweekview.WeekView
import com.joemerhej.androidweekview.WeekViewEvent
import com.joemerhej.platform.R
import com.joemerhej.platform.dialogfragments.EditEventDialogFragment
import com.joemerhej.platform.models.Event
import com.joemerhej.platform.sharedpreferences.SharedPreferencesKey
import com.joemerhej.platform.sharedpreferences.SharedPreferencesManager
import com.joemerhej.platform.utils.DebugUtils
import com.joemerhej.platform.utils.EventUtils
import com.joemerhej.platform.viewmodels.EventsViewModel
import kotlinx.android.synthetic.main.fragment_schedule.*
import androidx.lifecycle.Observer


/**
 * Created by Joe Merhej on 11/14/18.
 */
class ScheduleFragment : Fragment(), WeekView.EventClickListener,
        MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, EditEventDialogFragment.EventListener
{
    //TODO: This is not needed for now
    override fun onCertainEvent()
    {
    }

    companion object
    {
        fun newInstance() = ScheduleFragment()
    }

    private var selectedMenuItemId: Int = 0
    private lateinit var eventsViewModel: EventsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        // set the weekview listeners
        main_week_view.let {
            it.eventClickListener = this
            it.monthChangeListener = this
            it.eventLongPressListener = this
        }

        // initialize shared preferences
        activity?.let {
            SharedPreferencesManager.initialize(it)
        } ?: throw Exception("Invalid Activity for ScheduleFragment  - Initializing Shared Preferences")

        // set the week view starting hour TODO: should be user preference
        main_week_view.goToHour(8.0)

        // set up the view models
        eventsViewModel = activity?.run {
            ViewModelProviders.of(this).get(EventsViewModel::class.java)
        } ?: throw java.lang.Exception("Invalid Activity for ClientsFragment")

        // observe changes to the events view model
        eventsViewModel.events.observe(this, Observer {
            Log.d(DebugUtils.TAG, "Events Changed! $it")
            main_week_view.notifyDataSetChanged()
        })

        // setup the add event fab
        add_event_fab.animate().setDuration(200).scaleX(1.0f).scaleY(1.0f).interpolator = LinearOutSlowInInterpolator()
        add_event_fab.setOnClickListener()
        {
            EditEventDialogFragment.show(fragmentManager, "tag")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?)
    {
        menuInflater?.inflate(R.menu.schedule_options_menu, menu) ?: throw Exception("Couldn't inflate schedule fragment menu")
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?)
    {
        // set the week view visible days based on user's preferences
        val visibleDaysSaved = SharedPreferencesManager.readInt(SharedPreferencesKey.VISIBLE_DAYS_NUMBER, 7)
        when(visibleDaysSaved)
        {
            1 ->
            {
                selectedMenuItemId = R.id.action_day_view
                main_week_view.numberOfVisibleDays = 1
            }
            3 ->
            {
                selectedMenuItemId = R.id.action_three_day_view
                main_week_view.numberOfVisibleDays = 3
            }
            7 ->
            {
                selectedMenuItemId = R.id.action_week_view
                main_week_view.numberOfVisibleDays = 7
            }
            else -> main_week_view.numberOfVisibleDays = visibleDaysSaved
        }

        menu?.let {
            it.findItem(selectedMenuItemId).isChecked = true
        }

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if(item == null)
            return super.onOptionsItemSelected(item)

        val id = item.itemId

        when(id)
        {
            R.id.action_today ->
            {
                main_week_view.goToToday()
            }
            R.id.action_day_view ->
            {
                if(selectedMenuItemId != R.id.action_day_view)
                {
                    item.isChecked = true
                    selectedMenuItemId = R.id.action_day_view
                    SharedPreferencesManager.writeInt(SharedPreferencesKey.VISIBLE_DAYS_NUMBER, 1)

                    // Lets change some dimensions to best fit the view.
                    main_week_view.apply {
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
                    item.isChecked = true
                    selectedMenuItemId = R.id.action_three_day_view
                    SharedPreferencesManager.writeInt(SharedPreferencesKey.VISIBLE_DAYS_NUMBER, 3)

                    // Lets change some dimensions to best fit the view.
                    main_week_view.apply {
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
                    item.isChecked = true
                    selectedMenuItemId = R.id.action_week_view
                    SharedPreferencesManager.writeInt(SharedPreferencesKey.VISIBLE_DAYS_NUMBER, 7)

                    // Lets change some dimensions to best fit the view.
                    main_week_view.apply {
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
     * Listener to when an event is clicked.
     *
     * @param event week view event clicked.
     * @param eventRect Rectangle of the event clicked.
     */
    override fun onEventClick(event: WeekViewEvent, eventRect: RectF)
    {
        Toast.makeText(activity, "Clicked " + event.title!!, Toast.LENGTH_SHORT).show()
    }

    /**
     * Listener to when an event is long pressed.
     *
     * @param event week view event pressed.
     * @param eventRect Rectangle of the event pressed.
     */
    override fun onEventLongPress(event: WeekViewEvent, eventRect: RectF)
    {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Delete event?")
                .setPositiveButton("Delete") { _, _ ->
                    eventsViewModel.removeEvent(event as Event)
                    main_week_view.notifyDataSetChanged()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

        val alertDialog = builder.create()
        alertDialog.show()
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














