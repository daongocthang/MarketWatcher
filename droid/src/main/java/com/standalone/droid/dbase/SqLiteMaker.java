package com.standalone.droid.dbase;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class SqLiteMaker<T> {
    private final Class<T> className;


    public SqLiteMaker(Class<T> className) {
        this.className = className;
    }

    public SqLiteHandler.MetaData createMetaData(String tableName) {
        List<String> cols = new ArrayList<>();

        for (Field field : className.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) continue;
            StringBuilder builder = new StringBuilder();
            builder.append(field.getName()).append(" ");
            builder.append((field.getType().isAssignableFrom(String.class) ? "TEXT" : "INTEGER"));
            if (column.primary()) {
                builder.append(" PRIMARY KEY AUTOINCREMENT");
            }

            cols.add(builder.toString());
        }

        return new SqLiteHandler.MetaData(tableName, cols.toArray(new String[0]));
    }

    public ContentValues createContentValues(T t) throws IllegalAccessException {
        ContentValues cv = new ContentValues();
        for (Field field : className.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null || column.primary()) continue;
            field.setAccessible(true);
            Log.e(getClass().getSimpleName(), field.getName());
            Object value = field.get(t);
            if (value != null) {
                Log.e(getClass().getSimpleName(), value.toString());
                cv.put(field.getName(), value.toString());
            }
        }

        return cv;
    }

    public T createDataClass(Cursor cursor) throws IllegalAccessException, InstantiationException {
        T t = className.newInstance();
        for (Field field : className.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) continue;
            field.setAccessible(true);

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
