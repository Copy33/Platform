package com.joemerhej.platform.sharedpreferences;

/**
 * Created by Joe Merhej on 9/28/18.
 */
public enum SharedPreferencesKey
{
    // IMPORTANT! the strings shouldn't be concatenated (ex: "as" and "asd"), because
    // when reading profiles from shared preferences we are searching for string
    // in shared preferences key (ex: searching for "as" will return "asd" values)
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

