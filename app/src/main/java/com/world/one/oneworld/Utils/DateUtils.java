package com.world.one.oneworld.Utils;

import java.util.Calendar;
import java.util.TimeZone;

public class DateUtils {

    public static long getCurrentUnixTimeStampWithTime() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().getTime();
    }

    public static long getDifferenceInMinutes(long startDateTime, long endDateTime) {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;

        long difference = endDateTime - startDateTime;
        return (difference / minutesInMilli);
    }
}
