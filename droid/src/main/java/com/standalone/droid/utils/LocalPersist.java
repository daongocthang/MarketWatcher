package com.standalone.droid.utils;

import android.content.ContentValues;
import android.content.Context;

public class LocalPersist {
    Context context;

    public static LocalPersist from(Context context) {
        LocalPersist self = new LocalPersist();
        self.context = context;
        return self;
    }

    public void write() {

    }

    public void read() {

    }

    public void remove() {

    }
}
