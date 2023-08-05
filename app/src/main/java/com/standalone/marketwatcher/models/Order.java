package com.standalone.marketwatcher.models;

import com.standalone.droid.dbase.Column;

import java.io.Serializable;

public class Order implements Serializable, Comparable<Order> {
    public static int SHORT = 0;
    public static int LONG = 1;

    @Column(primary = true)
    int id;
    @Column
    String code;
    @Column
    double target;

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
            return this.getCode().compareTo(other.getCode());
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
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
