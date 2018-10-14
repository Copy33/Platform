package com.joemerhej.androidweekview

import androidx.annotation.ColorInt

/**
 * Interface that takes a WeekViewEvent and returns its text color (annotated to be a Color Id).
 */
interface TextColorPicker
{
    @ColorInt
    fun getTextColor(event: WeekViewEvent): Int
}
