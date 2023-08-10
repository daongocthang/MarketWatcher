package com.standalone.marketwatcher.activities;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.droid.adapters.RecyclerItemTouchHelper;
import com.standalone.droid.services.AlarmScheduler;
import com.standalone.droid.ui.DraggableFloatingActionButton;
import com.standalone.droid.utils.Alerts;
import com.standalone.droid.utils.ListUtils;
import com.standalone.droid.utils.NotifyMe;
import com.standalone.marketwatcher.R;
import com.standalone.marketwatcher.adapters.OrderAdapter;
import com.standalone.marketwatcher.database.OrderDb;
import com.standalone.marketwatcher.database.StockDb;
import com.standalone.marketwatcher.models.Order;
import com.standalone.marketwatcher.models.StockInfo;
import com.standalone.marketwatcher.models.StockRealTime;
import com.standalone.marketwatcher.receivers.AlarmReceiver;
import com.standalone.marketwatcher.requests.Broker;
import com.standalone.marketwatcher.utils.AppUtils;
import com.standalone.marketwatcher.utils.TradingHours;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    OrderDb orderdb;
    OrderAdapter adapter;

    AlertDialog progressDialog;

    AlarmScheduler alarmScheduler;
    boolean alarmRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DraggableFloatingActionButton fab = findViewById(R.id.bt_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditorActivity(null);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotifyMe.createChannel(this, AlarmReceiver.CHANNEL_ID, "Market Watcher Notification");
        }

        alarmScheduler = AlarmScheduler.from(this);

        httpRequest();

        orderdb = new OrderDb(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new OrderAdapter(MainActivity.this, orderdb);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        asyncStockRealTimes();

        if (adapter.getItemCount() > 0) {
            alarmScheduler.setAlarm(AlarmReceiver.REQUEST_ALARM, AlarmReceiver.class, TradingHours.getTimeMillis());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.loadItemList();
        asyncStockRealTimes();
    }


    private void openEditorActivity(Order order) {
        Intent intent = new Intent(this, EditorActivity.class);
        if (order != null) {
            intent.putExtra("Order", order);
        }

        startActivity(intent);
    }

    private void httpRequest() {
        StockDb stockDb = new StockDb(this);
        if (stockDb.getCount() > 0 || !AppUtils.isNetworkAvailable(this)) return;

        progressDialog = Alerts.createProgressBar(this, com.standalone.droid.R.layout.simple_progress_dialog);
        progressDialog.show();
        Broker.fetchStockInfo(this, new Broker.OnResponseListener<List<StockInfo>>() {
            @Override
            public void onResponse(List<StockInfo> stockInfoList) {
                for (StockInfo info : stockInfoList) {
                    if (info.type.equals("s"))
                        stockDb.insert(info);
                }

                progressDialog.dismiss();
            }

            @Override
            public void onError() {
                progressDialog.dismiss();
            }
        });
    }

    private void asyncStockRealTimes() {
        if (!AppUtils.isNetworkAvailable(this)) return;

        List<String> distinctList = new ArrayList<>();
        orderdb.fetchAll().stream().filter(ListUtils.distinctByKey(Order::getStockNo)).forEach(s -> distinctList.add(s.getStockNo()));


        Broker.fetchStockRealTimes(this, distinctList, new Broker.OnResponseListener<List<StockRealTime>>() {
            @Override
            public void onResponse(List<StockRealTime> stockRealTimes) {
                adapter.setStockRealTimes(stockRealTimes);
            }

            @Override
            public void onError() {

            }
        });
    }


    public class ItemTouchCallback extends RecyclerItemTouchHelper {

        public ItemTouchCallback() {
            super(MainActivity.this);
        }

        @Override
        public void onSwipeLeft(int position) {
            openEditorActivity(adapter.getItem(position));
        }

        @Override
        public void onSwipeRight(int position) {
            Alerts.showYesNoDialog(adapter.getContext(), com.standalone.droid.R.style.AlertDialogTheme, "Are you sure you want to delete?", new Alerts.OnClickListener() {
                @Override
                public void onPositive(DialogInterface dialog, int which) {
                    adapter.removeItem(position);
                    if (adapter.getItemCount() == 0) {
                        alarmScheduler.cancelAlarm(AlarmReceiver.REQUEST_ALARM, AlarmReceiver.class);
                    }
                }

                @Override
                public void onNegative(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(position);
                }
            });
        }
    }


}