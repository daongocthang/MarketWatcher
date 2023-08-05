package com.standalone.marketwatcher.database;

import android.content.Context;

import com.standalone.droid.dbase.SqLiteBase;
import com.standalone.marketwatcher.models.Order;

public class OrderDb extends SqLiteBase<Order> {

    public OrderDb(Context context) {
        super(context, "tbl_orders");
    }
    public void insert(Order order) {
        try {
            getDatabase().insert(getTableName(), null, parseContentValues(order));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Order order) {
        try {
            getDatabase().update(getTableName(), parseContentValues(order), "id = ?", new String[]{String.valueOf(order.getId())});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(int id) {
        getDatabase().delete(getTableName(), "id = ?", new String[]{String.valueOf(id)});
    }

}
