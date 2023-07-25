package com.standalone.droid.dbase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.standalone.droid.utils.StorageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Properties;

public class SqLiteManager {
    @SuppressLint("StaticFieldLeak")
    private static SqLiteOpen sqLiteOpenInstance;

    public static SQLiteDatabase getDatabase(Context context) {
        if (sqLiteOpenInstance == null) {
            try {
                sqLiteOpenInstance = new SqLiteOpen(context, getProperty(context, "database_name"), Integer.parseInt(getProperty(context, "database_version")));
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return sqLiteOpenInstance.getDatabase();
    }

    public static boolean importDB(Context context) {
        try {
            String dbName = getProperty(context, "database_name");
            File dir = getExtStorage(context);
            getDatabase(context).close();
            if (dir.canRead()) {
                File dst = context.getDatabasePath(dbName);
                File src = new File(dir, dbName);
                transfer(context, src, dst);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean exportDB(Context context) {
        try {
            String dbName = getProperty(context, "database_name");
            File dir = getExtStorage(context);
            getDatabase(context).close();
            if (dir.canWrite()) {
                File src = context.getDatabasePath(dbName);
                File dst = new File(dir, dbName);
                transfer(context, src, dst);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static File getExtStorage(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return StorageUtils.getRemovableStorage(context);
        }

        return StorageUtils.getDefaultStorage(context);
    }

    private static void transfer(Context context, File src, File dst) throws IOException {
        if (src.exists()) {
            FileChannel srcChannel = new FileInputStream(src).getChannel();
            FileChannel dstChannel = new FileOutputStream(dst).getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
            srcChannel.close();
            dstChannel.close();
        }
    }

    private static String getProperty(Context context, String key) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = context.getAssets().open("config.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }


}
