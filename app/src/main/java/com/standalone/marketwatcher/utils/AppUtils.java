package com.standalone.marketwatcher.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppUtils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isAvailable();
    }

}
