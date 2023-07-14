package com.standalone.droid.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.List;

/**
 * Requirement:
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 **/
public class StorageUtils {
    private static String TAG = "EXTERNAL_STORAGE";
    public static File getDefaultStorage(Context context) {
        String appName = getApplicationName(context);
        File file = new File(Environment.getExternalStorageDirectory(), appName);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.e(TAG, "Cannot create a new folder");
            }
        }

        return file;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static File getRemovableStorage(Context context) {
        String appName = getApplicationName(context);
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();
        StorageVolume storageVolume = storageVolumeList.get(1);
        File file= new File(storageVolume.getDirectory(), appName);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.e(TAG, "Cannot create a new folder");
            }
        }

        return file;
    }

    public static boolean hasSDCard() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }
}
