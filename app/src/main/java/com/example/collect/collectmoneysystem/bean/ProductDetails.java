package com.example.collect.collectmoneysystem.bean;

/**
 * Created by Administrator on 2018/5/16 0016.
 */

public class ProductDetails {
    private String part;
    private int count;
    private float price;
    private float total;

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
