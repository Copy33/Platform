package com.joemerhej.platform.models

/**
 * Created by Joe Merhej on 10/24/18.
 */
data class Client(var name: String = "",
             var phoneNumbers: MutableList<String> = mutableListOf(), var favoritePhoneNumberIndex:Int = 0,
             var emails: MutableList<String> = mutableListOf(), var favoriteEmailIndex:Int = 0,
             var locations: MutableList<String> = mutableListOf(), var favoriteLocationIndex: Int = 0,
             var balance:Double = 0.0,
             var notes: String = "")
{
    fun clone() : Client
    {
        val client = Client()
        client.name = this.name
        client.phoneNumbers = mutableListOf()
        client.phoneNumbers.addAll(this.phoneNumbers)
        client.favoritePhoneNumberIndex = this.favoritePhoneNumberIndex
        client.emails = mutableListOf()
        client.emails.addAll(this.emails)
        client.favoriteEmailIndex = this.favoriteEmailIndex
        client.locations = mutableListOf()
        client.locations.addAll(this.locations)
        client.favoriteLocationIndex = this.favoriteLocationIndex
        client.balance = this.balance
        client.notes = this.notes
        return client
    }
}
