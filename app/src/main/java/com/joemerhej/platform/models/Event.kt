package com.joemerhej.platform.models

import android.os.Parcel
import android.os.Parcelable
import com.joemerhej.androidweekview.WeekViewEvent
import com.joemerhej.platform.utils.DebugUtils
import java.util.*

/**
 * Created by Joe Merhej on 10/17/18.
 */
class Event(
        id: String,
        title: String,
        subtitle: String?,
        startTime: Calendar,
        endTime: Calendar,
        color: Int,
        isAllDay: Boolean = false,
        var owner: String? = null,
        var location: String? = null,
        var reminder: Calendar? = null,
        var client: Client? = null,
        var eventStatus: EventStatus = EventStatus.NONE,
        var amountPaid: Double = 0.0,
        var isRecurrent: Boolean = false,
        var notes: String? = null)
    : WeekViewEvent(id, title, subtitle, startTime, endTime, color, isAllDay)
{

    enum class EventStatus(var value: String)
    {
        NONE("NONE"),
        UNCONFIRMED("UNCONFIRMED"),
        CONFIRMED_PENDING_PAYMENT("CONFIRMED_PENDING_PAYMENT"),
        PAID("PAID")
    }

    // empty constructor provides an empty event with now as start time and 1 hour duration
    constructor() : this("", "", null, Calendar.getInstance(), Calendar.getInstance().also { it.add(Calendar.HOUR_OF_DAY, 1) }, 0)

    override fun toString(): String
    {
        return "Event(id=$id, title=$title, subtitle=$subtitle, startTime=${DebugUtils.CalendarToString(startTime)}, endTime= ${DebugUtils.CalendarToString(endTime)}, " +
                "isAllDay=$isAllDay, color=$color, owner=$owner, location=$location, reminder=${DebugUtils.CalendarToString(reminder)}, client=$client, " +
                "eventStatus=$eventStatus, amountPaid=$amountPaid, isRecurrent=$isRecurrent, notes=$notes)"
    }
}