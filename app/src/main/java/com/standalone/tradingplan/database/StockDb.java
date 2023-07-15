package com.standalone.tradingplan.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.standalone.droid.dbase.SqliteBase;
import com.standalone.droid.dbase.SqliteTableHandler;
import com.standalone.tradingplan.models.Order;
import com.standalone.tradingplan.models.StockInfo;


public class StockDb extends SqliteTableHandler<StockInfo> {

    private final SqliteBase<StockInfo> sqliteBase;

    public StockDb(SQLiteDatabase db) {
        super(db);
        sqliteBase = new SqliteBase<>(StockInfo.class);
        metaData = sqliteBase.createMetaData("tbl_stocks");
        init();
    }

    @Override
    public StockInfo from(Cursor cursor) {
        try {
            return sqliteBase.from(cursor);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ContentValues convert(StockInfo stockInfo) {
        try {
            return sqliteBase.toContentValues(stockInfo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(StockInfo info) {
        db.insert(metaData.getName(), null, convert(info));
    }
}
