package com.standalone.tradingplan.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.droid.adapters.RecyclerItemTouchHelper;
import com.standalone.droid.dbase.DatabaseManager;
import com.standalone.droid.utils.Alerts;
import com.standalone.droid.utils.ViewUtils;
import com.standalone.tradingplan.R;
import com.standalone.tradingplan.adapters.OrderAdapter;
import com.standalone.tradingplan.database.OrderTableHandler;
import com.standalone.tradingplan.models.Order;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    OrderTableHandler dbHandler;
    OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new OrderTableHandler(DatabaseManager.getDatabase(this));

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new OrderAdapter(this, dbHandler);

        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.loadItemList();
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