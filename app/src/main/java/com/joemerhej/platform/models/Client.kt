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
             var notes: String? = null)
{
    override fun toString(): String
    {
        return "Client(name=$name, phoneNumbers=$phoneNumbers, defaultPhoneNumberIndex=$defaultPhoneNumberIndex, " +
                "emails=$emails, defaultEmailIndex=$defaultEmailIndex, locations=$locations, defaultLocationIndex=$defaultLocationIndex, " +
                "balance=$balance, notes=$notes)"
    }
}