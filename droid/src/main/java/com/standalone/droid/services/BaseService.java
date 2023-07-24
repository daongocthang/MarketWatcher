package com.standalone.droid.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public abstract class BaseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent);
        return START_NOT_STICKY;
    }

    public abstract void onStart(Intent intent);
}
