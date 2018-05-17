 package com.example.collect.collectmoneysystem.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;
import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener;
import com.example.collect.collectmoneysystem.R;
import com.example.collect.collectmoneysystem.adapter.CalculatorAdapter;
import com.example.collect.collectmoneysystem.app.AppApplication;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

 public class MainActivity extends BaseActivity {

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
         return R.layout.act_main;
     }

     @Override
     public void initPresenter() {

     }

     @Override
     public void initView() {
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
         initCalculator();
         initListener();
         initProductDetails();
     }

     private void initProductDetails() {
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
                 return false;
             }
         });

     }

     private void initListener() {
         clearAll.setOnClickListener(v->{
             getAmount.setText("0");
             });

         commitNum.setOnClickListener(v->{
             startActivity(TestActivity.class);
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
 }
