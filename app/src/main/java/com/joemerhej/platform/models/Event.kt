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
    : WeekViewEvent(id, title, subtitle, startTime, endTime, color, isAllDay), Parcelable
{

    enum class EventStatus(var value: String)
    {
        NONE("NONE"),
        UNCONFIRMED("UNCONFIRMED"),
        CONFIRMED_PENDING_PAYMENT("CONFIRMED_PENDING_PAYMENT"),
        PAID("PAID")
    }

    constructor(parcel: Parcel) : this(parcel.readString()!!, parcel.readString()!!, parcel.readString(),
            Calendar.getInstance().also { it.timeInMillis = parcel.readLong() },
            Calendar.getInstance().also { it.timeInMillis = parcel.readLong() },
            parcel.readInt(), parcel.readInt() != 0)
    {
        owner = parcel.readString()
        location = parcel.readString()
        val nextLong = parcel.readLong()
        if(nextLong != Long.MIN_VALUE)
        {
            reminder = Calendar.getInstance().also { it.timeInMillis = parcel.readLong() }
        }
        client = parcel.readParcelable(Client::class.java.classLoader)
        eventStatus = EventStatus.valueOf(parcel.readString()!!)
        amountPaid = parcel.readDouble()
        isRecurrent = parcel.readInt() != 0
        notes = parcel.readString()
    }

    // empty constructor provides an empty event with now as start time and 1 hour duration
    constructor() : this("", "", null, Calendar.getInstance(), Calendar.getInstance().also { it.add(Calendar.HOUR_OF_DAY, 1) }, 0)

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(subtitle)
        parcel.writeLong(startTime.timeInMillis)
        parcel.writeLong(endTime.timeInMillis)
        parcel.writeInt(color)
        parcel.writeInt(if(isAllDay) 1 else 0)
        parcel.writeString(owner)
        parcel.writeString(location)
        reminder?.let {
            parcel.writeLong(it.timeInMillis)
        } ?: parcel.writeLong(Long.MIN_VALUE)
        parcel.writeParcelable(client, flags)
        parcel.writeString(eventStatus.value)
        parcel.writeDouble(amountPaid)
        parcel.writeInt(if(isRecurrent) 1 else 0)
        parcel.writeString(notes)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    override fun toString(): String
    {
        return "Event(id=$id, title=$title, subtitle=$subtitle, startTime=${DebugUtils.CalendarToString(startTime)}, endTime= ${DebugUtils.CalendarToString(endTime)}, " +
                "isAllDay=$isAllDay, color=$color, owner=$owner, location=$location, reminder=${DebugUtils.CalendarToString(reminder)}, client=$client, " +
                "eventStatus=$eventStatus, amountPaid=$amountPaid, isRecurrent=$isRecurrent, notes=$notes)"
    }


    companion object CREATOR : Parcelable.Creator<Event>
    {
        override fun createFromParcel(parcel: Parcel): Event
        {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?>
        {
            return arrayOfNulls(size)
        }
    }


}