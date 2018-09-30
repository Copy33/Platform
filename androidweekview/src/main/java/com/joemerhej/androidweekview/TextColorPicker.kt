package com.joemerhej.androidweekview

import android.support.annotation.ColorInt

interface TextColorPicker
{
    @ColorInt
    fun getTextColor(event: WeekViewEvent): Int
}
