package com.joemerhej.platform.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joe Merhej on 9/28/18.
 */
public class SharedPreferencesManager
{
    // instance of shared preferences
    private static SharedPreferences mSharedPref;

    // private constants
    private static final String SCHEDULER_PREFIX = "scheduler_";


    private SharedPreferencesManager()
    {
    }

    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
    }

    public static String readString(SharedPreferencesKey key, String defaultValue)
    {
        return mSharedPref.getString(SCHEDULER_PREFIX + key.getValue(), defaultValue);
    }

    public static void writeString(SharedPreferencesKey key, String value)
    {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(SCHEDULER_PREFIX + key.getValue(), value);
        prefsEditor.apply();
    }

    public static boolean readBoolean(SharedPreferencesKey key, boolean defaultValue)
    {
        return mSharedPref.getBoolean(SCHEDULER_PREFIX + key.getValue(), defaultValue);
    }

    public static void writeBoolean(SharedPreferencesKey key, boolean value)
    {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(SCHEDULER_PREFIX + key.getValue(), value);
        prefsEditor.apply();
    }

    public static int readInt(SharedPreferencesKey key, int defaultValue)
    {
        return mSharedPref.getInt(SCHEDULER_PREFIX + key.getValue(), defaultValue);
    }

    public static void writeInt(SharedPreferencesKey key, int value)
    {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(SCHEDULER_PREFIX + key.getValue(), value).apply();
    }

    public static Long readLong(SharedPreferencesKey key)
    {
        String longString = mSharedPref.getString(SCHEDULER_PREFIX + key.getValue(), "");

        Long result = 0L;
        try
        {
            result = Long.valueOf(longString);
        }
        catch(NumberFormatException e)
        {
            return 0L;
        }

        return result;
    }

    public static void writeLong(SharedPreferencesKey key, Long value)
    {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(SCHEDULER_PREFIX + key.getValue(), value.toString());
        prefsEditor.apply();
    }

    public static void removePreference(SharedPreferencesKey key)
    {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove(SCHEDULER_PREFIX + key.getValue()).apply();
    }

    public static void clearSharedPreferences()
    {
        mSharedPref.edit().clear().apply();
    }
    
}
