package com.example.collect.collectmoneysystem.model;


import com.example.collect.collectmoneysystem.api.Api;
import com.example.collect.collectmoneysystem.api.HostType;
import com.example.collect.collectmoneysystem.bean.LoginTokenData;
import com.example.collect.collectmoneysystem.contract.AccountContract;
import com.jaydenxiao.common.baserx.RxSchedulers;


import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class AccountModel implements AccountContract.Model{
    @Override
    public Observable<LoginTokenData> getTokenSignIn(String userName, String passWord) {
        return Api.getDefault(HostType.QUALITY_DATA_NEW)
                .getTokenWithSignIn(userName, passWord)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }
}
