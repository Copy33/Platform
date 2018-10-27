package com.joemerhej.platform.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joemerhej.platform.R


class EditEventActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

//        val today = WeekViewUtil.today()
//        val startTime = today.clone() as Calendar
//        startTime.set(Calendar.HOUR_OF_DAY, 11)
//        startTime.set(Calendar.MINUTE, 24)
//        startTime.set(Calendar.SECOND, 13)
//        val endTime = startTime.clone() as Calendar
//        endTime.add(Calendar.HOUR, 1)
//
//        val event = Event("1", "title", "subtitle", startTime, endTime, ContextCompat.getColor(this, R.color.event_color_01), false, "owner",
//                "location", null, Client("clientfirst", "clientLast", "clientLocation"), Event.EventStatus.PAID, 12.0, true, "notes")
//        Log.d(DebugUtils.TAG, event.toString())
//        intent.putExtra("event", event)
//        setResult(Activity.RESULT_OK, intent)
//        finish()
    }
}
