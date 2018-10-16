package com.joemerhej.androidweekview

import androidx.annotation.IntRange
import java.util.*

/**
 * This class provides prefetching data loading behaviour.
 * By setting a specific period of N, data is retrieved for the current period,
 * the next N periods and the previous N periods.
 *
 * PrefetchingWeekViewLoader is a class that implements the WeekViewLoader interface while
 * having an instance of the interface itself as property. The user will then need to define
 * a class that implements the regular WeekViewLoader interface and pass it to this "prefetching" variant.
 *
 *
 * @param weekViewLoader An instance of the WeekViewLoader interface
 * @param prefetchingPeriod The amount of periods to be fetched before and after the
 * current period. Must be 1 or greater.
 */
class PrefetchingWeekViewLoader(val weekViewLoader: WeekViewLoader, @IntRange(from = 1L) val prefetchingPeriod: Int = 1) : WeekViewLoader
{
    init
    {
        if(prefetchingPeriod < 1)
            throw IllegalArgumentException("Must specify prefetching period of at least 1!")
    }

    override fun onLoad(periodIndex: Int): MutableList<WeekViewEvent>?
    {
        // fetch the current period
        var loadedEvents = weekViewLoader.onLoad(periodIndex)
        val events = ArrayList<WeekViewEvent>()
        if(loadedEvents != null)
            events.addAll(loadedEvents)
        // fetch periods before/after
        for(i in 1..this.prefetchingPeriod)
        {
            loadedEvents = weekViewLoader.onLoad(periodIndex - i)
            if(loadedEvents != null)
                events.addAll(loadedEvents)
            loadedEvents = weekViewLoader.onLoad(periodIndex + i)
            if(loadedEvents != null)
                events.addAll(loadedEvents)
        }
        // return list of all events together
        return events
    }

    override fun toWeekViewPeriodIndex(instance: Calendar) = weekViewLoader.toWeekViewPeriodIndex(instance)

}
