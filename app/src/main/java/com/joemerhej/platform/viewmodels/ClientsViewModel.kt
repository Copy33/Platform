package com.joemerhej.platform.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joemerhej.platform.models.Client
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by Joe Merhej on 11/14/18.
 */
class ClientsViewModel : ViewModel()
{
    var clients = MutableLiveData<MutableList<Client>>().apply { value = mutableListOf() }
        private set

    var isMocked = false


    // This extension function is necessary since observer won't trigger unless whole list is replaced,
    //  it should be called whenever there were changes in the list
    private fun <T> MutableLiveData<T>.notifyObserver()
    {
        this.value = this.value
    }

    fun getClientsList() : MutableList<Client>
    {
        return clients.value ?: mutableListOf()
    }

    fun getClient(position: Int) : Client?
    {
        return clients.value?.get(position)
    }

    // These functions don't notify the observer since they handle single client transactions,
    //  the view adapter will handle these transactions thus there is no need to refresh the whole list
    fun addClient(client: Client)
    {
        // always add at the end of the list whe not specified
        clients.value?.let {
            addClient(it.size, client)
        }
    }

    fun addClient(position: Int, client: Client)
    {
        clients.value?.add(position, client)
    }

    fun removeClient(client: Client)
    {
        clients.value?.remove(client)
    }

    fun removeClient(position: Int)
    {
        clients.value?.removeAt(position)
    }

    // mock methods
    fun mockClientsList(size: Int)
    {
        if(isMocked || size <= 0) return

        isMocked = true

        doAsync {
            val mockedList = mutableListOf<Client>()
            val locationList = mutableListOf("Avenue Residence 4, Apt. 107, Al Barsha Heights, Dubai")
            for(i in 1..size)
            {
                val rand = Random()
                val randomCode = rand.nextInt(5)
                val randomNumber = rand.nextInt(9000000) + 1000000
                val randomBalance = rand.nextInt(1000) * (if(rand.nextBoolean()) 1 else -1)
                mockedList.add(Client("Client #$i", "+971$randomCode$randomNumber", randomBalance.toDouble(), locationList, 0))
            }

            clients.postValue(mockedList)
        }
    }
}




