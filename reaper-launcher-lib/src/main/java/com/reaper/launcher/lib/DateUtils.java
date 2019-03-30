package com.reaper.launcher.lib;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.util.Calendar;

public class DateUtils {

    private DateUtils() {
        super();
    }

    public static long remainingMilliSecondsToTheNext15Minutes(){
        long millis = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        int hours = c.get(Calendar.HOUR);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);

        LocalTime t = LocalTime.of(hours, minutes, seconds);
        return (t.with(next15Minutes()).toSecondOfDay() - t.toSecondOfDay()) * 1000;
    }

    private static TemporalAdjuster next15Minutes() {
        return temporal -> {
            int minute = temporal.get(ChronoField.MINUTE_OF_DAY);
            int next15 = (minute / 15 + 1) * 15;
            return temporal.with(ChronoField.NANO_OF_DAY, 0).plus(next15, ChronoUnit.MINUTES);
        };
    }
}
