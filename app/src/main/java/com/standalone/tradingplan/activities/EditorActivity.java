package com.standalone.tradingplan.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.droid.dbase.DatabaseManager;
import com.standalone.droid.utils.Humanize;
import com.standalone.droid.utils.ViewUtils;
import com.standalone.tradingplan.R;
import com.standalone.tradingplan.database.OrderDb;
import com.standalone.tradingplan.database.StockDb;
import com.standalone.tradingplan.models.Order;
import com.standalone.tradingplan.models.StockInfo;
import com.standalone.tradingplan.requests.Broker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    List<String> stockCodes;


    boolean isUpdate;

    int orderId;

    List<StockInfo> stockInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        selOrderType = findViewById(R.id.sel_order_type);
        edtSymbol = findViewById(R.id.ed_symbol);
        edtPrice = findViewById(R.id.ed_price);
        edtShares = findViewById(R.id.ed_shares);
        edtMessage = findViewById(R.id.ed_message);
        tvDate = findViewById(R.id.ed_date);

        Button btnSave = findViewById(R.id.btSave);
        ImageButton btnDatePicker = findViewById(R.id.bt_date_picker);


        stockInfoList = new StockDb(DatabaseManager.getDatabase(this)).fetchAll();
        stockCodes = new ArrayList<>();
        stockInfoList.forEach(s -> stockCodes.add(s.code));

        edtSymbol.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stockCodes));


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
            selOrderType.setSelection(extra.getType().ordinal());
            edtMessage.setText(extra.getMessage());
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
                if (stockCodes.contains(edtSymbol.getText().toString())) {
                    onSave();
                } else {
                    edtSymbol.setError("No code found matching the query", null);
                }
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

        StockInfo stockInfo = stockInfoList.stream().filter(s -> s.code.equals(inputSymbol)).findFirst().orElse(null);

        Order order = new Order();
        if (stockInfo != null) order.setStockNo(stockInfo.stockNo);
        order.setSymbol(inputSymbol);
        order.setPrice(inputPrice);
        order.setShares(inputShares);
        order.setDate(tvDate.getText().toString());
        order.setType(Order.Type.valueOf(selOrderType.getSelectedItem().toString()));
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
