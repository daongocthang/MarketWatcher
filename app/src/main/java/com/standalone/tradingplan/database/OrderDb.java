package com.standalone.tradingplan.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.standalone.droid.dbase.SqLiteBase;
import com.standalone.droid.dbase.SqLiteHandler;
import com.standalone.tradingplan.models.Order;

public class OrderDb extends SqLiteHandler<Order> {

    private final SqLiteBase<Order> base;

    public OrderDb(SQLiteDatabase db) {
        super(db);
        base = new SqLiteBase<>(Order.class);
        metaData = base.createMetaData("tbl_orders");

        init();
    }

    @SuppressLint("Range")
    @Override
    public Order fromResult(Cursor curs) {
        try {
            return base.fromResult(curs);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ContentValues createContentValues(Order order) {
        try {
            return base.createContentValues(order);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Order order) {
        db.insert(metaData.getName(), null, createContentValues(order));
    }

    public void update(Order order) {
        db.update(metaData.getName(), createContentValues(order), "id = ?", new String[]{String.valueOf(order.getId())});
    }

    public void remove(int id) {
        db.delete(metaData.getName(), "id = ?", new String[]{String.valueOf(id)});
    }

}
