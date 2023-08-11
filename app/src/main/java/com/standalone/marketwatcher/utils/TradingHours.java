package com.standalone.marketwatcher.utils;

import android.util.Log;

import java.util.Calendar;


public class TradingHours {
    static final int[] OPENING_IN_MORNING = {9, 0};
    static final int[] CLOSING_IN_MORNING = {11, 30};
    static final int[] OPENING_IN_AFTERNOON = {13, 0};
    static final int[] CLOSING_IN_AFTERNOON = {15, 0};
    static final long PERIOD_OF_WATCHING = 500000; // 5 minutes + 60 seconds * 1000 milliseconds

    static boolean greaterOrEqual(int[] time, Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (hour < time[0]) return false;
        if (hour > time[0]) return true;
        return minute >= time[1];
    }

    static boolean lessOrEqual(int[] time, Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (hour > time[0]) return false;
        if (hour < time[0]) return true;

        return minute <= time[1];
    }

    public static boolean marketOpening(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return false;

        boolean morningSession = greaterOrEqual(OPENING_IN_MORNING, calendar) && lessOrEqual(CLOSING_IN_MORNING, calendar);
        boolean afternoonSession = greaterOrEqual(OPENING_IN_AFTERNOON, calendar) && lessOrEqual(CLOSING_IN_AFTERNOON, calendar);

        return morningSession || afternoonSession;
    }

    public static long getTimeMillis() {
        Calendar calendar = Calendar.getInstance();

        if (marketOpening(calendar)) return calendar.getTimeInMillis() + PERIOD_OF_WATCHING;

        int startedTimeInHours = OPENING_IN_MORNING[0];
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int nextDays = 0;
        switch (dayOfWeek) {
            case Calendar.SATURDAY:
                nextDays = 2;
                break;
            case Calendar.SUNDAY:
                nextDays = 1;
                break;
            default:
                if (greaterOrEqual(CLOSING_IN_AFTERNOON, calendar)) {
                    nextDays = dayOfWeek == Calendar.FRIDAY ? 3 : 1;
                } else if (greaterOrEqual(CLOSING_IN_MORNING, calendar)) {
                    startedTimeInHours = OPENING_IN_AFTERNOON[0];
                }
        }

        if (nextDays > 0) calendar.add(Calendar.DAY_OF_YEAR, nextDays);

        calendar.set(Calendar.HOUR_OF_DAY, startedTimeInHours);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }
}



