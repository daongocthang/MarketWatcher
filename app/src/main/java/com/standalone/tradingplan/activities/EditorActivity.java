package com.standalone.tradingplan.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.droid.dbase.DatabaseManager;
import com.standalone.droid.utils.Alerts;
import com.standalone.droid.utils.Humanize;
import com.standalone.droid.utils.ViewUtils;
import com.standalone.tradingplan.R;
import com.standalone.tradingplan.adapters.OrderAdapter;
import com.standalone.tradingplan.database.OrderDb;
import com.standalone.tradingplan.database.StockDb;
import com.standalone.tradingplan.models.Order;
import com.standalone.tradingplan.models.StockInfo;
import com.standalone.tradingplan.models.StockRealTime;
import com.standalone.tradingplan.requests.Broker;
import com.standalone.tradingplan.utils.Watches;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditorActivity extends AppCompatActivity {
    Spinner selOrderType;
    AutoCompleteTextView edtSymbol;
    EditText edtPrice;
    EditText edtShares;
    EditText edtMessage;
    TextView tvDate;
    Map<String, String> stockMap;
    AlertDialog progressDialog;
    boolean isUpdate;
    int orderId;

    List<String> codeList;
    List<StockInfo> stockInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        progressDialog = Alerts.createProgressBar(this, com.standalone.droid.R.layout.simple_progress_dialog);

        selOrderType = findViewById(R.id.sel_order_type);
        edtSymbol = findViewById(R.id.ed_symbol);
        edtPrice = findViewById(R.id.ed_price);
        edtShares = findViewById(R.id.ed_shares);
        edtMessage = findViewById(R.id.ed_message);
        tvDate = findViewById(R.id.ed_date);

        Button btnSave = findViewById(R.id.btSave);
        ImageButton btnDatePicker = findViewById(R.id.bt_date_picker);


        stockInfoList = new StockDb(DatabaseManager.getDatabase(this)).fetchAll();
        stockMap = new HashMap<>();
        stockInfoList.forEach(s -> stockMap.put(s.code, s.stockNo));

        codeList = new ArrayList<>(stockMap.keySet());
        edtSymbol.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, codeList));
        edtSymbol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hintPrice();
            }
        });

        View view = getWindow().getDecorView().getRootView();

        ViewUtils.addCancelButton(view, edtSymbol, R.id.bt_cancel_symbol);
        ViewUtils.addCancelButton(view, edtPrice, R.id.bt_cancel_price);
        ViewUtils.addCancelButton(view, edtShares, R.id.bt_cancel_shares);
        ViewUtils.addCancelButton(view, edtMessage, R.id.bt_cancel_message);

        final RecyclerView recyclerView = findViewById(R.id.rv_suggestion);
        recyclerView.setVisibility(View.VISIBLE);
        ViewUtils.setNumberSuggestion(this, edtShares, recyclerView, 1, 5, true);


        // Fill fields if exists
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText(today);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.order_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selOrderType.setAdapter(adapter);

        //  Fetch serializable
        Order extra = (Order) getIntent().getSerializableExtra("Order");
        if (extra != null) {
            isUpdate = true;

            orderId = extra.getId();
            edtSymbol.setText(extra.getSymbol());
            tvDate.setText(extra.getDate());
            edtPrice.setText(Humanize.doubleComma(extra.getPrice()));
            edtShares.setText(Humanize.intComma(extra.getShares()));
            selOrderType.setSelection(extra.getType());
            edtMessage.setText(extra.getMessage());

            hintPrice();
        }

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtils.showDatePicker(view, tvDate);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ViewUtils.validateRequiredField(edtSymbol) || !ViewUtils.validateRequiredField(edtPrice) || !ViewUtils.validateRequiredField(edtShares)) {
                    return;
                }

                //TODO: validate Symbol field
                if (stockMap.containsKey(edtSymbol.getText().toString().toUpperCase())) {
                    onSave();
                } else {
                    edtSymbol.setError("No code found matching the query", null);
                }
            }
        });
    }

    private void hintPrice() {
        String code = edtSymbol.getText().toString();
        if (!Watches.isNetworkAvailable(EditorActivity.this) || !stockMap.containsKey(code))
            return;

        progressDialog.show();
        Broker.fetchStockRealTimes(EditorActivity.this, Collections.singletonList(stockMap.get(code)), new Broker.OnResponseListener<List<StockRealTime>>() {
            @Override
            public void onResponse(List<StockRealTime> stockRealTimes) {
                stockRealTimes.stream().filter(s -> s.stockSymbol.equals(code))
                        .findFirst().ifPresent(stockRealTime -> edtPrice.setHint(String.format(Locale.US, "%,.2f", (double) stockRealTime.getPrice() / 1000)));

                progressDialog.dismiss();
            }

            @Override
            public void onError() {
                progressDialog.dismiss();
            }
        });
    }

    private void onSave() {
        String inputSymbol = edtSymbol.getText().toString().toUpperCase();

        double inputPrice;
        try {
            inputPrice = Double.parseDouble(edtPrice.getText().toString());
        } catch (NumberFormatException e) {
            inputPrice = 0.0;
        }

        int inputShares;
        try {
            inputShares = Integer.parseInt(edtShares.getText().toString().replace(",", ""));
        } catch (NumberFormatException e) {
            inputShares = 0;
        }

        String stockNo = stockMap.get(inputSymbol);

        Order order = new Order();
        order.setStockNo(stockNo);
        order.setId(orderId);
        order.setSymbol(inputSymbol);
        order.setPrice(inputPrice);
        order.setShares(inputShares);
        order.setDate(tvDate.getText().toString());
        order.setType(selOrderType.getSelectedItemPosition());
        order.setMessage(edtMessage.getText().toString());

        OrderDb handler = new OrderDb(DatabaseManager.getDatabase(this));
        if (isUpdate) {
            handler.update(order);
        } else {
            handler.insert(order);
        }

        Toast.makeText(this, "Order saved successfully.", Toast.LENGTH_SHORT).show();

        finish();
    }
}
