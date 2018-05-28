 package com.example.collect.collectmoneysystem.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;
import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener;
import com.example.collect.collectmoneysystem.R;
import com.example.collect.collectmoneysystem.adapter.CalculatorAdapter;
import com.example.collect.collectmoneysystem.app.AppApplication;
import com.example.collect.collectmoneysystem.app.AppConstant;
import com.example.collect.collectmoneysystem.bean.HttpResponse;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.example.collect.collectmoneysystem.contract.MainContract;
import com.example.collect.collectmoneysystem.model.MainModel;
import com.example.collect.collectmoneysystem.presenter.MainPresenter;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baserx.RxBus2;
import com.jaydenxiao.common.commonutils.ImageLoaderUtils;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cc.lotuscard.ILotusCallBack;
import cc.lotuscard.LotusCardDriver;
import cc.lotuscard.LotusCardParam;
import io.reactivex.functions.Consumer;

import static cc.lotuscard.LotusCardDriver.m_InEndpoint;
import static cc.lotuscard.LotusCardDriver.m_OutEndpoint;
import static cc.lotuscard.LotusCardDriver.m_UsbDeviceConnection;

 public class MainActivity extends BaseActivity<MainPresenter,MainModel> implements MainContract.View ,ILotusCallBack {

     private LotusCardDriver mLotusCardDriver;
     private UsbManager usbManager = null;
     private UsbDevice usbDevice = null;
     private UsbInterface usbInterface = null;
     private UsbDeviceConnection usbDeviceConnection = null;
     private final int m_nVID = 1306;//供应商ID
     private final int m_nPID = 20763;//产品识别码
     private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

     public static boolean haveUsbHostApi = false;
     private String deviceNode;//USB设备名称
     private HashMap<String, UsbDevice> deviceList;

     private long deviceHandle = -1;
     private Handler mHandler = null;
     private CardOperateThread cardOperateThread;
     private LotusCardParam tLotusCardParam1 = new LotusCardParam();
     private UsbDeviceConnection conn = null;//这个类用于发送和接收数据和控制消息到USB设备
     private boolean flag = false;

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
     @BindView(R.id.scan_add_goods)
     Button scan_add_goods;


     List<ProductDetails> productDetailsList = new ArrayList<>();
     CommonRecycleViewAdapter<ProductDetails> productAdapter;

     float factPrice = 0;
     float finalPrice = 0;
     float associatorDiscount = 10;

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
     protected void onResume() {
         super.onResume();
         flag = true;
     }

     @Override
     public void initView() {
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
         getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//底部导航栏覆盖activity
         mLotusCardDriver = new LotusCardDriver();
         mLotusCardDriver.m_lotusCallBack = this;
         // 设置USB读写回调 串口可以不用此操作
         haveUsbHostApi = setUsbCallBack();
         //测卡器设备检测
         cardDeviceChecked();
         initHandleCardDetails();
         initCalculator();
         initListener();
         initProductDetails();
         initDiscount();
     }

     private void initDiscount() {
         associatorDiscount = (float) (Math.round(associatorDiscount * 10)) / 10;
         discount.setText(String.valueOf(associatorDiscount));
     }

     private void initProductDetails() {
         productAdapter = new CommonRecycleViewAdapter<ProductDetails>(mContext,R.layout.item_product_details,productDetailsList) {
             @Override
             public void convert(ViewHolderHelper helper, ProductDetails productDetails) {
                 TextView part = helper.getView(R.id.part);
                 TextView spec = helper.getView(R.id.spec);
                 TextView size = helper.getView(R.id.size);
                 TextView price = helper.getView(R.id.price);
                 ImageView img = helper.getView(R.id.sample_photo);

                 part.setText(productDetails.getName());
                 spec.setText(productDetails.getSpec());
                 size.setText(productDetails.getSize());
                 price.setText(String.valueOf(productDetails.getRetailPrice()));
                 ImageLoaderUtils.displaySmallPhoto(MainActivity.this,img, AppConstant.IMAGE_DOMAIN_NAME+productDetails.getImage());

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
             startActivity(TestActivity.class);
         });

         addGoods.setOnClickListener(v ->
                 mPresenter.getProductDetailsRequest("826168449")
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

         scan_add_goods.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mPresenter.getProductDetailsWithScanRequest("http://weixin.qq.com/q/02gJC4lIIAdW210000g07x");
             }
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
                 if (keyTypt.equals("←")&&!editText.equals("0")) {
                     if (editText.length() > 1) {
                         getAmount.setText(editText.substring(0,editText.length()-1));
                     }else {
                         getAmount.setText("0");
                     }

                 }
             }

         });
     }

     private void initHandleCardDetails() {
         mHandler = new Handler() {
             @Override
             public void handleMessage(Message msg) {
                 super.handleMessage(msg);
                 if (flag) {
                    String num = msg.obj.toString();
                    mPresenter.getProductDetailsRequest("826168449");
                    flag = false;
                 }
             }
         };
     }

     //刷卡器USB状态检测
     private void cardDeviceChecked() {
         if (haveUsbHostApi) {
             ToastUtil.showShort("读卡设备已连接！");
//            m_tvDeviceNode.post(new Runnable() {
//                @Override
//                public void run() {
//                    m_tvDeviceNode.setText("已连接");
//                }
//            });
             initAuto();
         }else {
             ToastUtil.showShort("未检测到读卡设备！");
//            m_tvDeviceNode.post(new Runnable() {
//                @Override
//                public void run() {
//                    m_tvDeviceNode.setText("未连接");
//                }
//            });
         }
     }

     //设置USB读写回调
     private Boolean setUsbCallBack() {
         Boolean bResult = false;
         PendingIntent pendingIntent;
         pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                 ACTION_USB_PERMISSION), 0);
         // Get UsbManager from Android.
         usbManager = (UsbManager) AppApplication.getAppContext().getSystemService(USB_SERVICE);
         if (null == usbManager){
             return bResult;
         }

         //获取设备及设备名字
         deviceList = usbManager.getDeviceList();
         if (!deviceList.isEmpty()) {
             for (UsbDevice device : deviceList.values()) {
                 if ((m_nVID == device.getVendorId())
                         && (m_nPID == device.getProductId())) {
                     usbDevice = device;
                     deviceNode = usbDevice.getDeviceName();
                     break;
                 }
             }
         }
         if (null == usbDevice){
             return bResult;
         }
         usbInterface = usbDevice.getInterface(0);
         if (null == usbInterface){
             return bResult;
         }
         if (false == usbManager.hasPermission(usbDevice)) {//权限判断
             usbManager.requestPermission(usbDevice, pendingIntent);
         }

         if (usbManager.hasPermission(usbDevice)) {
             conn = usbManager.openDevice(usbDevice);//获取实例
         }

         if (null == conn){
             return bResult;
         }

         if (conn.claimInterface(usbInterface, true)) {
             usbDeviceConnection = conn;
         } else {
             conn.close();
         }
         if (null == usbDeviceConnection){
             return bResult;
         }
         // 把上面获取的对性设置到接口中用于回调操作
         LotusCardDriver.m_UsbDeviceConnection = usbDeviceConnection;
         if (usbInterface.getEndpoint(1) != null) {
             LotusCardDriver.m_OutEndpoint = usbInterface.getEndpoint(1);
         }
         if (usbInterface.getEndpoint(0) != null) {
             LotusCardDriver.m_InEndpoint = usbInterface.getEndpoint(0);
         }
         bResult = true;
         return bResult;
     }

     //自动检测USB设备初始化
     public void initAuto() {
         if (-1 == deviceHandle) {
             deviceHandle = mLotusCardDriver.OpenDevice("", 0, 0, 0, 0,// 使用内部默认超时设置
                     true);
         }
         if (deviceHandle != -1) {
             cardOperateThread = new CardOperateThread();
             new Thread(cardOperateThread).start();
         }
     }

     //子线程检测卡号
     public class CardOperateThread implements Runnable {
         @Override
         public void run() {
             boolean bResult;
             int nRequestType;
             long lCardNo;
             int n=0;

             while (true) {//使得线程循环
                 if (haveUsbHostApi && flag) {//是否暂停
                     Log.e("test===",String.valueOf(n++));
                     try {
                         nRequestType = LotusCardDriver.RT_NOT_HALT;//未进入休眠的卡
                         bResult = mLotusCardDriver.GetCardNo(deviceHandle, nRequestType, tLotusCardParam1);//获取卡号，true表示成功

                         //如果失败了则sleep跳出,再循环
                         if (!bResult) {
                             Thread.sleep(500);
                             continue;
                         }

                         Message msg = new Message();
                         lCardNo = bytes2long(tLotusCardParam1.arrCardNo);
                         msg.obj = lCardNo;
                         mHandler.sendMessage(msg);

                         mLotusCardDriver.Beep(deviceHandle, 10);//响铃
                         mLotusCardDriver.Halt(deviceHandle);//响铃关闭
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }

             }
         }
     }

     //获取到的卡号是4个字节的需转换
     public long bytes2long(byte[] byteNum) {
         long num = 0;
         for (int ix = 3; ix >= 0; --ix) {
             num <<= 8;
             if (byteNum[ix] < 0) {
                 num |= (256 + (byteNum[ix]) & 0xff);
             } else {
                 num |= (byteNum[ix] & 0xff);
             }
         }
         return num;
     }

     @Override
     public boolean callBackExtendIdDeviceProcess(Object objUser, byte[] arrBuffer) {
         return false;
     }

     @Override
     public boolean callBackReadWriteProcess(long nDeviceHandle, boolean bRead, byte[] arrBuffer) {
         int nResult = 0;
         boolean bResult = false;
         int nBufferLength = arrBuffer.length;
         int nWaitCount = 0;
         if (null == m_UsbDeviceConnection) {
             addLog("null == m_UsbDeviceConnection");
             return false;
         }
         if (null == m_OutEndpoint) {
             addLog("null == m_OutEndpoint");
             return false;
         }
         if (null == m_InEndpoint) {
             addLog("null == m_InEndpoint");
             return false;
         }
         if (nBufferLength < 65) {
             addLog("nBufferLength < 65");
             return false;
         }
         if (true == bRead) {
             arrBuffer[0] = 0;
             while (true) {
                 nResult = m_UsbDeviceConnection.bulkTransfer(m_InEndpoint,
                         arrBuffer, 64, 3000);
                 if (nResult <= 0) {
                     addLog("nResult <= 0 is " + nResult);
                     break;
                 }
                 if (arrBuffer[0] != 0) {
                     //此处调整一下
                     System.arraycopy(arrBuffer, 0, arrBuffer, 1, nResult);
                     arrBuffer[0] = (byte)nResult;
                     break;
                 }
                 nWaitCount++;
                 if (nWaitCount > 1000) {
                     addLog("nWaitCount > 1000");
                     break;
                 }
             }
             if (nResult == 64) {
                 bResult = true;
             } else {
                 addLog("nResult != 64 is" +nResult);
                 bResult = false;
             }
         } else {
             nResult = m_UsbDeviceConnection.bulkTransfer(m_OutEndpoint,
                     arrBuffer, 64, 3000);
             if (nResult == 64) {
                 bResult = true;
             } else {
                 addLog("m_OutEndpoint bulkTransfer Write error");
                 bResult = false;
             }
         }
         return bResult;
     }
     public void addLog(String strLog) {
     }


     //返回获取的成衣情况
     @Override
     public void returnGetProductDetails(ProductDetails productDetails) {
         flag = true;
         ToastUtil.showShort("OK");
         productDetailsList.add(productDetails);
         productAdapter.notifyDataSetChanged();

         goodsCounts.setText(String.valueOf(productDetailsList.size()));
         factPrice = factPrice + productDetails.getRetailPrice();
         goodsTotals.setText(String.valueOf(factPrice));

         finalPrice = factPrice * associatorDiscount / 10;
         receivable.setText(String.valueOf(finalPrice));
         final_fact.setText(String.valueOf(finalPrice));
     }

     @Override
     public void returnGetProductDetailsWithScan(ProductDetails productDetails) {
         ToastUtil.showShort("OK");
         productDetailsList.add(productDetails);
         productAdapter.notifyDataSetChanged();

         goodsCounts.setText(String.valueOf(productDetailsList.size()));
         factPrice = factPrice + productDetails.getRetailPrice();
         goodsTotals.setText(String.valueOf(factPrice));

         finalPrice = factPrice * associatorDiscount / 10;
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
         flag = true;
         ToastUtil.showShort(msg);
     }

     @Override
     protected void onPause() {
         super.onPause();
         flag = false;
     }
 }
