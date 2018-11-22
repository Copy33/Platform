package com.joemerhej.platform.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Joe Merhej on 10/24/18.
 */
class Client(var name: String?,
             var phoneNumbers: MutableList<String>? = null, var defaultPhoneNumberIndex:Int = 0,
             var emails: MutableList<String>? = null, var defaultEmailIndex:Int = 0,
             var locations: MutableList<String>? = null, var defaultLocationIndex: Int = 0,
             var balance:Double = 0.0,
             var notes: String? = null) : Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readString())
    {
        val phoneNumbersSize = parcel.readInt()
        if(phoneNumbersSize > 0)
        {
            phoneNumbers = mutableListOf()
            for(index in 0..phoneNumbersSize)
                phoneNumbers?.let {
                    it[index] = parcel.readString()!!
                }
        }
        defaultPhoneNumberIndex = parcel.readInt()

        val emailsSize = parcel.readInt()
        if(emailsSize > 0)
        {
            emails = mutableListOf()
            for(index in 0..emailsSize)
                emails?.let {
                    it[index] = parcel.readString()!!
                }
        }
        defaultEmailIndex = parcel.readInt()

        val locationsSize = parcel.readInt()
        if(locationsSize > 0)
        {
            locations = mutableListOf()
            for(index in 0..locationsSize)
                locations?.let {
                    it[index] = parcel.readString()!!
                }
        }
        defaultLocationIndex = parcel.readInt()

        balance = parcel.readDouble()
        notes = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(name)

        phoneNumbers?.let {
            parcel.writeInt(it.size)
            for(number in it)
                parcel.writeString(number)
            parcel.writeInt(defaultPhoneNumberIndex)
        } ?: parcel.writeInt(0)

        emails?.let {
            parcel.writeInt(it.size)
            for(email in it)
                parcel.writeString(email)
            parcel.writeInt(defaultEmailIndex)
        } ?: parcel.writeInt(0)

        locations?.let {
            parcel.writeInt(it.size)
            for(location in it)
                parcel.writeString(location)
            parcel.writeInt(defaultLocationIndex)
        } ?: parcel.writeInt(0)

        parcel.writeDouble(balance)
        parcel.writeString(notes)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    override fun toString(): String
    {
        return "Client(name=$name, phoneNumbers=$phoneNumbers, defaultPhoneNumberIndex=$defaultPhoneNumberIndex, " +
                "emails=$emails, defaultEmailIndex=$defaultEmailIndex, locations=$locations, defaultLocationIndex=$defaultLocationIndex, " +
                "balance=$balance, notes=$notes)"
    }

    companion object CREATOR : Parcelable.Creator<Client>
    {
        override fun createFromParcel(parcel: Parcel): Client
        {
            return Client(parcel)
        }

        override fun newArray(size: Int): Array<Client?>
        {
            return arrayOfNulls(size)
        }
    }


}