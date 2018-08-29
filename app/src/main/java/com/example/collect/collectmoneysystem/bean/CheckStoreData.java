package com.example.collect.collectmoneysystem.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/8/29 0029.
 */

public class CheckStoreData {
    /**
     * actual_count : 4
     * clothes_card_count : 2
     * other_card_count : 1
     * diff : {"1":"3148273226","2":"518691438","3":"2040022106"}
     * more : {"1":"3123356586"}
     * shop_id : 5b7270389134ca4283472812
     * updated_at : 2018-08-29 16:16:23
     * created_at : 2018-08-29 16:16:23
     * _id : 5b8656579134ca3108010d76
     */

    private int actual_count;//总共数量
    private int clothes_card_count;//有效卡的数量
    private int other_card_count;//无效卡的数量
    private List<String> diff;//还需要盘点的卡
    private List<String> more;//不在盘点中的卡
    private String shop_id;
    private String _id;

    public int getActual_count() {
        return actual_count;
    }

    public void setActual_count(int actual_count) {
        this.actual_count = actual_count;
    }

    public int getClothes_card_count() {
        return clothes_card_count;
    }

    public void setClothes_card_count(int clothes_card_count) {
        this.clothes_card_count = clothes_card_count;
    }

    public int getOther_card_count() {
        return other_card_count;
    }

    public void setOther_card_count(int other_card_count) {
        this.other_card_count = other_card_count;
    }

    public List<String> getDiff() {
        return diff;
    }

    public void setDiff(List<String> diff) {
        this.diff = diff;
    }

    public List<String> getMore() {
        return more;
    }

    public void setMore(List<String> more) {
        this.more = more;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

}
