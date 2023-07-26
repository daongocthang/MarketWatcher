package com.standalone.tradingplan.database;

import android.content.Context;

import com.standalone.droid.dbase.SqLiteBase;
import com.standalone.tradingplan.models.StockInfo;


public class StockDb extends SqLiteBase<StockInfo> {

    public StockDb(Context context) {
        super(context, "tbl_stocks");
    }

    public void insert(StockInfo info) {
        try {
            getDatabase().insert(getTableName(), null, parseContentValues(info));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
