package com.example.collect.collectmoneysystem.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.collect.collectmoneysystem.R;
import com.example.collect.collectmoneysystem.adapter.MyExpandableListViewAdapter;
import com.example.collect.collectmoneysystem.app.AppConstant;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.example.collect.collectmoneysystem.bean.SerializableGroup;
import com.example.collect.collectmoneysystem.bean.SerializableChild;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baserx.RxBus2;
import com.jaydenxiao.common.commonutils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/28 0028.
 */

public class RegisterDetalisActivity extends BaseActivity{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rc_details)
    ExpandableListView expandableListView;
    List<List<ProductDetails>> registerDetailsList = new ArrayList<>();
    List<String> groupItems = new ArrayList<>();
    Bundle bundle;
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

        initAdapter();
    }

    private void initAdapter() {
        bundle = getIntent().getExtras();
        SerializableChild serializableChild = (SerializableChild) bundle.get(AppConstant.SEND_REGISTER_DETAILS_WITH_CHILD);
        SerializableGroup serializableGroup = (SerializableGroup) bundle.get(AppConstant.SEND_REGISTER_DETAILS_WITH_GROUP);
        groupItems = serializableGroup.getGroupItems();
        registerDetailsList = serializableChild.getMap();

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
