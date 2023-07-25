package com.standalone.tradingplan.models;


import com.google.gson.annotations.SerializedName;
import com.standalone.droid.dbase.Column;

public class StockInfo {
    @Column(primary = true)
    public int id;

    @Column
    @SerializedName("type")
    public String type = "";

    @Column
    @SerializedName("code")
    public String code = "";

    @SerializedName("description")
    public String description = "";

    @SerializedName("exchange")
    public String exchange = "";

    @Column
    @SerializedName("stockNo")
    public String stockNo = "";

    @SerializedName("marketCapPrev")
    public long marketCapPrev = 0;

    @SerializedName("sharesOutstanding")
    public long sharesOutstanding = 0;
}
