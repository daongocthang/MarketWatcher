package com.standalone.tradingplan.activities;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.droid.adapters.RecyclerItemTouchHelper;
import com.standalone.droid.services.Scheduler;
import com.standalone.droid.utils.Alerts;
import com.standalone.tradingplan.R;
import com.standalone.tradingplan.adapters.OrderAdapter;
import com.standalone.tradingplan.database.OrderDb;
import com.standalone.tradingplan.database.StockDb;
import com.standalone.tradingplan.models.Order;
import com.standalone.tradingplan.models.StockInfo;
import com.standalone.tradingplan.models.StockRealTime;
import com.standalone.tradingplan.receivers.MarketWatcher;
import com.standalone.tradingplan.requests.Broker;
import com.standalone.tradingplan.utils.NetworkUtils;
import com.standalone.tradingplan.utils.TradingHours;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity {
    public final String TAG = MainActivity.this.getClass().getSimpleName();
    static final int ALARM_REQUEST_CODE = 1;

    OrderDb orderdb;
    OrderAdapter adapter;

    AlertDialog progressDialog;

    Scheduler scheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scheduler = Scheduler.from(this);

        httpRequest();

        orderdb = new OrderDb(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new OrderAdapter(MainActivity.this, orderdb);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        asyncStockRealTimes();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.loadItemList();
        asyncStockRealTimes();

        PendingIntent pendingIntent = scheduler.getBroadcast(ALARM_REQUEST_CODE, MarketWatcher.class);

        if (adapter.getItemCount() > 0) {
            scheduler.setRepeatingAlarm(pendingIntent, System.currentTimeMillis(), TradingHours.getTimeMillis());
        } else {
            scheduler.cancelAlarm(pendingIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_add) {
            openEditorActivity(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (stockDb.getCount() > 0 || !NetworkUtils.isNetworkAvailable(this)) return;

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
        if (!NetworkUtils.isNetworkAvailable(this)) return;

        List<String> distinctList = new ArrayList<>();
        orderdb.fetchAll().stream().filter(distinctByKey(Order::getStockNo)).forEach(s -> distinctList.add(s.getStockNo()));


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

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
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
                }

                @Override
                public void onNegative(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(position);
                }
            });
        }
    }


}