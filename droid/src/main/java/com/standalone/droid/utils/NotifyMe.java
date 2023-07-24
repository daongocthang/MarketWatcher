package com.standalone.droid.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

@SuppressLint("MissingPermission")
public class NotifyMe {
    private final static long[] VibrationPattern = new long[]{100, 1000, 200, 340};

    public static void post(Context context, String channelId, int icon, String title, String content, PendingIntent link) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(link)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(new Random().nextInt(), builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createChannel(Context context, String channelId, CharSequence name) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = manager.getNotificationChannel(channelId);
        if (channel == null) {
            channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setVibrationPattern(VibrationPattern);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
        }
    }
}
