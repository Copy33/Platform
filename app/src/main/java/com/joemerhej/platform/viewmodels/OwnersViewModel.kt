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

    var isMocked = false


    // This extension function is necessary since observer won't trigger unless whole list is replaced,
    //  it should be called whenever there were changes in the list
    private fun <T> MutableLiveData<T>.notifyObserver()
    {
        this.value = this.value
    }

    fun getOwnersList(): MutableList<Owner>
    {
        return owners.value ?: mutableListOf()
    }

    fun getOwner(position: Int): Owner?
    {
        owners.value?.let {
            if(position < 0 || position >= it.size)
                return null
        } ?: return null

        return owners.value?.get(position)
    }

    // These functions don't notify the observer since they handle single owner transactions,
    //  the view adapter will handle these transactions thus there is no need to refresh the whole list
    fun addOwner(owner: Owner)
    {
        // always add at the end of the list whe not specified
        owners.value?.let {
            addOwner(it.size, owner)
        }
    }

    fun addOwner(position: Int, owner: Owner)
    {
        owners.value?.add(position, owner)
    }

    fun removeOwner(owner: Owner)
    {
        owners.value ?: return
        owners.value?.remove(owner)
    }

    fun removeOwner(position: Int)
    {
        owners.value?.let {
            if(position < 0 || position >= it.size)
                return
        } ?: return

        owners.value?.removeAt(position)
    }

    // mock methods
    fun mockOwnersList(size: Int)
    {
        if(isMocked || size <= 0) return

        isMocked = true

        for(i in 1..size)
            owners.value?.add(Owner("Owner #$i", ""))
    }
}
