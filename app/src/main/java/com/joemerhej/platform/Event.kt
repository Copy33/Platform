package com.joemerhej.platform

import android.graphics.Shader
import android.os.Parcel
import android.os.Parcelable
import com.joemerhej.androidweekview.WeekViewEvent
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
        var owner: String? = null,
        var location: String? = null,
        var reminder: Calendar? = null,
        var client: Client? = null,
        allDay: Boolean = false,
        var eventStatus: Event.EventStatus = EventStatus.UNCONFIRMED,
        var amountPaid: Double = 0.0,
        var recurrent: Boolean = false,
        var notes: String? = null)
    : WeekViewEvent(id, title, subtitle, startTime, endTime, color, allDay), Parcelable
{

    enum class EventStatus(var value: String)
    {
        UNCONFIRMED("UNCONFIRMED"),
        CONFIRMED("CONFIRMED"),
        PAID("PAID")
    }

    constructor(parcel: Parcel) : this(parcel.readString()!!, parcel.readString()!!, parcel.readString(),
            GregorianCalendar(TimeZone.getTimeZone(parcel.readString())).also { it.timeInMillis = parcel.readLong() },
            GregorianCalendar(TimeZone.getTimeZone(parcel.readString())).also { it.timeInMillis = parcel.readLong() },
            parcel.readInt())
    {
        owner = parcel.readString()
        location = parcel.readString()
        val nextLong = parcel.readLong()
        if(nextLong != 3L)
        {
            val reminderTimeZoneId = parcel.readString()
            reminder = GregorianCalendar(TimeZone.getTimeZone(reminderTimeZoneId)).also { it.timeInMillis = nextLong }
        }
        eventStatus = EventStatus.valueOf(parcel.readString()!!)
        client = parcel.readParcelable(Client::class.java.classLoader)
        amountPaid = parcel.readDouble()
        recurrent = !parcel.readByte().equals(0)
        notes = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(subtitle)
        parcel.writeString(startTime.timeZone.id)
        parcel.writeLong(startTime.timeInMillis)
        parcel.writeString(endTime.timeZone.id)
        parcel.writeLong(endTime.timeInMillis)
        parcel.writeInt(color)
        parcel.writeString(owner)
        parcel.writeString(location)
        reminder?.let {
            parcel.writeLong(it.timeInMillis)
            parcel.writeString(it.timeZone.id)
        }
        if(reminder == null)
            parcel.writeLong(3)
        parcel.writeString(eventStatus.value)
        parcel.writeParcelable(client, flags)
        parcel.writeDouble(amountPaid)
        parcel.writeByte(if(recurrent) 1 else 0)
        parcel.writeString(notes)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    override fun toString(): String
    {
        return "Event(owner=$owner, location=$location, reminder=$reminder, client=$client, eventStatus=$eventStatus, amountPaid=$amountPaid, recurrent=$recurrent, notes=$notes) ${super.toString()}"
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