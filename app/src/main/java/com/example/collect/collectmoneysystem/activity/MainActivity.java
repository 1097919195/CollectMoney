 package com.example.collect.collectmoneysystem.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;
import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener;
import com.example.collect.collectmoneysystem.R;
import com.example.collect.collectmoneysystem.adapter.CalculatorAdapter;
import com.example.collect.collectmoneysystem.app.AppApplication;
import com.example.collect.collectmoneysystem.bean.HttpResponse;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.example.collect.collectmoneysystem.contract.MainContract;
import com.example.collect.collectmoneysystem.model.MainModel;
import com.example.collect.collectmoneysystem.presenter.MainPresenter;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

 public class MainActivity extends BaseActivity<MainPresenter,MainModel> implements MainContract.View {

     @BindView(R.id.commitNum)
     Button commitNum;
     @BindView(R.id.productDetailIrc)
     RecyclerView productDetailIrc;
     @BindView(R.id.clearAll)
     Button clearAll;
     @BindView(R.id.edit_getAmount)
     EditText getAmount;
     @BindView(R.id.recycleView)
     RecyclerView recycleView;
     CalculatorAdapter calculatorAdapter;
     List<String> num = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.Ca_num));
     List<String> type = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.Ca_type));

     @BindView(R.id.add_goods)
     Button addGoods;
     @BindView(R.id.goods_clear)
     TextView goods_clear;
     @BindView(R.id.goods_counts)
     TextView goodsCounts;
     @BindView(R.id.goods_totals)
     TextView goodsTotals;
     @BindView(R.id.discount)
     TextView discount;
     @BindView(R.id.receivable)
     TextView receivable;

     @BindView(R.id.final_fact)
     TextView final_fact;
     @BindView(R.id.final_totals)
     TextView final_totals;
     @BindView(R.id.oddChange)
     TextView oddChange;


     List<ProductDetails> productDetailsList = new ArrayList<>();
     CommonRecycleViewAdapter<ProductDetails> productAdapter;

     float factPrice = 0;
     float finalPrice = 0;

     MaterialDialog delateDialog;

     @Override
     public int getLayoutId() {
         return R.layout.act_main;
     }

     @Override
     public void initPresenter() {
         mPresenter.setVM(this , mModel);
     }

     @Override
     public void initView() {
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
         getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//底部导航栏覆盖activity
         initCalculator();
         initListener();
         initProductDetails();
     }

     private void initProductDetails() {
         productAdapter = new CommonRecycleViewAdapter<ProductDetails>(mContext,R.layout.item_product_details,productDetailsList) {
             @Override
             public void convert(ViewHolderHelper helper, ProductDetails productDetails) {
                 TextView part = helper.getView(R.id.part);
                 TextView spec = helper.getView(R.id.spec);
                 TextView size = helper.getView(R.id.size);
                 TextView price = helper.getView(R.id.price);

                 part.setText(productDetails.getName());
                 spec.setText(productDetails.getSpec());
                 size.setText(productDetails.getSize());
                 price.setText(String.valueOf(productDetails.getRetailPrice()));
             }
         };

         productDetailIrc.setAdapter(productAdapter);
         productDetailIrc.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
         //设置分割线
         productDetailIrc.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));//默认
//         DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
//         divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.custom_divider));
//         productDetailIrc.addItemDecoration(divider);

         productAdapter.setOnItemClickListener(new OnItemClickListener() {
             @Override
             public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                 ToastUtil.showShort(String.valueOf(position));
             }

             @Override
             public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {

                 delateDialog = new MaterialDialog.Builder(MainActivity.this)
                         .title("是否删除该商品？")
                         .backgroundColor(getResources().getColor(R.color.white))
                         .positiveText("确认")
                         .negativeText("取消")
                         .onPositive((dialog, which) -> {
                             factPrice = factPrice - productDetailsList.get(position).getRetailPrice();
                             productDetailsList.remove(position);
                             productAdapter.notifyDataSetChanged();

                             goodsCounts.setText(String.valueOf(productDetailsList.size()));
                             goodsTotals.setText(String.valueOf(factPrice));
                             finalPrice = factPrice * 9 / 10;
                             receivable.setText(String.valueOf(finalPrice));
                             final_fact.setText(String.valueOf(finalPrice));
                         })
                         .negativeColor(getResources().getColor(R.color.red))
                         .build();
                 delateDialog.show();

                 return false;
             }
         });

     }

     private void initListener() {
         clearAll.setOnClickListener(v->{
             getAmount.setText("0");
         });

         commitNum.setOnClickListener(v->{
//             startActivity(TestActivity.class);
         });

         addGoods.setOnClickListener(v ->
                 mPresenter.getProductDetailsRequest("2770769178")
         );

         goods_clear.setOnClickListener(v -> {
             productDetailsList.clear();
             productAdapter.notifyDataSetChanged();
             goodsCounts.setText("");
             factPrice = 0;
             finalPrice = 0;
             goodsTotals.setText("");
             receivable.setText("");
             final_fact.setText("0.0");
         });

         RxTextView.textChanges(final_fact)
                 .debounce( 100 , TimeUnit.MILLISECONDS )
                 .subscribe(new Consumer<Object>() {
                     @Override
                     public void accept(Object o) throws Exception {
                         final_totals.post(new Runnable() {
                             @Override
                             public void run() {
                                 final_totals.setText(String.valueOf(Float.parseFloat(getAmount.getText().toString())));
                             }
                         });

                         oddChange.post(new Runnable() {
                             @Override
                             public void run() {
                                 float number = (Float.parseFloat(getAmount.getText().toString()) - finalPrice);
                                 oddChange.setText(String.valueOf((float) (Math.round(number * 100)) / 100));
                             }
                         });
                     }
                 });

         RxTextView.textChanges(getAmount)
                 .debounce( 100 , TimeUnit.MILLISECONDS )
                 .subscribe(new Consumer<Object>() {
                     @Override
                     public void accept(Object o) throws Exception {
                         final_totals.post(new Runnable() {
                             @Override
                             public void run() {
                                 final_totals.setText(String.valueOf(Float.parseFloat(getAmount.getText().toString())));
                             }
                         });

                         oddChange.post(new Runnable() {
                             @Override
                             public void run() {
                                 float number = (Float.parseFloat(getAmount.getText().toString()) - finalPrice);
                                 oddChange.setText(String.valueOf((float) (Math.round(number * 100)) / 100));
                             }
                         });
                     }
                 });

     }

     private void initCalculator() {
         calculatorAdapter = new CalculatorAdapter(mContext,num,type);
         recycleView.setAdapter(calculatorAdapter);
         recycleView.setLayoutManager(new GridLayoutManager(mContext,3,StaggeredGridLayoutManager.VERTICAL,false));
//         recycleView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
//         recycleView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

         calculatorAdapter.setOnItemClickListener((position, keyTypt) -> {
             String actVlaue = num.get(position);
             String editText = getAmount.getText().toString();
             if (keyTypt.equals("num")) {
                 if(editText.lastIndexOf(".")!=-1 && editText.lastIndexOf(".")<editText.length()-2){
                     ToastUtil.showShort("只能输入小数点后两位");
                 }else {
                     if (editText.equals("0")) {
                         getAmount.setText(actVlaue);
                     } else {
                         getAmount.setText(editText + actVlaue);
                     }
                 }
             } else {
                 if (keyTypt.equals(".") && !editText.contains(".")) {
                     getAmount.setText(editText + actVlaue);
                 }
                 if (keyTypt.equals("✖")&&!editText.equals("0")) {
                     if (editText.length() > 1) {
                         getAmount.setText(editText.substring(0,editText.length()-1));
                     }else {
                         getAmount.setText("0");
                     }

                 }
             }

         });
     }

     //返回获取的成衣情况
     @Override
     public void returnGetProductDetails(ProductDetails productDetails) {
         ToastUtil.showShort("OK");
         productDetailsList.add(productDetails);
         productAdapter.notifyDataSetChanged();

         goodsCounts.setText(String.valueOf(productDetailsList.size()));
         factPrice = factPrice + productDetails.getRetailPrice();
         goodsTotals.setText(String.valueOf(factPrice));
         discount.setText("9");
         finalPrice = factPrice * 9 / 10;
         receivable.setText(String.valueOf(finalPrice));
         final_fact.setText(String.valueOf(finalPrice));
     }

     @Override
     public void showLoading(String title) {

     }

     @Override
     public void stopLoading() {

     }

     @Override
     public void showErrorTip(String msg) {
         ToastUtil.showShort(msg);
     }
 }
