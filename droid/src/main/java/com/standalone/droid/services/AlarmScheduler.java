package com.standalone.droid.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class AlarmScheduler {
    private Context context;

    public static AlarmScheduler from(Context context) {
        AlarmScheduler self = new AlarmScheduler();
        self.context = context;
        return self;
    }

    public PendingIntent getBroadcast(int requestCode, Class<? extends BroadcastReceiver> className) {
        Intent intent = new Intent(context, className);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        }

        /*
         * Returns an existing or new PendingIntent matching the given parameters.
         * May return null only if FLAG_NO_CREATE has been supplied.
         */
        return PendingIntent.getBroadcast(context, requestCode, intent, flags);
    }


    @SuppressLint("MissingPermission")
    public void setAlarm(PendingIntent pendingIntent, long triggerAtMillis) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    public void setRepeatingAlarm(PendingIntent pendingIntent, long triggerAtMillis, long intervalMillis) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
    }

    public void setDailyAlarm(PendingIntent pendingIntent, String time) {
        String[] splitTime = time.split(":");
        int hours = Integer.parseInt(splitTime[0]);
        int minutes = Integer.parseInt(splitTime[1]);

        Calendar calendar = Calendar.getInstance();
        // if it's after or equal ? schedule for next day
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= hours) {
            calendar.add(Calendar.DAY_OF_YEAR, 1); // add, not set!
        }

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);

        setRepeatingAlarm(pendingIntent, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY);
    }

    public void cancelAlarm(PendingIntent pendingIntent) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }
}
