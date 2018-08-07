package com.example.collect.collectmoneysystem.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/2 0002.
 */

public class OrderData {

    /**
     * _id : 5b6270229134ca277c709112
     * clothes_ids : ["5b5a686f9134ca295e263562"]
     * shop_id : 5b5eafbe9134ca75e6534f02
     * status : 1
     * pay_type : wx_quick_pay
     * total_fee : 1000000
     * trade_no : 1533177890_58zTrLkdTSu0g1C
     * updated_at : 2018-08-02 10:44:50
     * created_at : 2018-08-02 10:44:50
     * status_name : 待支付
     */

    private String _id;
    private String shop_id;
    private int status;
    private String pay_type;
    private float total_fee;
    private String trade_no;
    private String updated_at;
    private String created_at;
    private String status_name;
    private List<String> clothes_ids;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public float getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(float total_fee) {
        this.total_fee = total_fee;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public List<String> getClothes_ids() {
        return clothes_ids;
    }

    public void setClothes_ids(List<String> clothes_ids) {
        this.clothes_ids = clothes_ids;
    }
}
