package com.standalone.droid.dbase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class SqLiteBase<T> {

    final SQLiteDatabase db;
    final String tableName;
    final Class<T> classType;

    public SqLiteBase(Context context, String tableName) {
        this.db = SqLiteManager.getDatabase(context);
        this.tableName = tableName;
        classType = getClassType();

        createTableIfNotExists();
    }

    public List<T> fetchAll() {
        List<T> res = new ArrayList<>();
        Cursor curs = null;
        db.beginTransaction();
        try {
            curs = db.query(tableName, null, null, null, null, null, null);
            if (curs != null) {
                if (curs.moveToFirst()) {
                    do {
                        res.add(createFromResult(curs));
                    } while (curs.moveToNext());
                }
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        } finally {
            db.endTransaction();
            assert curs != null;
            curs.close();
        }
        return res;
    }

    public long getCount() {
        return DatabaseUtils.queryNumEntries(db, tableName);
    }

    public String getTableName() {
        return tableName;
    }

    public SQLiteDatabase getDatabase() {
        return db;
    }

    public ContentValues parseContentValues(T t) throws IllegalAccessException {
        ContentValues cv = new ContentValues();
        for (Field field : classType.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null || column.primary()) continue;
            field.setAccessible(true);
            Object value = null;
            value = field.get(t);
            if (value != null) {
                cv.put(field.getName(), value.toString());
            }
        }

        return cv;
    }

    public T createFromResult(Cursor cursor) throws IllegalAccessException, InstantiationException {
        T t = classType.newInstance();
        for (Field field : classType.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) continue;
            field.setAccessible(true);

            int colIndex = cursor.getColumnIndex(field.getName());
            Object typeValue = null;
            Class<?> type = field.getType();
            if (type.isAssignableFrom(int.class)) {
                typeValue = cursor.getInt(colIndex);
            } else if (type.isAssignableFrom(long.class)) {
                typeValue = cursor.getLong(colIndex);
            } else if (type.isAssignableFrom(float.class)) {
                typeValue = cursor.getFloat(colIndex);
            } else if (type.isAssignableFrom(double.class)) {
                typeValue = cursor.getDouble(colIndex);
            } else if (type.isAssignableFrom(String.class)) {
                typeValue = cursor.getString(colIndex);
            }

            if (typeValue != null)
                field.set(t, typeValue);
        }

        return t;
    }

    private void createTableIfNotExists() {
        List<String> cols = new ArrayList<>();
        for (Field field : classType.getDeclaredFields()) {
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

        String sql = String.format("CREATE TABLE IF NOT EXISTS %s(%s);", tableName, String.join(", ", cols));
        db.execSQL(sql);
    }


    @SuppressWarnings("unchecked")
    private Class<T> getClassType() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        assert type != null;
        return (Class<T>) type.getActualTypeArguments()[0];
    }
}
