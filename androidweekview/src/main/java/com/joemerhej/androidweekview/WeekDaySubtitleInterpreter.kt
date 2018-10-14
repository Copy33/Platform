package com.joemerhej.androidweekview

import java.util.*

/**
 * Interface that user can implement to return the desired format of how the week day
 * subtitle displayed (ex: day number displayed under weekday name).
 */
interface WeekDaySubtitleInterpreter
{
    fun getFormattedWeekDaySubtitle(date: Calendar): String
}
