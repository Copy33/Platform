package com.joemerhej.androidweekview

import android.graphics.Shader
import androidx.annotation.ColorInt
import com.joemerhej.androidweekview.WeekViewUtil.isSameDay
import java.util.*

/**
 * Open class (can be derived, by default kotlin's classes are final) that is used to define a single event in the week
 */
open class WeekViewEvent
{
    val id: String?                 // id of the event
    val startTime: Calendar         // start time of the event
    val endTime: Calendar           // end time of the event
    var name: String? = null        // name of the event
    var location: String? = null    // location of the event
    @ColorInt
    @get:ColorInt
    var color: Int = 0              // color of the event, int annotated as color id AARRGGBB
    val isAllDay: Boolean           // boolean to check if all day event
    var shader: Shader? = null      // shader to draw the event


    /**Constructor for a single, all day event*/
    constructor(id: String?, name: String?, location: String? = null, allDayTime: Calendar, shader: Shader? = null) : this(id, name, location, allDayTime, allDayTime, true, shader)

    /**
     * Initializes the event for week view.
     *
     * @param id        The id of the event as String.
     * @param name      Name of the event.
     * @param location  The location of the event.
     * @param startTime The time when the event starts.
     * @param endTime   The time when the event ends.
     * @param allDay    Is the event an all day event.
     * @param shader    the Shader of the event rectangle
     */
    @JvmOverloads constructor(id: String?, name: String?, location: String?, startTime: Calendar, endTime: Calendar, allDay: Boolean = false, shader: Shader? = null)
    {
        this.id = id
        this.name = name
        this.location = location
        this.isAllDay = allDay
        if(!allDay)
        {
            this.startTime = startTime
            this.endTime = endTime
        }
        else
        {
            WeekViewUtil.resetTime(startTime)
            this.startTime = startTime
            if(!WeekViewUtil.isSameDay(startTime, endTime))
            {
                WeekViewUtil.resetTime(endTime)
                this.endTime = endTime
            }
            else
                this.endTime = startTime
        }
        this.shader = shader
    }

    /**
     * Initializes the event for week view.
     *
     * @param id        The id of the event specified as String.
     * @param name      Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime   The time when the event ends.
     */
    constructor(id: String?, name: String?, startTime: Calendar, endTime: Calendar) : this(id, name, null, startTime, endTime)

    /**
     * Overloaded equals() that returns whether passed WeekViewEvent is the same comparing event IDs.
     *
     * @param other     Another WeekViewEvent.
     * @return          Whether the passed event has the same ID as this.
     */
    override fun equals(other: Any?): Boolean
    {
        if(this === other) return true
        if(other !is WeekViewEvent) return false
        return id == other.id
    }

    /**
     * Overloaded hashCode() that returns a hash of the event ID.
     *
     * @return          Hash (int) of this event's ID.
     */
    override fun hashCode(): Int
    {
        return id?.hashCode() ?: 0
    }

    /**
     * Method that will split an event (ideally spanning over multiple days) into multiple events by day.
     *
     * @return          List of events per day.
     */
    fun splitWeekViewEvents(): MutableList<WeekViewEvent>
    {
        //This function splits the WeekViewEvent in WeekViewEvents by day
        if(isSameDay(this.startTime, this.endTime))
        {
            val events = ArrayList<WeekViewEvent>(1)
            events.add(this)
            return events
        }
        val events = ArrayList<WeekViewEvent>()
        // The first millisecond of the next day is still the same day. (no need to split events for this).
        var endTime = this.endTime.clone() as Calendar
        endTime.add(Calendar.MILLISECOND, -1)
        endTime = this.startTime.clone() as Calendar
        endTime.set(Calendar.HOUR_OF_DAY, 23)
        endTime.set(Calendar.MINUTE, 59)
        val event1 = WeekViewEvent(this.id, this.name, this.location, this.startTime, endTime, this.isAllDay)
        event1.color = this.color
        events.add(event1)
        // Add other days.
        if(!isSameDay(this.startTime, this.endTime))
        {
            val otherDay = this.startTime.clone() as Calendar
            otherDay.add(Calendar.DATE, 1)
            while(!isSameDay(otherDay, this.endTime))
            {
                val overDay = otherDay.clone() as Calendar
                overDay.set(Calendar.HOUR_OF_DAY, 0)
                overDay.set(Calendar.MINUTE, 0)
                val endOfOverDay = overDay.clone() as Calendar
                endOfOverDay.set(Calendar.HOUR_OF_DAY, 23)
                endOfOverDay.set(Calendar.MINUTE, 59)
                val eventMore = WeekViewEvent(this.id, this.name, null, overDay, endOfOverDay, this.isAllDay)
                eventMore.color = this.color
                events.add(eventMore)

                // Add next day.
                otherDay.add(Calendar.DATE, 1)
            }
            // Add last day.
            val startTime = this.endTime.clone() as Calendar
            startTime.set(Calendar.HOUR_OF_DAY, 0)
            startTime.set(Calendar.MINUTE, 0)
            val event2 = WeekViewEvent(this.id, this.name, this.location, startTime, this.endTime, this.isAllDay)
            event2.color = this.color
            events.add(event2)
        }
        return events
    }

    /**
     * Overloaded toString() method to print the event's properties
     *
     * @return          Events properties based on event type (all day, multiple days...etc).
     */
    override fun toString(): String
    {
        val colorStr = "#${Integer.toHexString(color)}"
        val startTimeStr = WeekViewUtil.calendarToString(startTime, !isAllDay)
        if(isAllDay)
        {
            if(WeekViewUtil.isSameDay(startTime, endTime))
                return "allDayEvent(id=$id, time=$startTimeStr..${WeekViewUtil.calendarToString(startTime, false)}, name=$name, location=$location, color=$colorStr ,shader=$shader)"
            return "allDayEvent(id=$id, time=$startTimeStr, name=$name, location=$location, color=$colorStr ,shader=$shader)"
        }
        val endTimeStr = WeekViewUtil.calendarToString(endTime, true)
        return "normalEvent(id=$id, startTime=$colorStr, endTime=$endTimeStr, name=$name, location=$location, color=$colorStr , shader=$shader)"
    }
}
