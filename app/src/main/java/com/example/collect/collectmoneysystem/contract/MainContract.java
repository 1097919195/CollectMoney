package com.example.collect.collectmoneysystem.contract;

import com.example.collect.collectmoneysystem.bean.CheckStoreData;
import com.example.collect.collectmoneysystem.bean.HttpResponse;
import com.example.collect.collectmoneysystem.bean.InventoryData;
import com.example.collect.collectmoneysystem.bean.OrderData;
import com.example.collect.collectmoneysystem.bean.PayOrderWithMultipartBean;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public interface MainContract {
    interface Model extends BaseModel {
        Observable<CheckStoreData> getInventory(String num);
    }

    interface View extends BaseView{
        void returnGetInventory(CheckStoreData checkStoreData, String num);

    }

    abstract class Presenter extends BasePresenter<View, Model> {
        public abstract void getInventoryRequest(String num);
    }
}
