package com.example.collect.collectmoneysystem.presenter;


import com.example.collect.collectmoneysystem.bean.LoginTokenData;
import com.example.collect.collectmoneysystem.contract.AccountContract;
import com.jaydenxiao.common.baserx.RxSubscriber;



/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class AccountPresenter extends AccountContract.Presenter{
    @Override
    public void getTokenSignInRequest(String userName, String passWord) {
//        mRxManage.add(mModel.getTokenSignIn(userName,passWord)
//                .subscribe(
//                        httpResponse -> {mView.returnGetTokenSignIn(httpResponse);},
//                        e ->{mView.showErrorTip(e.getMessage());}
//                ));

        mRxManage.add(mModel.getTokenSignIn(userName,passWord).subscribeWith(new RxSubscriber<LoginTokenData>(mContext, true) {
            @Override
            protected void _onNext(LoginTokenData httpResponse) {
                mView.returnGetTokenSignIn(httpResponse);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }
}
