package com.standalone.marketwatcher.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.standalone.droid.services.AlarmScheduler;
import com.standalone.droid.utils.ListUtils;
import com.standalone.droid.utils.NotifyMe;
import com.standalone.marketwatcher.R;
import com.standalone.marketwatcher.activities.MainActivity;
import com.standalone.marketwatcher.database.OrderDb;
import com.standalone.marketwatcher.models.Order;
import com.standalone.marketwatcher.models.StockRealTime;
import com.standalone.marketwatcher.requests.Broker;
import com.standalone.marketwatcher.utils.AppUtils;
import com.standalone.marketwatcher.utils.TradingHours;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_ALARM = 0;
    public static final int REQUEST_ACTIVITY = 1;
    public static final String CHANNEL_ID = "MatchedMarketPrices";

    Context context;
    OrderDb db;

    AlarmScheduler scheduler;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        scheduler = AlarmScheduler.from(context);
        db = new OrderDb(context);
        if (db.fetchAll().size() == 0) {
            scheduler.cancelAlarm(REQUEST_ALARM, AlarmReceiver.class);
            return;
        }

        Log.e(getClass().getSimpleName(), "Alarm running");
        Calendar calendar = Calendar.getInstance();
        if (TradingHours.marketOpening(calendar) && AppUtils.isNetworkAvailable(context)) {
            onMarketWatching();
        }

        // set Alarm Schedule
        scheduleAlarms();
    }

    void scheduleAlarms() {
        PendingIntent pendingIntent = scheduler.getBroadcast(REQUEST_ALARM, AlarmReceiver.class);
        scheduler.setAlarm(pendingIntent, TradingHours.getTimeMillis());
    }

    void onMarketWatching() {
        List<Order> orderList = db.fetchAll();

        List<String> watchList = new ArrayList<>();
        orderList.stream().filter(ListUtils.distinctByKey(Order::getStockNo)).forEach(s -> watchList.add(s.getStockNo()));
        Broker.fetchStockRealTimes(context, watchList, new Broker.OnResponseListener<List<StockRealTime>>() {
            @Override
            public void onResponse(List<StockRealTime> stockRealTimes) {
                for (Order order : orderList) {
                    StockRealTime stockRealTime = stockRealTimes.stream().filter(s -> s.stockNo.equals(order.getStockNo())).findFirst().orElse(null);
                    if (stockRealTime == null) continue;

                    notifyIfMatching(order, stockRealTime.getPrice());
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    void notifyIfMatching(Order order, long marketPrice) {
        int type = order.getType();
        long price = (long) (order.getTarget() * 1000);
        boolean matching = (type == Order.TYPE_LONG && price <= marketPrice) || (type == Order.TYPE_SHORT && price >= marketPrice);

        if (!matching) {
            if (order.getStatus() == Order.STATUS_MATCHING_ORDER) {
                order.setStatus(Order.STATUS_PENDING_ORDER);
                db.update(order);
            }

            return;
        }

        if (order.getStatus() == Order.STATUS_MATCHING_ORDER) return;


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss", Locale.getDefault());

        String content = order.getCode() + " " + order.getMessage() + " in " + sdf.format(cal.getTime());

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingActivity = PendingIntent.getActivity(context, REQUEST_ACTIVITY, intent, flags);
        NotifyMe.post(context, CHANNEL_ID, R.drawable.ic_candlestick_chart, "MarketWatcher", content, pendingActivity);

        order.setStatus(Order.STATUS_MATCHING_ORDER);
        db.update(order);
    }


}
