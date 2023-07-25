package com.standalone.tradingplan.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.standalone.droid.dbase.SqLiteBase;
import com.standalone.droid.dbase.SqLiteHandler;
import com.standalone.tradingplan.models.StockInfo;


public class StockDb extends SqLiteHandler<StockInfo> {

    private final SqLiteBase<StockInfo> base;

    public StockDb(SQLiteDatabase db) {
        super(db);
        base = new SqLiteBase<>(StockInfo.class);
        metaData = base.createMetaData("tbl_stocks");
        init();
    }

    @Override
    public StockInfo fromResult(Cursor cursor) {
        try {
            return base.fromResult(cursor);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ContentValues createContentValues(StockInfo stockInfo) {
        try {
            return base.createContentValues(stockInfo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(StockInfo info) {
        db.insert(metaData.getName(), null, createContentValues(info));
    }
}
