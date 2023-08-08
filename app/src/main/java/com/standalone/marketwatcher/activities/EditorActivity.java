package com.standalone.marketwatcher.activities;

import android.os.Bundle;
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

import com.standalone.droid.utils.Alerts;
import com.standalone.droid.utils.Humanize;
import com.standalone.droid.utils.ViewUtils;
import com.standalone.marketwatcher.R;
import com.standalone.marketwatcher.database.OrderDb;
import com.standalone.marketwatcher.database.StockDb;
import com.standalone.marketwatcher.models.Order;
import com.standalone.marketwatcher.models.StockInfo;
import com.standalone.marketwatcher.models.StockRealTime;
import com.standalone.marketwatcher.requests.Broker;
import com.standalone.marketwatcher.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditorActivity extends AppCompatActivity {
    Spinner selOrderType;
    AutoCompleteTextView edtCode;
    EditText edtTarget;
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
        edtCode = findViewById(R.id.ed_code);
        edtTarget = findViewById(R.id.ed_target);
        edtMessage = findViewById(R.id.ed_message);
        tvDate = findViewById(R.id.ed_date);

        Button btnSave = findViewById(R.id.btSave);
        ImageButton btnDatePicker = findViewById(R.id.bt_date_picker);


        stockInfoList = new StockDb(this).fetchAll();
        stockMap = new HashMap<>();
        stockInfoList.forEach(s -> stockMap.put(s.code, s.stockNo));

        codeList = new ArrayList<>(stockMap.keySet());
        edtCode.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, codeList));
        edtCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hintPrice();
            }
        });

        View view = getWindow().getDecorView().getRootView();

        ViewUtils.addCancelButton(view, edtCode, R.id.bt_cancel_symbol);
        ViewUtils.addCancelButton(view, edtTarget, R.id.bt_cancel_price);
        ViewUtils.addCancelButton(view, edtMessage, R.id.bt_cancel_message);

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
            edtCode.setText(extra.getCode());
            tvDate.setText(extra.getDate());
            edtTarget.setText(Humanize.doubleComma(extra.getTarget()));
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
                if (!ViewUtils.validateRequiredField(edtCode) || !ViewUtils.validateRequiredField(edtTarget)) {
                    return;
                }

                //TODO: validate Symbol field
                if (stockMap.containsKey(edtCode.getText().toString().toUpperCase())) {
                    onSave();
                } else {
                    edtCode.setError("No code found matching the query", null);
                }
            }
        });
    }

    private void hintPrice() {
        String code = edtCode.getText().toString();
        if (!AppUtils.isNetworkAvailable(EditorActivity.this) || !stockMap.containsKey(code))
            return;

        progressDialog.show();
        Broker.fetchStockRealTimes(EditorActivity.this, Collections.singletonList(stockMap.get(code)), new Broker.OnResponseListener<List<StockRealTime>>() {
            @Override
            public void onResponse(List<StockRealTime> stockRealTimes) {
                stockRealTimes.stream().filter(s -> s.stockSymbol.equals(code))
                        .findFirst().ifPresent(stockRealTime -> edtTarget.setHint(String.format(Locale.US, "%,.2f", (double) stockRealTime.getPrice() / 1000)));

                progressDialog.dismiss();
            }

            @Override
            public void onError() {
                progressDialog.dismiss();
            }
        });
    }

    private void onSave() {
        String inputSymbol = edtCode.getText().toString().toUpperCase();

        double inputPrice;
        try {
            inputPrice = Double.parseDouble(edtTarget.getText().toString());
        } catch (NumberFormatException e) {
            inputPrice = 0.0;
        }


        String stockNo = stockMap.get(inputSymbol);

        Order order = new Order();
        order.setStockNo(stockNo);
        order.setId(orderId);
        order.setCode(inputSymbol);
        order.setTarget(inputPrice);
        order.setDate(tvDate.getText().toString());
        order.setType(selOrderType.getSelectedItemPosition());
        order.setMessage(edtMessage.getText().toString());

        OrderDb db = new OrderDb(this);
        if (isUpdate) {
            db.update(order);
        } else {
            db.insert(order);
        }

        Toast.makeText(this, "Order saved successfully.", Toast.LENGTH_SHORT).show();

        finish();
    }
}
