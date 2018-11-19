package com.joemerhej.platform.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Joe Merhej on 10/24/18.
 */
class Client(var name: String?, var phoneNumber: String?, var balance: Double = 0.0, var locations: MutableList<String>? = null, var defaultLocationIndex: Int = -1, var notes: String? = null) : Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble())
    {
        val locationSize = parcel.readInt()
        if(locationSize > 0)
        {
            locations = mutableListOf()
            for(index in 0..locationSize)
                locations?.let {
                    it[index] = parcel.readString()!!
                }
        }
        defaultLocationIndex = parcel.readInt()
        notes = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(name)
        parcel.writeString(phoneNumber)
        parcel.writeDouble(balance)
        locations?.let {
            parcel.writeInt(it.size)
            for(location in it)
                parcel.writeString(location)
            parcel.writeInt(defaultLocationIndex)
        } ?: parcel.writeInt(0)
        parcel.writeString(notes)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    override fun toString(): String
    {
        return "Client(firstName=$name, balance=$balance, locations=$locations, defaultLocationIndex=$defaultLocationIndex, notes=$notes)"
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