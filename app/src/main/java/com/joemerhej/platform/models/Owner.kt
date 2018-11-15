package com.joemerhej.platform.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Joe Merhej on 10/24/18.
 */
class Owner(var name: String?, var imageUri: String?) : Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(name)
        parcel.writeString(imageUri)
    }

    override fun describeContents(): Int
    {
        return 0
    }

    override fun toString(): String
    {
        return "Owner(name=$name, imageUri=$imageUri)"
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