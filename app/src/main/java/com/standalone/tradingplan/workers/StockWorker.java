package com.standalone.tradingplan.workers;


import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.standalone.droid.utils.NotifyMe;
import com.standalone.tradingplan.R;

public class StockWorker extends Worker {

    public static final String CHANNEL_ID = "DemoNotificationChannelId";


    public StockWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotifyMe.createChannel(context, CHANNEL_ID, "DemoWorker");
        }

        String title = context.getResources().getString(R.string.app_name);

        NotifyMe.post(context,CHANNEL_ID , R.drawable.ic_calendar, title, "Testing", null);

        return Result.retry();
    }
}
