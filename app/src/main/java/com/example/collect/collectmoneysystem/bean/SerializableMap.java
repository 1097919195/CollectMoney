package com.example.collect.collectmoneysystem.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

public class SerializableMap implements Serializable {
    private List<List<ProductDetails>> map;
    public List<List<ProductDetails>> getMap()
    {
        return map;
    }
    public void setMap(List<List<ProductDetails>> map)
    {
        this.map=map;
    }

}
