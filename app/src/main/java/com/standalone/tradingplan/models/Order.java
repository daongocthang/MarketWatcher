package com.standalone.tradingplan.models;

import com.standalone.droid.dbase.Column;

import java.io.Serializable;

public class Order implements Serializable, Comparable<Order> {
    public static int SELL = 0;
    public static int BUY = 1;

    @Column(primary = true)
    int id;
    @Column
    String symbol;
    @Column
    double price;
    @Column
    long shares;
    @Column
    String message;
    @Column
    int type;
    @Column
    String date;
    @Column
    String stockNo;

    @Override
    public int compareTo(Order other) {
        try {
            return other.getDate().compareTo(this.getDate());
        } catch (RuntimeException e) {
            return this.getSymbol().compareTo(other.getSymbol());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getShares() {
        return shares;
    }

    public void setShares(long shares) {
        this.shares = shares;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStockNo() {
        return stockNo;
    }

    public void setStockNo(String stockNo) {
        this.stockNo = stockNo;
    }

}
