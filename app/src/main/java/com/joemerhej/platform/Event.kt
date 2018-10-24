package com.joemerhej.platform

import android.graphics.Shader
import com.joemerhej.androidweekview.WeekViewEvent
import java.util.*

/**
 * Created by Joe Merhej on 10/17/18.
 */
class Event(
        id: String?,
        title: String?,
        subtitle: String?,
        startTime: Calendar,
        endTime: Calendar,
        color: Int,
        var owner: String?,
        var location: String?,
        var reminder: Calendar?,
        var client: Client?,
        allDay: Boolean = false,
        var eventStatus: Event.EventStatus = EventStatus.UNCONFIRMED,
        var amountPaid: Double = 0.0,
        var recurrent: Boolean = false,
        var notes: String? = null,
        shader: Shader? = null)
    : WeekViewEvent(id, title, subtitle, startTime, endTime, color, allDay, shader)
{
    enum class EventStatus
    {
        UNCONFIRMED,
        CONFIRMED,
        PAID
    }

}