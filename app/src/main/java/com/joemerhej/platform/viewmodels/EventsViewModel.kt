package com.joemerhej.platform.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joemerhej.platform.Event

/**
 * Created by Joe Merhej on 10/29/18.
 */
class EventsViewModel : ViewModel()
{
    var events = MutableLiveData<MutableList<Event>>().apply { value = mutableListOf() }
        private set

    fun addEvent(event: Event)
    {
        events.value?.add(event)
        events.notifyObserver()
    }

    fun removeEvent(event: Event)
    {
        events.value?.remove(event)
        events.notifyObserver()
    }

    // this extension function is necessary since observer won't trigger unless whole list is replaced
    private fun <T> MutableLiveData<T>.notifyObserver()
    {
        this.value = this.value
    }
}