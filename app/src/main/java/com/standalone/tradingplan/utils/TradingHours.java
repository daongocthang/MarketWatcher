package com.standalone.tradingplan.utils;

import java.util.Calendar;


public class TradingHours {
    static final int[] OPENING_IN_MORNING = {9, 0};
    static final int[] CLOSING_IN_MORNING = {11, 30};
    static final int[] OPENING_IN_AFTERNOON = {13, 0};
    static final int[] CLOSING_IN_AFTERNOON = {15, 0};
    static final long PERIOD_OF_WATCHING = 60000;

    static boolean greaterOrEqual(int[] time, Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return hour >= time[0] && minute >= time[1];
    }

    static boolean lessOrEqual(int[] time, Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return hour <= time[0] && minute <= time[1];
    }

    static boolean marketOpening(Calendar calendar) {
        boolean morningSession = greaterOrEqual(OPENING_IN_MORNING, calendar) && lessOrEqual(CLOSING_IN_MORNING, calendar);
        boolean afternoonSession = greaterOrEqual(OPENING_IN_AFTERNOON, calendar) && lessOrEqual(CLOSING_IN_AFTERNOON, calendar);

        return morningSession || afternoonSession;
    }

    public static long getTimeMillis() {
        Calendar calendar = Calendar.getInstance();

        if (marketOpening(calendar)) return PERIOD_OF_WATCHING;

        int startTimeInHours = OPENING_IN_MORNING[0];

        if (greaterOrEqual(CLOSING_IN_MORNING, calendar)) {
            startTimeInHours = OPENING_IN_AFTERNOON[0];
        }

        if (greaterOrEqual(CLOSING_IN_AFTERNOON, calendar)) {
            int days = (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) ? 2 : 1;
            calendar.add(Calendar.DAY_OF_YEAR, days);
            startTimeInHours = OPENING_IN_MORNING[0];
        }

        calendar.set(Calendar.HOUR_OF_DAY, startTimeInHours);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }
}



