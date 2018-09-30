package com.joemerhej.androidweekview

import java.util.*

interface WeekDaySubtitleInterpreter
{
    fun getFormattedWeekDaySubtitle(date: Calendar): String
}
