package com.example.collect.collectmoneysystem.contract;

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
        Observable<ProductDetails> getProductDetails(String num);

        Observable<ProductDetails> getProductDetailsWithShop(String content);

//        Observable<OrderData> getProductOrder(MultipartBody.Part[] clothesId);
        Observable<OrderData> getProductOrder(String data);

        Observable<HttpResponse> getPayResultInfo(String orderId, String code);

        Observable<InventoryData> getInventoryAmounts(String shop);
    }

    interface View extends BaseView{
        void returnGetProductDetails(ProductDetails productDetails);

        void returnGetProductDetailsWithShop(ProductDetails productDetails);

//        void returnGetProductOrder(OrderData orderData);
        void returnGetProductOrder(OrderData orderData);

        void returnGetPayResultInfo(HttpResponse httpResponse);

        void returnGetInventoryAmounts(InventoryData httpResponse);
    }

    abstract class Presenter extends BasePresenter<View, Model> {
        public abstract void getProductDetailsRequest(String num);

        public abstract void getProductDetailsWithShopRequest(String content);

//        public abstract void getProductOrderRequest(MultipartBody.Part[] clothesId);
        public abstract void getProductOrderRequest(String data);

        public abstract void getPayResultInfoRequest(String orderId, String code);

        public abstract void getInventoryAmountsRequest(String shop);
    }
}
