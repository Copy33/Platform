package com.joemerhej.platform.models

/**
 * Created by Joe Merhej on 10/24/18.
 */
data class Owner(var name: String = "", var imageUri: String = "")
{
    fun clone() : Owner
    {
        val owner = Owner()
        owner.name = this.name
        owner.imageUri = this.imageUri
        return owner
    }
}