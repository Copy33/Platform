package com.joemerhej.platform.utils

import java.util.*

/**
 * Created by Joe Merhej on 10/22/18.
 */
object DebugUtils
{
    val TAG: String = "DebugPlat"

    fun CalendarToString(c: Calendar?) : String
    {
        c?.let {
            return "${it.get(Calendar.DAY_OF_MONTH)}/${it.get(Calendar.MONTH)}/${it.get(Calendar.YEAR)} " +
                    "@${it.get(Calendar.HOUR_OF_DAY)}:${it.get(Calendar.MINUTE)}:${it.get(Calendar.SECOND)}:${it.get(Calendar.MILLISECOND)}"
        } ?: return ""
    }
}