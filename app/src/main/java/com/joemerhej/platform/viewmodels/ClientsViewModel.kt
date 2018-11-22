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

    private var isMocked = false


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
        clients.value?.let {
            if(position < 0 || position >= it.size)
                return null
        } ?: return null

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
        if(position < 0)
            return

        clients.value?.add(position, client)
    }

    fun removeClient(client: Client)
    {
        clients.value ?: return
        clients.value?.remove(client)
    }

    fun removeClient(position: Int)
    {
        clients.value?.let {
            if(position < 0 || position >= it.size)
                return
        } ?: return

        clients.value?.removeAt(position)
    }

    // mock methods
    fun mockClientsList(size: Int)
    {
        if(isMocked || size <= 0) return

        isMocked = true

        doAsync {
            val rand = Random()
            val mockedList = mutableListOf<Client>()
            val locationsList = mutableListOf("Avenue Residence 4, Apt. 107, Al Barsha Heights, Dubai", "Bay Square Building 21, 3rd floor, Abu Dhabi near the knowledge village place")
            val emailsList = mutableListOf("blablabla@hotmail.com", "idontcareatall@gmail.com", "misterandmisses@yes.ae")
            val randomCode1 = rand.nextInt(6)
            val randomCode2 = rand.nextInt(6)
            val randomNumber1 = rand.nextInt(9000000) + 1000000
            val randomNumber2 = rand.nextInt(9000000) + 1000000
            val randomNumber3 = rand.nextInt(9000000) + 1000000
            val randomPhoneNumber1 = "+971$randomCode1$randomNumber1"
            val randomPhoneNumber2 = "+971$randomCode1$randomNumber2"
            val randomPhoneNumber3 = "+971$randomCode2$randomNumber3"
            val phoneNumbersList = mutableListOf<String>(randomPhoneNumber1, randomPhoneNumber2, randomPhoneNumber3)

            val notes = "this is example notes of the client, here a user can input anything related to client preferences or really anything they want.\nThey can also put as many new\nlines as they want bla bla bla..."
            for(i in 1..size)
            {
                val defaultLocationIndex = rand.nextInt(2)
                val defaultEmailIndex = rand.nextInt(3)
                val defaultPhoneNumberIndex = rand.nextInt(3)
                val randomBalance = rand.nextInt(1000) * (if(rand.nextBoolean()) 1 else -1)
                mockedList.add(Client("Client #$i", phoneNumbersList, defaultPhoneNumberIndex, emailsList, defaultEmailIndex, locationsList, defaultLocationIndex, randomBalance.toDouble(), notes))
            }

            clients.postValue(mockedList)
        }
    }
}




