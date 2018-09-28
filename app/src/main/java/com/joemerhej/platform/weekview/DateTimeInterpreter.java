package com.joemerhej.platform.weekview;

import java.util.Calendar;

/**
 * Created by Joe Merhej on 9/27/18.
 */
public interface DateTimeInterpreter
{
    String interpretDate(Calendar date);

    String interpretTime(int hour);
}
