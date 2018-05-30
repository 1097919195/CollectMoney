package com.example.collect.collectmoneysystem.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/30 0030.
 */

public class SerializableGroup implements Serializable {
    private ArrayList<String> groupItems;

    public ArrayList<String> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(ArrayList<String> groupItems) {
        this.groupItems = groupItems;
    }
}
