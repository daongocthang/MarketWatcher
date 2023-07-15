package com.standalone.droid.dbase;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class SqliteBase<T> {
    private final Class<T> className;

    public SqliteBase(Class<T> className) {
        this.className = className;
    }

    public SqliteTableHandler.MetaData createMetaData(String tableName) {
        List<String> cols = new ArrayList<>();
        cols.add("id INTEGER PRIMARY KEY AUTOINCREMENT");
        for (Field field : className.getFields()) {
            cols.add(field.getName() + " " + (field.getType().isAssignableFrom(String.class) ? "TEXT" : "INTEGER"));
        }

        return new SqliteTableHandler.MetaData(tableName, cols.toArray(new String[0]));
    }

    public ContentValues toContentValues(T t) throws IllegalAccessException {
        ContentValues cv = new ContentValues();
        for (Field field : className.getFields()) {
            Object value = field.get(t);
            if (value != null)
                cv.put(field.getName(), value.toString());
        }

        return cv;
    }

    public T from(Cursor cursor) throws IllegalAccessException, InstantiationException {
        T t = className.newInstance();
        for (Field field : className.getFields()) {
            int colIndex = cursor.getColumnIndex(field.getName());
            Object typeValue = null;
            Class<?> type = field.getType();
            if (type.isAssignableFrom(Integer.class)) {
                typeValue = cursor.getInt(colIndex);
            } else if (type.isAssignableFrom(Long.class)) {
                typeValue = cursor.getLong(colIndex);
            } else if (type.isAssignableFrom(Double.class)) {
                typeValue = cursor.getDouble(colIndex);
            } else if (type.isAssignableFrom(String.class)) {
                typeValue = cursor.getString(colIndex);
            }

            if (typeValue != null)
                field.set(t, typeValue);
        }

        return t;
    }
}
