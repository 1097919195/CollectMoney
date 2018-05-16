 package com.example.collect.collectmoneysystem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collect.collectmoneysystem.R;
import com.example.collect.collectmoneysystem.adapter.CalculatorAdapter;
import com.example.collect.collectmoneysystem.app.AppApplication;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.ToastUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

 public class MainActivity extends BaseActivity {

     @BindView(R.id.clearAll)
     Button clearAll;
     @BindView(R.id.edit_getAmount)
     EditText getAmount;
     @BindView(R.id.recycleView)
     RecyclerView recycleView;
     CalculatorAdapter calculatorAdapter;
     List<String> num = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.Ca_num));
     List<String> type = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.Ca_type));

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
     }

     private void initListener() {
         clearAll.setOnClickListener(v->{
             getAmount.setText("0");
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
