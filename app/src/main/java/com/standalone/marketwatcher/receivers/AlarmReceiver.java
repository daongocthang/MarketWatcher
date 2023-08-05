package com.standalone.marketwatcher.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.standalone.droid.services.AlarmScheduler;
import com.standalone.droid.utils.ListUtils;
import com.standalone.droid.utils.NotifyMe;
import com.standalone.marketwatcher.R;
import com.standalone.marketwatcher.database.OrderDb;
import com.standalone.marketwatcher.models.Order;
import com.standalone.marketwatcher.models.StockRealTime;
import com.standalone.marketwatcher.requests.Broker;
import com.standalone.marketwatcher.utils.NetworkUtils;
import com.standalone.marketwatcher.utils.TradingHours;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "MatchedMarketPrices";

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        if (TradingHours.marketOpening(calendar) && NetworkUtils.isNetworkAvailable(context)) {
            onMarketWatching(context);
        }

        // set Alarm Schedule
        setSchedule(context);
    }

    void setSchedule(Context context) {
        AlarmScheduler scheduler = AlarmScheduler.from(context);
        PendingIntent pendingIntent = scheduler.getBroadcast(0, AlarmReceiver.class);
        scheduler.setAlarm(pendingIntent, TradingHours.getTimeMillis());
    }

    void onMarketWatching(Context context) {
        List<Order> orderList = new OrderDb((context)).fetchAll();
        List<String> watchList = new ArrayList<>();
        orderList.stream().filter(ListUtils.distinctByKey(Order::getStockNo)).forEach(s -> watchList.add(s.getStockNo()));
        Broker.fetchStockRealTimes(context, watchList, new Broker.OnResponseListener<List<StockRealTime>>() {
            @Override
            public void onResponse(List<StockRealTime> stockRealTimes) {
                for (Order order : orderList) {
                    StockRealTime stockRealTime = stockRealTimes.stream().filter(s -> s.stockNo.equals(order.getStockNo())).findFirst().orElse(null);
                    if (stockRealTime == null) continue;

                    notifyIfMatched(context, order, stockRealTime.getPrice());
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    void notifyIfMatched(Context context, Order order, long marketPrice) {
        int type = order.getType();
        long price = (long) (order.getTarget() * 1000);
        boolean matched = (type == Order.LONG && price >= marketPrice) || (type == Order.SHORT && price <= marketPrice);

        if (!matched) return;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss", Locale.getDefault());

        String content = order.getCode() + " " + order.getMessage() + " in " + sdf.format(cal.getTime());
        NotifyMe.post(context, CHANNEL_ID, R.drawable.ic_calendar, "MarketWatcher", content, null);
    }


}
