package com.standalone.tradingplan.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.standalone.droid.dbase.SqLiteMaker;
import com.standalone.droid.dbase.SqLiteHandler;
import com.standalone.tradingplan.models.StockInfo;


public class StockDb extends SqLiteHandler<StockInfo> {

    private final SqLiteMaker<StockInfo> maker;

    public StockDb(SQLiteDatabase db) {
        super(db);
        maker = new SqLiteMaker<>(StockInfo.class);
        metaData = maker.createMetaData("tbl_stocks");
        init();
    }

    @Override
    public StockInfo createFromResult(Cursor cursor) {
        try {
            return maker.createDataClass(cursor);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ContentValues parseContentValues(StockInfo stockInfo) {
        try {
            return maker.createContentValues(stockInfo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public void insert(StockInfo info) {
        db.insert(metaData.getName(), null, parseContentValues(info));
    }
}
