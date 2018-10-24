package com.joemerhej.platform

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Joe Merhej on 10/24/18.
 */
class Client(var firstName: String?, var lastName: String?, var location: String?) : Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())
    {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(location)
    }

    override fun describeContents(): Int
    {
        return 0
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