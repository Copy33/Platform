package com.joemerhej.platform;

import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private WeekView mWeekView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeekView = findViewById(R.id.weekView);

        WeekView.EventClickListener mEventClickListener = new WeekView.EventClickListener()
        {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect)
            {
                Log.d("asd", "EventClickListener");
            }
        };

        MonthLoader.MonthChangeListener mMonthChangeListener = new MonthLoader.MonthChangeListener()
        {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth)
            {
                Log.d("asd", "MonthClickListener");
                List<WeekViewEvent> events = new ArrayList<>();
                return events;
            }
        };

        WeekView.EventLongPressListener mEventLongPressListener = new WeekView.EventLongPressListener()
        {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect)
            {
                Log.d("asd", "EventLongPressListener");
            }
        };

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(mEventClickListener);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(mMonthChangeListener);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(mEventLongPressListener);
    }
}
