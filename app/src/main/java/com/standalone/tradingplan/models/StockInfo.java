package com.standalone.tradingplan.models;


import com.google.gson.annotations.SerializedName;

public class StockInfo {
    int id;
    @SerializedName("type")
    public String type = "";
    @SerializedName("code")
    public String code = "";
    @SerializedName("description")
    public String description = "";
    @SerializedName("exchange")
    public String exchange = "";
    @SerializedName("stockNo")
    public String stockNo = "";
    @SerializedName("marketCapPrev")
    public long marketCapPrev = 0;
    @SerializedName("sharesOutstanding")
    public long sharesOutstanding = 0;
}
