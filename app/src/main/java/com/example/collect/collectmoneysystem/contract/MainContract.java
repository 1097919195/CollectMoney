package com.example.collect.collectmoneysystem.contract;

import com.example.collect.collectmoneysystem.bean.HttpResponse;
import com.example.collect.collectmoneysystem.bean.OrderData;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
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

        Observable<ProductDetails> getProductDetailsWithShop(String content);

        Observable<OrderData> getProductOrder(String clothesId);

        Observable<HttpResponse> getPayResultInfo(String orderId, String code);
    }

    interface View extends BaseView{
        void returnGetProductDetails(ProductDetails productDetails);

        void returnGetProductDetailsWithShop(ProductDetails productDetails);

        void returnGetProductOrder(OrderData orderData);

        void returnGetPayResultInfo(HttpResponse httpResponse);
    }

    abstract class Presenter extends BasePresenter<View, Model> {
        public abstract void getProductDetailsRequest(String num);

        public abstract void getProductDetailsWithShopRequest(String content);

        public abstract void getProductOrderRequest(String clothesId);

        public abstract void getPayResultInfoRequest(String orderId, String code);
    }
}
