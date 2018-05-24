package com.example.collect.collectmoneysystem.presenter;

import com.example.collect.collectmoneysystem.bean.HttpResponse;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.example.collect.collectmoneysystem.contract.MainContract;
import com.jaydenxiao.common.baserx.RxSubscriber;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class MainPresenter extends MainContract.Presenter{
    @Override
    public void getProductDetailsRequest(String num) {
        mRxManage.add(mModel.getProductDetails(num).subscribeWith(new RxSubscriber<ProductDetails>(mContext, true) {
            @Override
            protected void _onNext(ProductDetails productDetails) {
                mView.returnGetProductDetails(productDetails);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip("该卡号无对应成衣");
            }
        }));

    }
}
