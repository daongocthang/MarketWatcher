package com.standalone.tradingplan.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.standalone.droid.dbase.SqLiteMaker;
import com.standalone.droid.dbase.SqLiteHandler;
import com.standalone.tradingplan.models.Order;

public class OrderDb extends SqLiteHandler<Order> {

    private final SqLiteMaker<Order> maker;

    public OrderDb(SQLiteDatabase db) {
        super(db);
        maker = new SqLiteMaker<>(Order.class);
        metaData = maker.createMetaData("tbl_orders");

        init();
    }

    @SuppressLint("Range")
    @Override
    public Order createFromResult(Cursor curs) {
        try {
            return maker.createDataClass(curs);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ContentValues parseContentValues(Order order) {
        try {
            Log.e(getClass().getSimpleName(), order.getDate());
            return maker.createContentValues(order);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Order order) {
        db.insert(metaData.getName(), null, parseContentValues(order));
    }

    public void update(Order order) {
        db.update(metaData.getName(), parseContentValues(order), "id = ?", new String[]{String.valueOf(order.getId())});
    }

    public void remove(int id) {
        db.delete(metaData.getName(), "id = ?", new String[]{String.valueOf(id)});
    }

}
