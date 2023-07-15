package com.standalone.tradingplan.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.standalone.droid.dbase.SqliteTableHandler;
import com.standalone.tradingplan.models.Order;

public class OrderDb extends SqliteTableHandler<Order> {
    static final String TBL_NAME = "tbl_orders";
    static final String COL_ID = "id";
    static final String COL_SYMBOL = "symbol";
    static final String COL_PRICE = "price";
    static final String COL_SHARES = "shares";
    static final String COL_MSG = "message";
    static final String COL_TYPE = "type";
    static final String COL_DATE = "date";
    static final String COL_STOCK_NO = "stockNo";

    public OrderDb(SQLiteDatabase db) {
        super(db, new SqliteTableHandler.MetaData(TBL_NAME,
                new String[]{
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT",
                        COL_SYMBOL + " TEXT",
                        COL_PRICE + " INTEGER",
                        COL_SHARES + " INTEGER",
                        COL_MSG + " TEXT",
                        COL_TYPE + " INTEGER",
                        COL_STOCK_NO + " TEXT",
                        COL_DATE + " TEXT"
                }));

        init();
    }

    @SuppressLint("Range")
    @Override
    public Order from(Cursor curs) {
        Order newOrder = new Order();
        newOrder.setId(curs.getInt(curs.getColumnIndex(COL_ID)));
        newOrder.setSymbol(curs.getString(curs.getColumnIndex(COL_SYMBOL)));
        newOrder.setPrice(curs.getDouble(curs.getColumnIndex(COL_PRICE)));
        newOrder.setShares(curs.getInt(curs.getColumnIndex(COL_SHARES)));
        newOrder.setMessage(curs.getString(curs.getColumnIndex(COL_MSG)));
        newOrder.setStockNo(curs.getString(curs.getColumnIndex(COL_STOCK_NO)));
        newOrder.setType(curs.getInt(curs.getColumnIndex(COL_TYPE)));
        newOrder.setDate(curs.getString(curs.getColumnIndex(COL_DATE)));

        return newOrder;
    }

    @Override
    public ContentValues convert(Order order) {
        ContentValues cv = new ContentValues();
        cv.put(COL_SYMBOL, order.getSymbol());
        cv.put(COL_PRICE, order.getPrice());
        cv.put(COL_SHARES, order.getShares());
        cv.put(COL_MSG, order.getMessage());
        cv.put(COL_STOCK_NO, order.getStockNo());
        cv.put(COL_TYPE, order.getType());
        cv.put(COL_DATE, order.getDate());

        return cv;
    }

    public void insert(Order order) {
        db.insert(TBL_NAME, null, convert(order));
    }

    public void update(Order order) {
        db.update(TBL_NAME, convert(order), COL_ID + " = ?", new String[]{String.valueOf(order.getId())});
    }

    public void remove(int id) {
        db.delete(TBL_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

}
