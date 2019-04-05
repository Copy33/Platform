package com.joemerhej.platform.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joemerhej.platform.models.Event
import org.jetbrains.anko.doAsync
import java.util.*

/**
 * Created by Joe Merhej on 10/29/18.
 */
class EventsViewModel : ViewModel()
{
    var events = MutableLiveData<MutableList<Event>>().apply { value = mutableListOf() }
        private set

    private var isMocked = false


    // This extension function is necessary since observer won't trigger unless whole list is replaced,
    //  it should be called whenever there were changes in the list
    private fun <T> MutableLiveData<T>.notifyObserver()
    {
        this.value = this.value
    }

    fun getEventsList() : MutableList<Event>
    {
        return events.value ?: mutableListOf()
    }

    fun getEvent(position: Int) : Event?
    {
        events.value?.let {
            if(position < 0 || position >= it.size)
                return null
        } ?: return null

        return getEventsList()[position]
    }

    // These functions don't notify the observer since they handle single client transactions,
    //  the view adapter will handle these transactions thus there is no need to refresh the whole list
    fun addEvent(event: Event)
    {
        events.value?.let {
            addEvent(it.size, event)
        }
    }

    fun addEvent(position: Int, client: Event)
    {
        if(position < 0)
            return

        events.value?.add(position, client)
    }

    fun removeEvent(client: Event)
    {
        events.value ?: return
        events.value?.remove(client)
    }

    fun removeEvent(position: Int)
    {
        events.value?.let {
            if(position < 0 || position >= it.size)
                return
        } ?: return

        events.value?.removeAt(position)
    }

    // mock methods
    fun mockEvents(size: Int)
    {
        if(isMocked || size <= 0) return

        isMocked = true

        doAsync {
            val rand = Random()
            val mockedList = mutableListOf<Event>()

            val notes = "this is a mocked event example notes. this is a mocked event example notes. this is a mocked event example notes. this is a mocked event example notes."
            for(i in 1..size)
            {
                val randomDay = rand.nextInt(10)
                val randomHour = rand.nextInt(2)
                val startTime = Calendar.getInstance()
                startTime.add(Calendar.DATE, randomDay)
                startTime.add(Calendar.HOUR_OF_DAY, randomHour)
                val randomDuration = rand.nextInt(4)+1
                val endTime = Calendar.getInstance()
                endTime.add(Calendar.DATE, randomDay)
                endTime.add(Calendar.HOUR_OF_DAY, randomHour)
                endTime.add(Calendar.HOUR_OF_DAY, randomDuration)
                mockedList.add(Event(i.toString(), "random #$i", "subtitle$i", startTime, endTime, 0, false,
                        null, null, null, null, Event.EventStatus.NONE, 0.0, false, notes))
            }

            events.postValue(mockedList)
        }
    }
}
