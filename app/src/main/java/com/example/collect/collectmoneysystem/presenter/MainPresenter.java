package com.example.collect.collectmoneysystem.presenter;

import com.example.collect.collectmoneysystem.bean.CheckStoreData;
import com.example.collect.collectmoneysystem.bean.HttpResponse;
import com.example.collect.collectmoneysystem.bean.InventoryData;
import com.example.collect.collectmoneysystem.bean.OrderData;
import com.example.collect.collectmoneysystem.bean.PayOrderWithMultipartBean;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.example.collect.collectmoneysystem.contract.MainContract;
import com.jaydenxiao.common.baseapp.BaseApplication;
import com.jaydenxiao.common.baserx.RxSubscriber2;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class MainPresenter extends MainContract.Presenter{
    @Override
    public void getInventoryRequest(String num) {
        mRxManage.add(mModel.getInventory(num).subscribeWith(new RxSubscriber2<CheckStoreData>(mContext, true) {
            @Override
            protected void _onNext(CheckStoreData checkStoreData) {
                mView.returnGetInventory(checkStoreData,num);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));

    }
}
