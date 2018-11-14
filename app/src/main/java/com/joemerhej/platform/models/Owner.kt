package com.joemerhej.platform.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Joe Merhej on 10/24/18.
 */
class Owner(var firstName: String?, var lastName: String?) : Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    override fun toString(): String
    {
        return "Client(firstName=$firstName, lastName=$lastName)"
    }

    companion object CREATOR : Parcelable.Creator<Owner>
    {
        override fun createFromParcel(parcel: Parcel): Owner
        {
            return Owner(parcel)
        }

        override fun newArray(size: Int): Array<Owner?>
        {
            return arrayOfNulls(size)
        }
    }


}