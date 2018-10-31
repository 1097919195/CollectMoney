package com.example.collect.collectmoneysystem.presenter;


import com.example.collect.collectmoneysystem.bean.LoginTokenData;
import com.example.collect.collectmoneysystem.contract.AccountContract;
import com.jaydenxiao.common.baserx.RxSubscriber;
import com.jaydenxiao.common.baserx.RxSubscriber2;


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

        mRxManage.add(mModel.getTokenSignIn(userName,passWord).subscribeWith(new RxSubscriber2<LoginTokenData>(mContext, true) {
            @Override
            protected void _onNext(LoginTokenData httpResponse) {
                mView.returnGetTokenSignIn(httpResponse);
            }

            @Override
            protected void _onError(String message) {
                if (message == "token过期") {
                    mView.showErrorTip("账号或者密码错误");
                }else {
                    mView.showErrorTip(message);
                }
            }
        }));
    }
}
