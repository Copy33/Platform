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


    // this extension function is necessary since observer won't trigger unless whole list is replaced
    private fun <T> MutableLiveData<T>.notifyObserver()
    {
        this.value = this.value
    }

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

    fun getOwnersList(): MutableList<Owner>
    {
        return owners.value ?: mutableListOf()
    }

    fun mockOwnersList(size: Int)
    {
        if(size<=0) return

        for(i in 1..size)
            owners.value?.add(Owner("owner #$i", null))
    }
}
