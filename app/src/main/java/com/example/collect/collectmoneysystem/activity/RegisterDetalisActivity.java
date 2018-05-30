package com.example.collect.collectmoneysystem.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;
import com.example.collect.collectmoneysystem.R;
import com.example.collect.collectmoneysystem.adapter.MyExpandableListViewAdapter;
import com.example.collect.collectmoneysystem.app.AppConstant;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.example.collect.collectmoneysystem.bean.SerializableMap;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baserx.RxBus2;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2018/5/28 0028.
 */

public class RegisterDetalisActivity extends BaseActivity{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rc_details)
    ExpandableListView expandableListView;
    List<List<ProductDetails>> registerDetailsList = new ArrayList<>();
    MyExpandableListViewAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.act_register_details;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//底部导航栏覆盖activity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });

        initRxBus();
        initAdapter();
    }

    private void initRxBus() {
//        mRxManager.on(AppConstant.SEND_REGISTER_DETAILS, new Consumer<List<List<ProductDetails>>>() {
//            @Override
//            public void accept(List<List<ProductDetails>> lists) throws Exception {
//                registerDetailsList = lists;
//            }
//        });
    }

    private void initAdapter() {
        Bundle bundle = getIntent().getExtras();
        SerializableMap serializableMap = (SerializableMap) bundle.get(AppConstant.SEND_REGISTER_DETAILS);
        List<String> groupItems = getIntent().getStringArrayListExtra(AppConstant.SEND_REGISTER_DETAILS_WITH_GROUP);
        registerDetailsList = serializableMap.getMap();
        LogUtils.loge(String.valueOf(registerDetailsList.size()));
        LogUtils.loge(String.valueOf(registerDetailsList.get(0).size()));
        adapter = new MyExpandableListViewAdapter(this,groupItems,registerDetailsList);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.showShort(String.valueOf(position));
                new MaterialDialog.Builder(RegisterDetalisActivity.this)
                        .title("去结算？")
                        .onPositive((d, i) -> {
                            RxBus2.getInstance().post(AppConstant.REGISTER_RETURN,position);
                            finish();
                        })
                        .positiveText("确定")
                        .negativeColor(getResources().getColor(R.color.red))
                        .negativeText("点错了")
                        .show();
                return true;
            }
        });
    }

}
