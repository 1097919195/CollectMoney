package com.example.collect.collectmoneysystem.contract;

import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.example.collect.collectmoneysystem.bean.WeixinPayData;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public interface MainContract {
    interface Model extends BaseModel {
        Observable<ProductDetails> getProductDetails(String num);

        Observable<ProductDetails> getProductDetailsWithScan(String content);

        Observable<WeixinPayData> getPayResultInfo(String code);
    }

    interface View extends BaseView{
        void returnGetProductDetails(ProductDetails productDetails);

        void returnGetProductDetailsWithScan(ProductDetails productDetails);

        void returnGetPayResultInfo(WeixinPayData weixinPayData);
    }

    abstract class Presenter extends BasePresenter<View, Model> {
        public abstract void getProductDetailsRequest(String num);

        public abstract void getProductDetailsWithScanRequest(String content);

        public abstract void getPayResultInfoRequest(String code);
    }
}
