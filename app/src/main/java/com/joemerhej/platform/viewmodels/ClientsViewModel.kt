package com.joemerhej.platform.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joemerhej.platform.models.Client

/**
 * Created by Joe Merhej on 11/14/18.
 */
class ClientsViewModel : ViewModel()
{
    var clients = MutableLiveData<MutableList<Client>>().apply { value = mutableListOf() }
        private set


    fun addClient(client: Client)
    {
        clients.value?.add(client)
        clients.notifyObserver()
    }

    fun removeClient(client: Client)
    {
        clients.value?.remove(client)
        clients.notifyObserver()
    }

    // this extension function is necessary since observer won't trigger unless whole list is replaced
    private fun <T> MutableLiveData<T>.notifyObserver()
    {
        this.value = this.value
    }
}
