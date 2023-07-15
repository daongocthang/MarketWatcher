package com.standalone.droid.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.DialogCompat;

import com.standalone.droid.R;

public class Alerts {
    public static AlertDialog createProgressBar(Context context, @LayoutRes int resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ProgressDialogTheme);
        View v = LayoutInflater.from(context).inflate(resId, null);
        return builder.setView(v).setCancelable(false).create();
    }


    public static void showYesNoDialog(Context context, int style, String message, OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, style);
        builder.setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onPositive(dialog, which);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onNegative(dialog, which);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        listener.onNegative(dialog, DialogInterface.BUTTON_NEGATIVE);
                    }
                }).show();
    }

    public interface OnClickListener {
        void onPositive(DialogInterface dialog, int which);

        void onNegative(DialogInterface dialog, int which);
    }
}
