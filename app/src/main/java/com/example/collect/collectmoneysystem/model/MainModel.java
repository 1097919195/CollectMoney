package com.example.collect.collectmoneysystem.model;

import com.example.collect.collectmoneysystem.api.Api;
import com.example.collect.collectmoneysystem.api.HostType;
import com.example.collect.collectmoneysystem.bean.HttpResponse;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.example.collect.collectmoneysystem.bean.WeixinPayData;
import com.example.collect.collectmoneysystem.contract.MainContract;
import com.jaydenxiao.common.baserx.RxSchedulers;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class MainModel implements MainContract.Model{
    @Override
    public Observable<ProductDetails> getProductDetails(String num) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .getProductDetails(num)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<ProductDetails> getProductDetailsWithScan(String content) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .getProductDetailsWithScan(content)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<WeixinPayData> getPayResultInfo(String code) {
        return Api.getDefault(HostType.QUALITY_DATA_NEW)
                .getPayResult(code)
                .compose(RxSchedulers.io_main());
    }
}
