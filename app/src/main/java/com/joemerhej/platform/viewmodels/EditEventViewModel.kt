package com.joemerhej.platform.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joemerhej.platform.Event

/**
 * Created by Joe Merhej on 10/29/18.
 */
class EditEventViewModel : ViewModel()
{
    var event: MutableLiveData<Event> = MutableLiveData()
}