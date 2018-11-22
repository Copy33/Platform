package com.joemerhej.platform.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Joe Merhej on 10/24/18.
 */
class Owner(var name: String?, var imageUri: String?)
{
    override fun toString(): String
    {
        return "Owner(name=$name, imageUri=$imageUri)"
    }
}