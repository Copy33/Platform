package com.joemerhej.platform.sharedpreferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Joe Merhej on 9/28/18.
 */
object SharedPreferencesManager
{
    // instance of shared preferences
    private lateinit var instance: SharedPreferences

    // private constants
    private val SCHEDULER_PREFIX = "scheduler_"


    fun initialize(context: Context)
    {
        instance = context.applicationContext.getSharedPreferences(context.applicationContext.packageName, Context.MODE_PRIVATE)
    }

    fun readString(key: SharedPreferencesKey, defaultValue: String): String?
    {
        return instance.getString(SCHEDULER_PREFIX + key.value, defaultValue)
    }

    fun writeString(key: SharedPreferencesKey, value: String)
    {
        val prefsEditor = instance.edit()
        with(prefsEditor)
        {
            putString(SCHEDULER_PREFIX + key.value, value)
            apply()
        }
    }

    fun readBoolean(key: SharedPreferencesKey, defaultValue: Boolean): Boolean
    {
        return instance.getBoolean(SCHEDULER_PREFIX + key.value, defaultValue)
    }

    fun writeBoolean(key: SharedPreferencesKey, value: Boolean)
    {
        val prefsEditor = instance.edit()
        with(prefsEditor)
        {
            putBoolean(SCHEDULER_PREFIX + key.value, value)
            apply()
        }
    }

    fun readInt(key: SharedPreferencesKey, defaultValue: Int): Int
    {
        return instance.getInt(SCHEDULER_PREFIX + key.value, defaultValue)
    }

    fun writeInt(key: SharedPreferencesKey, value: Int)
    {
        val prefsEditor = instance.edit()
        prefsEditor.putInt(SCHEDULER_PREFIX + key.value, value).apply()
    }

    fun readLong(key: SharedPreferencesKey, defaultValue: Long): Long
    {
        return instance.getLong(SCHEDULER_PREFIX + key.value, defaultValue)
    }

    fun writeLong(key: SharedPreferencesKey, value: Long)
    {
        val prefsEditor = instance.edit()
        prefsEditor.putLong(SCHEDULER_PREFIX + key.value, value).apply()
    }

    fun removePreference(key: SharedPreferencesKey)
    {
        val prefsEditor = instance.edit()
        prefsEditor.remove(SCHEDULER_PREFIX + key.value).apply()
    }

    fun clearSharedPreferences()
    {
        instance.edit().clear().apply()
    }

}
