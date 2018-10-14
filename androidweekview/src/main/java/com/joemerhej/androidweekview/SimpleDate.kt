package com.joemerhej.androidweekview

import java.util.*

/**
 * Convenience class that takes a Calendar object and transforms it into a simple 3 integer class for year, month, and day.
 */
data class SimpleDate(val year: Int, val month: Int, val dayOfMonth: Int)
{
    override fun toString(): String = "$year-$month-$dayOfMonth"

    constructor(cal: Calendar) : this(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
}
