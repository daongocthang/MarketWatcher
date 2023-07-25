package com.standalone.droid.dbase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class SqLiteHandler<T> {
    protected MetaData metaData;
    protected final SQLiteDatabase db;

    public SqLiteHandler(SQLiteDatabase db, MetaData metaData) {
        this.metaData = metaData;
        this.db = db;
    }

    public SqLiteHandler(SQLiteDatabase db) {
        this.db = db;
    }

    public void init() {
        db.execSQL(this.metaData.getCreateTableStmt());
    }


    public abstract T fromResult(Cursor cursor);

    public abstract ContentValues createContentValues(T t);

    public int getCount() {
        return (int) DatabaseUtils.queryNumEntries(db, metaData.getName());
    }

    public List<T> fetchAll() {
        List<T> res = new ArrayList<>();
        Cursor curs = null;
        db.beginTransaction();
        try {
            curs = db.query(metaData.getName(), null, null, null, null, null, null);
            if (curs != null) {
                if (curs.moveToFirst()) {
                    do {
                        res.add(fromResult(curs));
                    } while (curs.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            assert curs != null;
            curs.close();
        }
        return res;
    }

    public static class MetaData {
        private final String tableName;
        private final String[] colDefinitions;

        public MetaData(String tableName, String[] colDefinitions) {
            this.tableName = tableName;
            this.colDefinitions = colDefinitions;
        }

        private String getCreateTableStmt() {
            return "CREATE TABLE IF NOT EXISTS " + tableName + "(" + String.join(", ", colDefinitions) + ");";
        }

        public String getName() {
            return tableName;
        }
    }
}
