package com.standalone.marketwatcher.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.standalone.droid.services.AlarmScheduler;
import com.standalone.marketwatcher.utils.TradingHours;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmScheduler scheduler = AlarmScheduler.from(context);
            PendingIntent pendingIntent = scheduler.getBroadcast(AlarmReceiver.REQUEST_ALARM, AlarmReceiver.class);
            scheduler.setAlarm(pendingIntent, TradingHours.getTimeMillis());
        }
    }
}
