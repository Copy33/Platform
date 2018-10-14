package com.joemerhej.androidweekview

import java.util.*

/**
 * This class is an implementation of the WeekViewLoader interface, it implements the methods in it.
 * This can be used as is for 1 month or can be passed to a PrefetchingWeekViewLoader class instance
 * to add the "prefetching" functionality to its methods.
 *
 * @param onMonthChangeListener month change listener interface
 */
class MonthLoader(var onMonthChangeListener: MonthChangeListener?) : WeekViewLoader
{

    override fun toWeekViewPeriodIndex(instance: Calendar): Double
    {
        return (instance.get(Calendar.YEAR) * 12).toDouble() + instance.get(Calendar.MONTH).toDouble() + (instance.get(Calendar.DAY_OF_MONTH) - 1) / 31.0
    }

    override fun onLoad(periodIndex: Int): MutableList<out WeekViewEvent>?
    {
        return onMonthChangeListener!!.onMonthChange(periodIndex / 12, periodIndex % 12 + 1)
    }

    interface MonthChangeListener
    {
        /**
         * Very important interface, it's the base to load events in the calendar.
         * This method is called three times: once to load the previous month, once to load the next month and once to load the current month.
         * **That's why you can have three times the same event at the same place if you mess up with the configuration**
         * In the case where this class is passed to a PrefetchingWeekViewLoader, this happens 3 times per load period. (TODO: optimize months loaded twice)
         *
         * @param newYear  : year of the events required by the view.
         * @param newMonth : month of the events required by the view **1 based (not like JAVA API) : January = 1 and December = 12**.
         * @return a list of the events happening **during the specified month**.
         */
        fun onMonthChange(newYear: Int, newMonth: Int): MutableList<out WeekViewEvent>?
    }
}
