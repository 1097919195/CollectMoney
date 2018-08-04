package com.example.collect.collectmoneysystem.model;

import com.example.collect.collectmoneysystem.api.Api;
import com.example.collect.collectmoneysystem.api.HostType;
import com.example.collect.collectmoneysystem.bean.HttpResponse;
import com.example.collect.collectmoneysystem.bean.OrderData;
import com.example.collect.collectmoneysystem.bean.PayOrderWithMultipartBean;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.example.collect.collectmoneysystem.contract.MainContract;
import com.google.gson.Gson;
import com.jaydenxiao.common.baserx.RxSchedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class MainModel implements MainContract.Model{
    @Override
    public Observable<ProductDetails> getProductDetails(String num) {
        return Api.getDefault(HostType.QUALITY_DATA_NEW)
                .getProductDetails(num)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<ProductDetails> getProductDetailsWithShop(String content) {
        return Api.getDefault(HostType.QUALITY_DATA_NEW)
                .getProductDetailsWithShop(content)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<OrderData> getProductOrder(String data) {
        return Api.getDefault(HostType.QUALITY_DATA_NEW)
                .productOrder(data)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

//    @Override
//    public Observable<OrderData> getProductOrder(MultipartBody.Part[] clothesId) {
//        return Api.getDefault(HostType.QUALITY_DATA_NEW)
//                .productOrder(clothesId)
//                .map(new Api.HttpResponseFunc<>())
//                .compose(RxSchedulers.io_main());
//    }

    @Override
    public Observable<HttpResponse> getPayResultInfo(String orderId, String code) {
        return Api.getDefault(HostType.QUALITY_DATA_NEW)
                .getPayResult(orderId, code)
                .compose(RxSchedulers.io_main());
    }
}
