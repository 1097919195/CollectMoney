package com.example.collect.collectmoneysystem.activity;

import android.app.Activity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;
import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener;
import com.aspsine.irecyclerview.widget.LoadMoreFooterView;
import com.example.collect.collectmoneysystem.R;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/16 0016.
 */

public class TestActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.testIrc)
    IRecyclerView testIrc;
    ProductDetails details1 = new ProductDetails();
    ProductDetails details2 = new ProductDetails();
    ProductDetails details3 = new ProductDetails();
    ProductDetails details4 = new ProductDetails();
    ProductDetails details5 = new ProductDetails();
    ProductDetails details6 = new ProductDetails();
    ProductDetails details7 = new ProductDetails();
    ProductDetails details8 = new ProductDetails();
    ProductDetails details9 = new ProductDetails();
    List<ProductDetails> productDetails = new ArrayList<>();
    CommonRecycleViewAdapter<ProductDetails> productAdapter;
    @Override
    public int getLayoutId() {
        return R.layout.act_test_refresh;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        initAdapter();
    }

    private void initAdapter() {
        details1.setPart("123asdf123dsf56asd1f23sd1f3asadffff");
        details2.setPart("123asdfasaa");
        details3.setPart("123asdfasaaasdfasdfsd");
        details4.setPart("123asdfasaa");
        details5.setPart("123asdfasaaasadf");
        details6.setPart("123asdfasaaasdfsdfsdfsdfasdfasdfasdfsdfsd");
        productDetails.add(details1);
        productDetails.add(details2);
        productDetails.add(details3);
        productDetails.add(details4);
        productDetails.add(details5);
        productDetails.add(details6);
        productDetails.add(details7);
        productDetails.add(details8);
        productDetails.add(details9);
        productAdapter = new CommonRecycleViewAdapter<ProductDetails>(mContext,R.layout.item_product_details,productDetails) {
            @Override
            public void convert(ViewHolderHelper helper, ProductDetails productDetails) {
                TextView part = helper.getView(R.id.part);
                TextView count = helper.getView(R.id.count);
                TextView price = helper.getView(R.id.price);
                TextView total = helper.getView(R.id.total);

                part.setText(productDetails.getPart());
                count.setText(String.valueOf(productDetails.getCount()));
                price.setText(String.valueOf(productDetails.getPrice()));
                total.setText(String.valueOf(productDetails.getTotal()));
            }
        };

        testIrc.setAdapter(productAdapter);
        testIrc.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        //设置分割线
        testIrc.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));//默认
//         DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
//         divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.custom_divider));
//         productDetailIrc.addItemDecoration(divider);

        testIrc.setOnLoadMoreListener(this);
        testIrc.setOnRefreshListener(this);

        productAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                ToastUtil.showShort(String.valueOf(position));
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });
    }

    @Override
    public void onRefresh() {
        testIrc.setRefreshing(true);
        stopRefreshOrLoadMore();
    }

    private void stopRefreshOrLoadMore() {
        testIrc.postDelayed(new Runnable() {
            @Override
            public void run() {
                testIrc.setRefreshing(false);
                testIrc.setLoadMoreStatus(LoadMoreFooterView.Status.GONE);
            }
        },1000);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        testIrc.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING);
        stopRefreshOrLoadMore();
    }
}
