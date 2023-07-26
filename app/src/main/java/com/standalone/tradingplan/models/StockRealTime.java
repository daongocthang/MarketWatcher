package com.standalone.tradingplan.models;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.standalone.tradingplan.R;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class StockRealTime {
    @SerializedName("stockNo")
    public String stockNo;

    @SerializedName("stockSymbol")
    public String stockSymbol;

    @SerializedName("ceiling")
    public long ceiling;

    @SerializedName("floor")
    public long floor;

    @SerializedName("refPrice")
    public long refPrice;

    @SerializedName("matchedPrice")
    public long matchedPrice;

    @SerializedName("lastMatchedPrice")
    public long lastMatchedPrice;

    @SerializedName("matchedVolume")
    public long matchedVolume;

    @SerializedName("highest")
    public long highest;

    @SerializedName("lowest")
    public long lowest;


    public int getColor(Context context) {
        int resId;
        if (getPrice() >= ceiling) {
            resId = R.color.text_cell;
        } else if (getPrice() <= floor) {
            resId = R.color.text_floor;
        } else if (getPrice() > refPrice) {
            resId = R.color.text_up;
        } else if (getPrice() < refPrice) {
            resId = R.color.text_down;
        } else {
            resId = R.color.text_ref;
        }

        return context.getColor(resId);
    }

    public long getPrice() {
        return (matchedPrice > 0 ? matchedPrice : refPrice);
    }

    @NonNull
    public String toString() {
        StringJoiner joiner = new StringJoiner("; ");
        for (Field field : this.getClass().getFields()) {
            try {
                Object value = field.get(this);
                assert value != null;
                joiner.add(field.getName() + ": " + value.toString());

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return joiner.toString();
    }
}
