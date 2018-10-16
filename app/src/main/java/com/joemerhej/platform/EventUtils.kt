package com.joemerhej.platform

import com.joemerhej.androidweekview.WeekViewEvent
import java.util.*

/**
 * Created by Joe Merhej on 10/16/18.
 */
object EventUtils
{
    // utility function that will get the events only for a specific month
    fun getEventsForMonth(eventList: MutableList<WeekViewEvent>, month: Int, year: Int): MutableList<WeekViewEvent>
    {
        val startOfMonth = Calendar.getInstance()
        startOfMonth.set(Calendar.YEAR, year)
        startOfMonth.set(Calendar.MONTH, month)
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0)
        startOfMonth.set(Calendar.MINUTE, 0)
        startOfMonth.set(Calendar.SECOND, 0)
        startOfMonth.set(Calendar.MILLISECOND, 0)

        val endOfMonth = startOfMonth.clone() as Calendar
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getMaximum(Calendar.DAY_OF_MONTH))
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23)
        endOfMonth.set(Calendar.MINUTE, 59)
        endOfMonth.set(Calendar.SECOND, 59)
        startOfMonth.set(Calendar.MILLISECOND, 999)

        val resultList: MutableList<WeekViewEvent> = mutableListOf()

        for(event in eventList)
            if(event.startTime.timeInMillis > startOfMonth.timeInMillis && event.endTime.timeInMillis < endOfMonth.timeInMillis)
                resultList.add(event)

        return resultList
    }
}