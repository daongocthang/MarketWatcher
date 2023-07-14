package com.standalone.droid.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Alerts {
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
