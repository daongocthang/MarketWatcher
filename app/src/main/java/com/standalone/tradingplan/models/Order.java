package com.standalone.tradingplan.models;

import java.io.Serializable;

public class Order implements Serializable, Comparable<Order> {

    @Override
    public int compareTo(Order other) {
        try {
            return other.getDate().compareTo(this.getDate());
        } catch (RuntimeException e) {
            return this.getSymbol().compareTo(other.getSymbol());
        }
    }

    public enum Type {BUY, SELL}

    int id;
    String symbol;
    double price;
    int shares;
    String message;
    Type type;
    String date;


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

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


}
