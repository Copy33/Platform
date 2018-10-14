package com.joemerhej.androidweekview

import android.util.Log

/**
 * Measures the time between two endpoints (start and end) and returns the total time and the average.
 * This is used to test the performance of drawing the views.
 */
class DrawPerformanceTester(val measureDrawTime: Boolean = true)
{
    var drawSamplesCount = 0L
    var drawTotalTime = 0L

    private var startTime: Long = 0L

    fun startMeasure()
    {
        if (!measureDrawTime)
            return
        startTime = System.currentTimeMillis()
    }

    fun endMeasure()
    {
        if (!measureDrawTime)
            return
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime
        ++drawSamplesCount
        drawTotalTime += totalTime
        val drawAverageTime = drawTotalTime.toFloat() / drawSamplesCount.toFloat()
        Log.d("PERF", "currentTime:$totalTime average:$drawAverageTime")
    }
}
