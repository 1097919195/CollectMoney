package com.example.collect.collectmoneysystem.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/5/16 0016.
 */

public class ProductDetails implements Serializable {
    /**
     * _id : 5afcfc749134ca08662ec729
     * name : 1
     * num : 1
     * spec : null
     * ban_xing : null
     * type : 3
     * apparelInfo : []
     * partsInfo : []
     * retailPrice : 1
     * costPrice : 1
     * size : 11
     * color : 1
     * fabric : 1
     * style : 1
     * profile : 1
     * image : images/clothes/clothes_1526799015_0SQRq7amhi.jpg
     */

    //  https://ts.npclo.com/images/clothes/clothes_1526799015_0SQRq7amhi.jpg

    private String _id;
    private String name;//样衣名称
    private String num;//订单编号
    private String spec;//规格
    private String ban_xing;//版型
    private int type;//样衣类型
    private int retail_price;//零售价
    private String size;//码号
    private String color;//颜色
    private String fabric;//布料
    private String style;//款式
    private String profile;//廓形
    private List<ImagesBean> image;//图片
    private int clothesIdCounts;//记录当前重复的衣服数量(记得数量+1)

    public int getClothesIdCounts() {
        return clothesIdCounts;
    }

    public void setClothesIdCounts(int clothesIdCounts) {
        this.clothesIdCounts = clothesIdCounts;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getBan_xing() {
        return ban_xing;
    }

    public void setBan_xing(String ban_xing) {
        this.ban_xing = ban_xing;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRetailPrice() {
        return retail_price;
    }

    public void setRetailPrice(int retail_price) {
        this.retail_price = retail_price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<ImagesBean> getImage() {
        return image;
    }

    public void setImage(List<ImagesBean> image) {
        this.image = image;
    }

    public static class ImagesBean {
        /**
         * path : https://ts.npclo.com/images/goods/goods_1532932887_NvawgTHxqg.jpg
         * relative_path : /images/goods/goods_1532932887_NvawgTHxqg.jpg
         */

        private String path;
        private String relative_path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getRelative_path() {
            return relative_path;
        }

        public void setRelative_path(String relative_path) {
            this.relative_path = relative_path;
        }
    }

}
