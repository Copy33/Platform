package com.joemerhej.platform.sharedpreferences;

/**
 * Created by Joe Merhej on 9/28/18.
 */
public enum SharedPreferencesKey
{
    // IMPORTANT, the strings shouldn't be concatative, because when reading profiles from shared preferences we are searching for string value in shared preferences key
    VISIBLE_DAYS_NUMBER("vdn");


    private final String value;

    SharedPreferencesKey(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}

