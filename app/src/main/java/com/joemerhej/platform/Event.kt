package com.joemerhej.platform

import android.graphics.Shader
import android.location.Location
import com.joemerhej.androidweekview.WeekViewEvent
import com.joemerhej.androidweekview.WeekViewUtil
import java.util.*

/**
 * Created by Joe Merhej on 10/17/18.
 */
class Event(var owner: String, var location: Location, id: String?, title: String?, subtitle: String?, startTime: Calendar, endTime: Calendar, color:Int, allDay: Boolean = false, shader: Shader? = null)
    : WeekViewEvent(id, title, subtitle, startTime, endTime, color, allDay, shader)
{
    init
    {
        owner = "Joe"
    }

}