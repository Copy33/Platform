package com.joemerhej.androidweekview

import java.util.*

/**
 * Interface that user can implement to return the desired format of how the week day
 * names (top of calendar)and the time of day (left of calendar) will be displayed.
 */
interface DateTimeInterpreter
{
    fun getFormattedWeekDayTitle(date: Calendar): String

    fun getFormattedTimeOfDay(hour: Int, minutes: Int): String
}
