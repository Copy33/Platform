package com.joemerhej.androidweekview

import java.util.*

interface DateTimeInterpreter
{
    fun getFormattedWeekDayTitle(date: Calendar): String

    fun getFormattedTimeOfDay(hour: Int, minutes: Int): String
}
