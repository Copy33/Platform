package com.joemerhej.platform

/**
 * Created by Joe Merhej on 10/17/18.
 */
class EventManager
{
    var activeMonthEvents: MutableList<Event>? = null
    var previousMonthEvents: MutableList<Event>? = null
    var nextMonthEvents: MutableList<Event>? = null
    var activeMonth: Int = 0
    var activeMonthYear: Int = 0
    var previousMonth: Int = 0
    var previousMonthYear: Int = 0
    var nextMonth: Int = 0
    var nextMonthYear: Int = 0

    fun getMonthEvents(month: Int, year: Int) : MutableList<Event>?
    {
        // if the month is somehow the same as active month, do nothing
        if(month == activeMonth && year == activeMonthYear)
            return activeMonthEvents

        // if we're scrolling backwards, aka new month is previous month
        if(month == previousMonth && year == previousMonthYear)
        {
            nextMonth = activeMonth
            nextMonthYear = activeMonthYear
            nextMonthEvents = activeMonthEvents

            activeMonth = previousMonth
            activeMonthYear = previousMonthYear
            activeMonthEvents = previousMonthEvents

            previousMonth = if(month == 1) 12 else month-1
            previousMonthYear = if(month == 1) year-1 else year
            previousMonthEvents = fetchMonthEvents(previousMonth, previousMonthYear)

            return activeMonthEvents
        }

        // if we're scrolling forward, aka new month is next month
        else if(month == nextMonth && activeMonthYear == nextMonthYear)
        {
            previousMonth = activeMonth
            previousMonthYear = activeMonthYear
            previousMonthEvents = activeMonthEvents

            activeMonth = nextMonth
            activeMonthYear = nextMonthYear
            activeMonthEvents = nextMonthEvents

            nextMonth = if(month == 12) 1 else month+1
            nextMonthYear = if(month == 12) year+1 else year
            nextMonthEvents = fetchMonthEvents(nextMonth, nextMonthYear)

            return activeMonthEvents
        }

        // if fast scroll or random call, aka completely new month
        else
        {
            activeMonth = month
            activeMonthYear = year
            activeMonthEvents = fetchMonthEvents(activeMonth, activeMonthYear)

            previousMonth = if(month == 1) 12 else month-1
            previousMonthYear = if(month == 1) year-1 else year
            previousMonthEvents = fetchMonthEvents(previousMonth, previousMonthYear)

            nextMonth = if(month == 12) 1 else month+1
            nextMonthYear = if(month == 12) year+1 else year
            nextMonthEvents = fetchMonthEvents(nextMonth, nextMonthYear)

            return activeMonthEvents
        }
    }

    // fetch events for single month
    private fun fetchMonthEvents(month: Int, year: Int) : MutableList<Event>?
    {
        return null
    }

}