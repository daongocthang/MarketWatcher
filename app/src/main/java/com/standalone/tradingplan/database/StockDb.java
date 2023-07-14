package com.standalone.tradingplan.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.standalone.droid.dbase.SqliteTableHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class StockDb {
    static final String TBL_NAME = "tbl_stocks";
    static final String COL_ID = "id";
    static final String COL_CODE = "code";
    static final String COL_NAME = "name";
    static final String COL_EXCHANGE = "exchange";
    static final String COL_STOCK_NO = "stock_no";

    public StockDb(SQLiteDatabase db, Object o) {
        List<String> columns = new ArrayList<>();
        columns.add("id");
    }

    public Object cursorToData(Cursor cursor) {
        return null;
    }

    public ContentValues convertToContentValues(Object o) throws IllegalAccessException {
        ContentValues cv = new ContentValues();
        for (Field field : o.getClass().getFields()) {
            Object value = field.get(o);
            if (value != null) {
                cv.put(field.getName(), value.toString());
            }
        }
        return cv;
    }
}
