package com.joemerhej.platform.models

/**
 * Created by Joe Merhej on 10/24/18.
 */
data class Owner(var name: String = "",
                 var imageUri: String = "")
{
    /**
     * clone function will deep copy data class into a clone object.
     * This is required since default copy() function does shallow copy.
     */
    fun clone(): Owner
    {
        val owner = Owner()
        owner.name = this.name
        owner.imageUri = this.imageUri
        return owner
    }
}