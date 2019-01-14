package com.example.collect.collectmoneysystem.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.collect.collectmoneysystem.R;
import com.example.collect.collectmoneysystem.app.AppApplication;
import com.example.collect.collectmoneysystem.app.AppConstant;
import com.example.collect.collectmoneysystem.bean.CheckStoreData;
import com.example.collect.collectmoneysystem.bean.HttpResponse;
import com.example.collect.collectmoneysystem.contract.MainContract;
import com.example.collect.collectmoneysystem.model.MainModel;
import com.example.collect.collectmoneysystem.presenter.MainPresenter;
import com.google.gson.Gson;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.base.BasePopupWindow;
import com.jaydenxiao.common.baseapp.AppManager;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.SPUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import cc.lotuscard.ILotusCallBack;
import cc.lotuscard.LotusCardDriver;
import cc.lotuscard.LotusCardParam;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static cc.lotuscard.LotusCardDriver.m_InEndpoint;
import static cc.lotuscard.LotusCardDriver.m_OutEndpoint;
import static cc.lotuscard.LotusCardDriver.m_UsbDeviceConnection;

public class MainActivity extends BaseActivity<MainPresenter, MainModel> implements MainContract.View, ILotusCallBack {

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

    @BindView(R.id.commit)
    Button commit;
    @BindView(R.id.bind)
    Button bind;
    @BindView(R.id.rl_main)
    RelativeLayout rl_main;
    private List<String> cards = new ArrayList<>();
    private View pop;
    private BasePopupWindow popupWindow;
    int validCards = 0;


    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_main;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this, mModel);
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
        initListener();
        initPopup();
    }

    private void initListener() {
        commit.setOnClickListener(v -> {

            String data = (new Gson()).toJson(cards);
            LogUtils.loge(data);
            if (cards.size() > 0) {
                validCards = getValidCards();//这次盘点刷的有效卡数
                mPresenter.getInventoryRequest(data);
                cards.clear();
            } else {
                ToastUtil.showShort("请先刷要盘点的卡！");
            }
        });

        bind.setOnClickListener(v -> {
            new MaterialDialog.Builder(this)
                    .title("售后的样衣重绑定")
                    .input("请输入样衣编号", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            LogUtils.loge("输入的是：" + input);
                            if (input.toString().length() == 0) {
                                ToastUtil.showShort("请先输入样衣编号");
                            } else {
                                if (cards != null && cards.size() > 0) {
                                    mPresenter.bindingRequest(cards.get(cards.size() - 1), input.toString());
                                    cards.clear();
                                } else {
                                    ToastUtil.showShort("请先刷卡");
                                }
                            }
                        }
                    })
                    .negativeText("取消")
                    .positiveColor(getResources().getColor(R.color.main_blue))
                    .show();
        });
    }

    private MultipartBody.Part getSpecialBodyType(String clothesIds) {
        //创建RequestBody，其中multipart/form-data为编码类型
        RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), clothesIds);


        //单纯的clothesIds数组
//                                        MultipartBody.Part[] clothesIds = new MultipartBody.Part[clothesIdList.size()];
//                                        for (int i=0;i<clothesIdList.size();i++) {
//                                            clothesIds[i] = getSpecialBodyType(clothesIdList.get(i));
//                                        }
//                                        mPresenter.getProductOrderRequest(clothesIds);

        return MultipartBody.Part.createFormData("card_nums", clothesIds);
    }

    private void initHandleCardDetails() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (flag) {
                    String num = msg.obj.toString();
                    cards.add(num);
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
        } else {
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
        if (null == usbManager) {
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
        if (null == usbDevice) {
            return bResult;
        }
        usbInterface = usbDevice.getInterface(0);
        if (null == usbInterface) {
            return bResult;
        }
        if (false == usbManager.hasPermission(usbDevice)) {//权限判断
            usbManager.requestPermission(usbDevice, pendingIntent);
        }

        if (usbManager.hasPermission(usbDevice)) {
            conn = usbManager.openDevice(usbDevice);//获取实例
        }

        if (null == conn) {
            return bResult;
        }

        if (conn.claimInterface(usbInterface, true)) {
            usbDeviceConnection = conn;
        } else {
            conn.close();
        }
        if (null == usbDeviceConnection) {
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
            int n = 0;

            while (true) {//使得线程循环
                if (haveUsbHostApi && flag) {//是否暂停
                    Log.e("test===", String.valueOf(n++));
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
                    arrBuffer[0] = (byte) nResult;
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
                addLog("nResult != 64 is" + nResult);
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

    //盘点返回
    @SuppressLint("StringFormatMatches")
    @Override
    public void returnGetInventory(CheckStoreData checkStoreData, String num) {
        if (checkStoreData != null) {
            TextView actual = pop.findViewById(R.id.actual_count);
            TextView clothesCards = pop.findViewById(R.id.clothes_card_count);
            TextView otherCards = pop.findViewById(R.id.other_card_count);
            Button btn = pop.findViewById(R.id.btn);

            actual.setText(getString(R.string.actual_count, checkStoreData.getActual_count(), 0));
            clothesCards.setText(getString(R.string.clothes_card_count, checkStoreData.getClothes_card_count(), 0));
            otherCards.setText(getString(R.string.other_card_count, validCards, 0));

            btn.setOnClickListener(v -> popupWindow.dismiss());
            showPopupWindow();
        }
    }

    //去重
    private int getValidCards() {
        List newCard = new ArrayList(new HashSet(cards));
        return newCard.size();
    }

    //返回卡号和衣服重新绑定
    @Override
    public void returnBinding(HttpResponse httpResponse) {
        if (httpResponse.getSuccess()) {
            ToastUtil.showShort("绑定成功!");
        }
    }

    private void initPopup() {
        pop = LayoutInflater.from(this).inflate(R.layout.pop_inventroy, null);
    }

    private void showPopupWindow() {
        popupWindow = new BasePopupWindow(this);
//        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(400);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setHeight(400);
        popupWindow.setContentView(pop);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        popupWindow.showAtLocation(rl_main, Gravity.CENTER, 0, 0);
        popupWindow.setAnimationStyle(R.style.MyPopWindow_anim_style);//好像起不了作用
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {
        //用户信息的token过期时
        if (msg == "token过期") {
            SPUtils.setSharedStringData(AppApplication.getAppContext(), AppConstant.TOKEN, "");
            AppManager.getAppManager().finishAllActivity();
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
            ToastUtil.showShort("用户信息已经过期,请重新登录");
            return;
        }
        ToastUtil.showShort(msg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        flag = false;
    }

}
