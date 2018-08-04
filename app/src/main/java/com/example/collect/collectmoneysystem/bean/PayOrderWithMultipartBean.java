package com.example.collect.collectmoneysystem.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/4 0004.
 */

public class PayOrderWithMultipartBean {
    private String id;
    private int count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public PayOrderWithMultipartBean(String clothesIds, Integer clothesIdCount) {
        this.id = clothesIds;
        this.count = clothesIdCount;
    }
}
