package com.joemerhej.platform

/**
 * Event manager class will take care of all event storage and actions (serving, fetching...etc)
 * This class will keep track of active month events in addition to the previous and next month events.
 *
 * Created by Joe Merhej on 10/17/18.
 */
object EventManager
{
    private var activeMonthEvents: MutableList<Event>? = null
    private var previousMonthEvents: MutableList<Event>? = null
    private var nextMonthEvents: MutableList<Event>? = null
    private var activeMonth: Int = 0
    private var activeMonthYear: Int = 0
    private var previousMonth: Int = 0
    private var previousMonthYear: Int = 0
    private var nextMonth: Int = 0
    private var nextMonthYear: Int = 0

    /**
     * Callback function handling returning events
     */
    interface EventsReturnedCallback
    {
        fun eventsReturnedSuccessfully(events: MutableList<Event>)
        fun errorReturningEvents(error: Error)
    }


    /**
     * function that will set the active month and all other EventManager variables based on it.
     *
     * @param month the active month to set.
     * @param year the year of the active month to set.
     */
    fun setActiveMonth(month: Int, year: Int)
    {
        activeMonth = month
        activeMonthYear = year
        //TODO CONTINUE SETTING THIS UP
    }

    /**
     * function that will return the events of a specific month.
     *
     * @param month the month to return its events.
     * @param year the year of the month to return its events.
     * @return events of specific month.
     */
    fun getMonthEvents(month: Int, year: Int) : MutableList<Event>?
    {
        // if the month is somehow the same as active month, do nothing
        if(month == activeMonth && year == activeMonthYear)
        {
            return activeMonthEvents
        }

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
            fetchMonthEvents(previousMonth, previousMonthYear, object: EventsReturnedCallback {
                override fun eventsReturnedSuccessfully(events: MutableList<Event>)
                {
                    previousMonthEvents = events
                }

                override fun errorReturningEvents(error: Error)
                {
                    // Do nothing for now
                }
            })

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
            fetchMonthEvents(nextMonth, nextMonthYear, object: EventsReturnedCallback {
                override fun eventsReturnedSuccessfully(events: MutableList<Event>)
                {
                    nextMonthEvents = events
                }

                override fun errorReturningEvents(error: Error)
                {
                    // Do nothing for now
                }
            })

            return activeMonthEvents
        }

        // if fast scroll or random call, aka completely new month
        else
        {
            activeMonth = month
            activeMonthYear = year
            fetchMonthEvents(activeMonth, activeMonthYear, object: EventsReturnedCallback {
                override fun eventsReturnedSuccessfully(events: MutableList<Event>)
                {
                    activeMonthEvents = events
                }

                override fun errorReturningEvents(error: Error)
                {
                    // Do nothing for now
                }
            })

            previousMonth = if(month == 1) 12 else month-1
            previousMonthYear = if(month == 1) year-1 else year
            fetchMonthEvents(previousMonth, previousMonthYear, object: EventsReturnedCallback {
                override fun eventsReturnedSuccessfully(events: MutableList<Event>)
                {
                    previousMonthEvents = events
                }

                override fun errorReturningEvents(error: Error)
                {
                    // Do nothing for now
                }
            })

            nextMonth = if(month == 12) 1 else month+1
            nextMonthYear = if(month == 12) year+1 else year
            fetchMonthEvents(nextMonth, nextMonthYear, object: EventsReturnedCallback {
                override fun eventsReturnedSuccessfully(events: MutableList<Event>)
                {
                    nextMonthEvents = events
                }

                override fun errorReturningEvents(error: Error)
                {
                    // Do nothing for now
                }
            })

            return activeMonthEvents
        }
    }

    /**
     * function that will fetch the events of a specific month from the database
     *
     * @param month the month to return its events.
     * @param year the year of the month to return its events.
     * @param callback the events returned callback handling returns.
     */
    private fun fetchMonthEvents(month: Int, year: Int, callback: EventsReturnedCallback)
    {
    }

    /**
     * function that will add an event.
     *
     * @param month the month to return its events.
     * @param year the year of the month to return its events.
     * @param callback the events returned callback handling returns.
     */
}























