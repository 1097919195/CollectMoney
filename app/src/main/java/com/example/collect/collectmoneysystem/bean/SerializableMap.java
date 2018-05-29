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

//    public static class List<ProductDetails> implements Serializable{
//        private List<ProductDetails> child;
//
//        public List<ProductDetails> getChild() {
//            return child;
//        }
//
//        public void setChild(List<ProductDetails> child) {
//            this.child = child;
//        }
//    }
}
