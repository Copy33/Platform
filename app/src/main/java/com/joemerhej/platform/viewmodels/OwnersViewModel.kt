package com.joemerhej.platform.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joemerhej.platform.models.Owner

/**
 * Created by Joe Merhej on 11/14/18.
 */
class OwnersViewModel : ViewModel()
{
    var owners = MutableLiveData<MutableList<Owner>>().apply { value = mutableListOf() }
        private set


    fun addOwner(owner: Owner)
    {
        owners.value?.add(owner)
        owners.notifyObserver()
    }

    fun removeOwner(owner: Owner)
    {
        owners.value?.remove(owner)
        owners.notifyObserver()
    }

    // this extension function is necessary since observer won't trigger unless whole list is replaced
    private fun <T> MutableLiveData<T>.notifyObserver()
    {
        this.value = this.value
    }
}
