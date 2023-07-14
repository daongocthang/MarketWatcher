package com.standalone.droid.dbase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Field;

public class BaseModel{
    public int id;

    public static ContentValues toContentValues(Object o) {
        ContentValues cv = new ContentValues();
        for (Field field : o.getClass().getFields()) {
            Object value = null;
            try {
                value = field.get(o);
                cv.put(field.getName(), value != null ? value.toString() : "");
            } catch (IllegalAccessException ignored) {
            }
        }

        return cv;
    }

    @SuppressLint("Range")
    public void fromDatabase(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex("id"));
    }

    public void declarative(){

    }
}
