package com.example.collect.collectmoneysystem.presenter;

import com.example.collect.collectmoneysystem.bean.HttpResponse;
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
    public void getProductDetailsRequest(String num) {
        mRxManage.add(mModel.getProductDetails(num).subscribeWith(new RxSubscriber2<ProductDetails>(mContext, true) {
            @Override
            protected void _onNext(ProductDetails productDetails) {
                mView.returnGetProductDetails(productDetails);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));

    }

    @Override
    public void getProductDetailsWithShopRequest(String content) {
        mRxManage.add(mModel.getProductDetailsWithShop(content).subscribeWith(new RxSubscriber2<ProductDetails>(mContext, true) {
            @Override
            protected void _onNext(ProductDetails productDetails) {
                mView.returnGetProductDetailsWithShop(productDetails);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }

    @Override
    public void getProductOrderRequest(String data) {
        mRxManage.add(mModel.getProductOrder(data).subscribeWith(new RxSubscriber2<OrderData>(mContext, true) {
            @Override
            protected void _onNext(OrderData orderData) {
                mView.returnGetProductOrder(orderData);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }

//    @Override
//    public void getProductOrderRequest(MultipartBody.Part[] clothesId) {
//        mRxManage.add(mModel.getProductOrder(clothesId).subscribeWith(new RxSubscriber2<OrderData>(mContext, true) {
//            @Override
//            protected void _onNext(OrderData orderData) {
//                mView.returnGetProductOrder(orderData);
//            }
//
//            @Override
//            protected void _onError(String message) {
//                mView.showErrorTip(message);
//            }
//        }));
//    }

    @Override
    public void getPayResultInfoRequest(String orderId, String code) {
        mRxManage.add(mModel.getPayResultInfo(orderId, code).subscribeWith(new RxSubscriber2<HttpResponse>(mContext, false) {
            @Override
            protected void _onNext(HttpResponse httpResponse) {
                mView.returnGetPayResultInfo(httpResponse);
            }

            @Override
            protected void _onError(String message) {
                //防止请求没能及时在手机端支付，造成请求超时的提示
                if (message.equals(BaseApplication.getAppContext().getString(com.jaydenxiao.common.R.string.net_error))) {
                } else {
                    mView.showErrorTip(message);
                }
            }
        }));
    }
}
