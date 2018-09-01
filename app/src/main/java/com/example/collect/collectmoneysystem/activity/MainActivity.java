package com.example.collect.collectmoneysystem.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;
import com.carlos.notificatoinbutton.library.NotificationButton;
import com.example.collect.collectmoneysystem.R;
import com.example.collect.collectmoneysystem.adapter.CalculatorAdapter;
import com.example.collect.collectmoneysystem.app.AppApplication;
import com.example.collect.collectmoneysystem.app.AppConstant;
import com.example.collect.collectmoneysystem.bean.ClothesIdBean;
import com.example.collect.collectmoneysystem.bean.HttpResponse;
import com.example.collect.collectmoneysystem.bean.InventoryData;
import com.example.collect.collectmoneysystem.bean.OrderData;
import com.example.collect.collectmoneysystem.bean.PayOrderWithMultipartBean;
import com.example.collect.collectmoneysystem.bean.ProductDetails;
import com.example.collect.collectmoneysystem.bean.SerializableChild;
import com.example.collect.collectmoneysystem.bean.SerializableGroup;
import com.example.collect.collectmoneysystem.camera.CaptureActivity;
import com.example.collect.collectmoneysystem.contract.MainContract;
import com.example.collect.collectmoneysystem.model.MainModel;
import com.example.collect.collectmoneysystem.presenter.MainPresenter;
import com.example.collect.collectmoneysystem.utils.MaterialDialogUtils;
import com.example.collect.collectmoneysystem.widget.SlideDelete;
import com.example.collect.collectmoneysystem.widget.SlideDelete.OnSlideDeleteListener;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.base.BasePopupWindow;
import com.jaydenxiao.common.baseapp.AppManager;
import com.jaydenxiao.common.commonutils.ACache;
import com.jaydenxiao.common.commonutils.ImageLoaderUtils;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.SPUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cc.lotuscard.ILotusCallBack;
import cc.lotuscard.LotusCardDriver;
import cc.lotuscard.LotusCardParam;
import io.reactivex.functions.Consumer;
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
    TextView goodsKindCounts;
    @BindView(R.id.goods_totals)
    TextView goodsTotalsFee;
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
    @BindView(R.id.register_account)
    TextView register_account;
    @BindView(R.id.register_account_details)
    NotificationButton notificationButton;
    @BindView(R.id.productCode)
    EditText productCode;
    @BindView(R.id.overPlus)
    Button overPlus;
    @BindView(R.id.rl_main)
    RelativeLayout rl_main;


    List<ProductDetails> productDetailsList = new ArrayList<>();
    List<ProductDetails> registerDetails = new ArrayList<>();
    CommonRecycleViewAdapter<ProductDetails> productAdapter;

    float factPrice = 0;
    float finalPrice = 0;
    float associatorDiscount = 10;
    int registerCount = 0;

    MaterialDialog delateDialog;
    public static final int REQUEST_CODE_SAMPLE = 1201;
    private static final int SCAN_HINT = 1001;
    private static final int CODE_HINT = 1002;

    MaterialDialog registerDialog;
    MaterialDialog payDialog;
    Bundle bundle = new Bundle();
    SerializableChild child = new SerializableChild();
    SerializableGroup group = new SerializableGroup();
    List<List<ProductDetails>> childList = new ArrayList<>();
    ArrayList<String> groupList = new ArrayList<>();
    List<SlideDelete> slideDeleteArrayList = new ArrayList<>();
    List<String> clothesIdList = new ArrayList<>();
    boolean haveClothesIds = false;
    boolean haveCard = false;
    List<Integer> clothesIdCount = new ArrayList<>();
    List<String> cardsList = new ArrayList<>();//记录刷过的卡
    List<List<String>> clothesIdWithCards = new ArrayList<>();//记录对应clothesId刷过的卡

    private View pop;
    private Button btn_left;
    private Button btn_right;
    private ImageView pop_exit;
    private IRecyclerView irc_search;
    private BasePopupWindow popupWindow;

    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        initCalculator();
        initPopup();
        initListener();
        initProductDetails();
        initDiscount();
        initRxBus();
        //挂单功能取消，逻辑太麻烦了
        register_account.setVisibility(View.GONE);
        notificationButton.setVisibility(View.GONE);
    }

    private void initPopup() {
        pop = LayoutInflater.from(this).inflate(R.layout.pop_search, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSaveInstanceState(savedInstanceState);
    }

    private void initSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            LogUtils.loge("initSaveInstanceState");
            bundle = savedInstanceState.getBundle(AppConstant.REGISTER_DETAILS_WITH_SAVEINSTANCESTATE);
            registerCount = savedInstanceState.getInt(AppConstant.REGISTER_MSG_WITH_SAVEINSTANCESTATE);
            notificationButton.setNotificationNumber(registerCount);
        } else {
            if (ACache.get(AppApplication.getAppContext()).getAsObject(AppConstant.ACACHE_REGISTER_DETAILS_WITH_CHILD) != null && ACache.get(AppApplication.getAppContext()).getAsObject(AppConstant.ACACHE_REGISTER_DETAILS_WITH_GROUP) != null && ACache.get(AppApplication.getAppContext()).getAsString(AppConstant.ACACHE_REGISTER_DETAILS_WITH_MSG) != null) {
                child = (SerializableChild) ACache.get(AppApplication.getAppContext()).getAsObject(AppConstant.ACACHE_REGISTER_DETAILS_WITH_CHILD);
                group = (SerializableGroup) ACache.get(AppApplication.getAppContext()).getAsObject(AppConstant.ACACHE_REGISTER_DETAILS_WITH_GROUP);
                registerCount = Integer.parseInt(ACache.get(AppApplication.getAppContext()).getAsString(AppConstant.ACACHE_REGISTER_DETAILS_WITH_MSG));
                notificationButton.setNotificationNumber(registerCount);
                bundle.putSerializable(AppConstant.SEND_REGISTER_DETAILS_WITH_CHILD, child);
                bundle.putSerializable(AppConstant.SEND_REGISTER_DETAILS_WITH_GROUP, group);
            }
        }
    }

    private void initRxBus() {
        mRxManager.on(AppConstant.REGISTER_RETURN, (Consumer<Integer>) integer -> {
            int position = integer;
            productDetailsList = child.getMap().get(position);
            initProductDetails();
            factPrice = 0;
            goodsKindCounts.setText(String.valueOf(productDetailsList.size()));
            for (ProductDetails list : productDetailsList) {
                factPrice = factPrice + list.getRetailPrice();
            }
            goodsTotalsFee.setText(String.valueOf(factPrice));

            finalPrice = factPrice * associatorDiscount / 10;
            receivable.setText(String.valueOf(finalPrice));
            final_fact.setText(String.valueOf(finalPrice));

            child.getMap().remove(position);
            group.getGroupItems().remove(position);
            registerCount = registerCount - 1;
            notificationButton.setNotificationNumber(registerCount);
        });
    }

    private void initDiscount() {
        associatorDiscount = (float) (Math.round(associatorDiscount * 10)) / 10;
        discount.setText(String.valueOf((int)associatorDiscount));
    }

    private void initProductDetails() {
        productAdapter = new CommonRecycleViewAdapter<ProductDetails>(mContext, R.layout.item_slide, productDetailsList) {
            @Override
            public void convert(ViewHolderHelper helper, ProductDetails productDetails) {
                TextView part = helper.getView(R.id.part);
                TextView spec = helper.getView(R.id.spec);
                TextView size = helper.getView(R.id.size);
                TextView price = helper.getView(R.id.price);
                ImageView img = helper.getView(R.id.sample_photo);
                TextView delete = helper.getView(R.id.mTvDelete);
//                TextView minus = helper.getView(R.id.decrease);
//                TextView plus = helper.getView(R.id.increase);
                SlideDelete slideDelete = helper.getView(R.id.slideDelete);

                part.setText(productDetails.getName());
                spec.setText(String.valueOf(productDetails.getClothesIdCounts()));
                size.setText(productDetails.getSize());
                price.setText(String.valueOf(productDetails.getRetailPrice()));
                if (productDetails.getImage() != null&&productDetails.getImage().size()>0) {
                    ImageLoaderUtils.displaySmallPhoto(MainActivity.this, img, AppConstant.IMAGE_DOMAIN_NAME + productDetails.getImage().get(0).getRelative_path());
                } else {
                    img.setImageResource(R.mipmap.background);
                }

                delete.setOnClickListener(v->{
                    delateDialog = new MaterialDialog.Builder(MainActivity.this)
                            .title("是否删除该商品？")
                            .backgroundColor(getResources().getColor(R.color.white))
                            .positiveText("确认")
                            .negativeText("取消")
                            .onPositive((dialog, which) -> {
                                ToastUtil.showShort(String.valueOf(helper.getLayoutPosition()));
                                factPrice = factPrice - productDetailsList.get(helper.getLayoutPosition()).getRetailPrice()*(productDetailsList.get(helper.getLayoutPosition()).getClothesIdCounts());
                                productDetailsList.remove(helper.getLayoutPosition());
                                productAdapter.notifyDataSetChanged();

                                goodsKindCounts.setText(String.valueOf(productDetailsList.size()));
                                goodsTotalsFee.setText(String.valueOf(factPrice));
                                finalPrice = factPrice * associatorDiscount / 10;
                                receivable.setText(String.valueOf(finalPrice));
                                final_fact.setText(String.valueOf(finalPrice));

                                clothesIdList.remove(helper.getLayoutPosition());
                                //清除clothesId下的所有卡
                                for (String cards : clothesIdWithCards.get(helper.getLayoutPosition())) {
                                    cardsList.remove(cardsList.indexOf(cards));
                                }
                                //清除clothesId下的所有卡
                                clothesIdWithCards.remove(helper.getLayoutPosition());

                            })
                            .negativeColor(getResources().getColor(R.color.red))
                            .build();
                    delateDialog.show();
                });

//                //商品加一
//                plus.setOnClickListener(v->{
//                    int counts = productDetailsList.get(helper.getLayoutPosition()).getClothesIdCounts();
//                    productDetailsList.get(helper.getLayoutPosition()).setClothesIdCounts(counts+1);
//                    productAdapter.notifyDataSetChanged();
//                    ToastUtil.showShort("该样衣已在列表中"+helper.getLayoutPosition());
//                    factPrice = factPrice + productDetails.getRetailPrice();
//                    goodsTotalsFee.setText(String.valueOf(factPrice));
//                    finalPrice = factPrice * associatorDiscount / 10;
//                    receivable.setText(String.valueOf(finalPrice));
//                    final_fact.setText(String.valueOf(finalPrice));
//                });
//
//                //商品减一
//                minus.setOnClickListener(v->{
//                    int counts = productDetailsList.get(helper.getLayoutPosition()).getClothesIdCounts();
//                    productDetailsList.get(helper.getLayoutPosition()).setClothesIdCounts(counts-1);
//                    if (productDetailsList.get(helper.getLayoutPosition()).getClothesIdCounts() < 1) {
//                        productDetailsList.remove(helper.getLayoutPosition());
//                        clothesIdList.remove(helper.getLayoutPosition());
//                        goodsKindCounts.setText(String.valueOf(productDetailsList.size()));//加的时候不用管，减的时候需要
//                    }
//                    productAdapter.notifyDataSetChanged();
//                    ToastUtil.showShort("该样衣已在列表中"+helper.getLayoutPosition());
//                    factPrice = factPrice - productDetails.getRetailPrice();
//                    goodsTotalsFee.setText(String.valueOf(factPrice));
//                    finalPrice = factPrice * associatorDiscount / 10;
//                    receivable.setText(String.valueOf(finalPrice));
//                    final_fact.setText(String.valueOf(finalPrice));
//                });

                slideDelete.setOnSlideDeleteListener(new SlideDelete.OnSlideDeleteListener() {
                    @Override
                    public void onOpen(SlideDelete slideDelete) {
                        closeOtherItem();
                        slideDeleteArrayList.add(slideDelete);
                        slideDelete.isShowDelete(true);
                    }

                    @Override
                    public void onClose(SlideDelete slideDelete) {
                        slideDeleteArrayList.remove(slideDelete);
                    }
                });


            }
        };

        productDetailIrc.setAdapter(productAdapter);
        productDetailIrc.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        //设置分割线
        productDetailIrc.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//默认
//         DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
//         divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.custom_divider));
//         productDetailIrc.addItemDecoration(divider);

        //长按item删除指定商品
//        productAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
//                ToastUtil.showShort(String.valueOf(position));
//            }
//
//            @Override
//            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
//
//                delateDialog = new MaterialDialog.Builder(MainActivity.this)
//                        .title("是否删除该商品？")
//                        .backgroundColor(getResources().getColor(R.color.white))
//                        .positiveText("确认")
//                        .negativeText("取消")
//                        .onPositive((dialog, which) -> {
//                            factPrice = factPrice - productDetailsList.get(position).getRetailPrice();
//                            productDetailsList.remove(position);
//                            productAdapter.notifyDataSetChanged();
//
//                            goodsCounts.setText(String.valueOf(productDetailsList.size()));
//                            goodsTotals.setText(String.valueOf(factPrice));
//                            finalPrice = factPrice * 9 / 10;
//                            receivable.setText(String.valueOf(finalPrice));
//                            final_fact.setText(String.valueOf(finalPrice));
//                        })
//                        .negativeColor(getResources().getColor(R.color.red))
//                        .build();
//                delateDialog.show();
//
//                return false;
//            }
//        });

    }

    private void closeOtherItem(){
        // 采用Iterator的原因是for是线程不安全的，迭代器是线程安全的
        ListIterator<SlideDelete> slideDeleteListIterator = slideDeleteArrayList.listIterator();
        while(slideDeleteListIterator.hasNext()){
            SlideDelete slideDelete = slideDeleteListIterator.next();
            slideDelete.isShowDelete(false);
        }
        slideDeleteArrayList.clear();
    }

    private MaterialDialog dialog;
    private void initListener() {
        //清除计算器所有内容
        clearAll.setOnClickListener(v -> {
            getAmount.setText("0");
//            if (dialog != null) {
//                dialog.show();
//            } else {
//                MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(mContext, "请稍等...", true);
//                builder.cancelable(true);
//                dialog = builder.show();
//            }
        });

        //结算提交
        commitNum.setOnClickListener(v -> {
//            startActivity(TestActivity.class);
            if (clothesIdList.size() > 0) {
                if (Float.valueOf(getAmount.getEditableText().toString())>0) {
                    payDialog = new MaterialDialog.Builder(this)
                            .title("当前订单金额为 "+getAmount.getEditableText().toString()+" 元"+"(请以实际后台返回的金额为准)")
                            .widgetColor(Color.WHITE)//输入框光标的颜色
                            .contentColor(Color.WHITE)
                            .keyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    //这里注意要作判断处理，ActionDown、ActionUp都会回调到这里，不作处理的话就会调用两次
                                    if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
                                        //处理事件
                                        //todo 可以精简一下
                                        LogUtils.loge("one code"+payDialog.getInputEditText().getEditableText());
                                        if (payDialog.getInputEditText().getEditableText().length() == 18) {
                                            AppConstant.AUTH_CODE = payDialog.getInputEditText().getEditableText().toString();

                                            for (int i = 0; i<clothesIdList.size(); i++) {
                                                clothesIdCount.add(productDetailsList.get(i).getClothesIdCounts());
                                            }

                                            List<PayOrderWithMultipartBean> data = new ArrayList<>();
                                            for (int i = 0; i<clothesIdList.size(); i++) {
                                                PayOrderWithMultipartBean bean = new PayOrderWithMultipartBean(clothesIdList.get(i), clothesIdCount.get(i),clothesIdWithCards.get(i));
                                                data.add(bean);
                                            }
                                            String s = (new Gson()).toJson(data);
                                            LogUtils.loge(s);

                                            //单纯的clothesIds数组
//                                        MultipartBody.Part[] cardNums = new MultipartBody.Part[cards.size()];
//                                        for (int i=0;i<cards.size();i++) {
//                                            cardNums[i] = getSpecialBodyType(cards.get(i));
//                                        }
//                                        mPresenter.getProductOrderRequest(clothesIds);

                                            mPresenter.getProductOrderRequest(s);
                                        } else if (payDialog.getInputEditText().getEditableText().length() == 0) {
                                            ToastUtil.showShort("您还没有输入条码信息呢！");
                                        }else {
                                            ToastUtil.showShort("您输入的条码长度不对！");
                                        }
                                        payDialog.dismiss();
                                        return true;
                                    }
                                    return false;

                                }
                            })
                            .input("条码信息", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    LogUtils.loge("输入的是：" + input);
                                    if (input.toString().length() == 18) {
                                        AppConstant.AUTH_CODE = input.toString();

                                        for (int i = 0; i<clothesIdList.size(); i++) {
                                            clothesIdCount.add(productDetailsList.get(i).getClothesIdCounts());
                                        }

                                        List<PayOrderWithMultipartBean> data = new ArrayList<>();
                                        for (int i = 0; i<clothesIdList.size(); i++) {
                                            PayOrderWithMultipartBean bean = new PayOrderWithMultipartBean(clothesIdList.get(i), clothesIdCount.get(i),clothesIdWithCards.get(i));
                                            data.add(bean);
                                        }
                                        String s = (new Gson()).toJson(data);
                                        LogUtils.loge(s);

                                        //单纯的clothesIds数组
//                                        MultipartBody.Part[] clothesIds = new MultipartBody.Part[clothesIdList.size()];
//                                        for (int i=0;i<clothesIdList.size();i++) {
//                                            clothesIds[i] = getSpecialBodyType(clothesIdList.get(i));
//                                        }
//                                        mPresenter.getProductOrderRequest(clothesIds);

                                        mPresenter.getProductOrderRequest(s);
                                    } else if (input.toString().length() == 0) {
                                        ToastUtil.showShort("您还没有输入条码信息呢！");
                                    } else if (input.toString().length() >= 36) {
                                        AppConstant.AUTH_CODE = input.toString().substring(0,18);

                                        for (int i = 0; i<clothesIdList.size(); i++) {
                                            clothesIdCount.add(productDetailsList.get(i).getClothesIdCounts());
                                        }

                                        List<PayOrderWithMultipartBean> data = new ArrayList<>();
                                        for (int i = 0; i<clothesIdList.size(); i++) {
                                            PayOrderWithMultipartBean bean = new PayOrderWithMultipartBean(clothesIdList.get(i), clothesIdCount.get(i), clothesIdWithCards.get(i));
                                            data.add(bean);
                                        }
                                        String s = (new Gson()).toJson(data);
                                        LogUtils.loge(s);

                                        //单纯的clothesIds数组
//                                        MultipartBody.Part[] clothesIds = new MultipartBody.Part[clothesIdList.size()];
//                                        for (int i=0;i<clothesIdList.size();i++) {
//                                            clothesIds[i] = getSpecialBodyType(clothesIdList.get(i));
//                                        }
//                                        mPresenter.getProductOrderRequest(clothesIds);

                                        mPresenter.getProductOrderRequest(s);
                                    }else {
                                        ToastUtil.showShort("您输入的条码长度不对！");
                                    }

                                }
                            })
                            .negativeText("取消")
                            .positiveColor(getResources().getColor(R.color.main_blue))
                            .show();
                }else {
                    ToastUtil.showShort("请先确认金额");
                }
            }else {
                ToastUtil.showShort("请先添加样衣");
            }


        });

        //模拟刷卡的按钮
        addGoods.setOnClickListener(v ->
                mPresenter.getProductDetailsRequest("3519171754")
        );

        //清除所有商品
        goods_clear.setOnClickListener(v -> {
            new MaterialDialog.Builder(this)
                    .title("确认清空当前所有的商品吗")
                    .positiveText("确定")
                    .onPositive((d, i) -> {
                        productDetailsList = new ArrayList<>();
                        initProductDetails();
                        goodsKindCounts.setText("");
                        factPrice = 0;
                        finalPrice = 0;
                        goodsTotalsFee.setText("");
                        receivable.setText("");
                        final_fact.setText("0.0");
                        clothesIdList.clear();
                        clothesIdWithCards.clear();
                        cardsList.clear();
                    })
                    .negativeColor(getResources().getColor(R.color.red))
                    .negativeText("点错了")
                    .show();
        });

        //挂单
        register_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDetails = new ArrayList<>();
                registerDetails = productDetailsList;
                if (registerDetails.size() > 0) {
                    registerDialog = new MaterialDialog.Builder(MainActivity.this)
                            .title("请输入备注信息")
                            .widgetColor(Color.BLUE)//输入框光标的颜色
//                            .inputType(InputType.TYPE_CLASS_PHONE)//可以输入的类型-电话号码
                            //前2个一个是hint一个是预输入的文字
                            .input("挂单用户名", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    if (group.getGroupItems() == null) {
                                        group.setGroupItems(groupList);
                                    }
                                    group.getGroupItems().add(input.toString());
                                    LogUtils.loge("registerDialog", "输入的是：" + input);
                                }
                            })
                            .onPositive((d, i) -> {
                                registerCount = registerCount + 1;
                                notificationButton.setNotificationNumber(registerCount);
                                if (child.getMap() == null) {
                                    child.setMap(childList);
                                }
                                child.getMap().add(registerDetails);

                                ToastUtil.showShort("挂单成功");
                                productDetailsList = new ArrayList<>();//需要重新建立一个集合来存放新的数据
                                initProductDetails();
                                goodsKindCounts.setText("");
                                factPrice = 0;
                                finalPrice = 0;
                                goodsTotalsFee.setText("");
                                receivable.setText("");
                                final_fact.setText("0.0");

                                clothesIdList.clear();

                                bundle.putSerializable(AppConstant.SEND_REGISTER_DETAILS_WITH_CHILD, child);
                                bundle.putSerializable(AppConstant.SEND_REGISTER_DETAILS_WITH_GROUP, group);

                            })
                            .positiveText("确定")
                            .negativeColor(getResources().getColor(R.color.red))
                            .negativeText("点错了")
                            .show();

                } else {
                    ToastUtil.showShort("请先添加商品");
                }
            }
        });

        //挂单详情按钮点击
        notificationButton.setOnClickListener(v -> {
            if (registerCount >= 1) {
                Intent intent = new Intent(MainActivity.this, RegisterDetalisActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                ToastUtil.showShort("暂无挂单信息");
            }
        });

        //二维码
        scan_add_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SAMPLE);
//                 mPresenter.getProductDetailsWithScanRequest("http://weixin.qq.com/q/02gJC4lIIAdW210000g07x");
            }
        });

        RxTextView.textChanges(final_fact)
                .debounce(100, TimeUnit.MILLISECONDS)
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
                .debounce(100, TimeUnit.MILLISECONDS)
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

        RxTextView.textChanges(productCode)
                .debounce(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        if (!TextUtils.isEmpty(productCode.getEditableText())) {
                            mPresenter.getProductDetailsWithShopRequest(productCode.getEditableText().toString());
                            //关闭软键盘
                            ((InputMethodManager)getSystemService(mContext.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                });

        overPlus.setOnClickListener(v -> {
            new MaterialDialog.Builder(MainActivity.this)
                    .title("库存查询")
                    .widgetColor(Color.BLUE)//输入框光标的颜色
                    //前2个一个是hint一个是预输入的文字
                    .input("请输入商品编号", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            if (input.toString().length() > 0) {
                                mPresenter.getInventoryAmountsRequest(input.toString());
                            }else {
                                ToastUtil.showShort("你还没有输入查询的内容呢");
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
        return MultipartBody.Part.createFormData("card_nums", clothesIds);
    }

    private void showPopupWindow() {
        popupWindow = new BasePopupWindow(this);
//        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(400);
//        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(400);
        popupWindow.setContentView(pop);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        popupWindow.showAtLocation(rl_main, Gravity.CENTER, 0, 0);
    }

    private void initCalculator() {
        calculatorAdapter = new CalculatorAdapter(mContext, num, type);
        recycleView.setAdapter(calculatorAdapter);
        recycleView.setLayoutManager(new GridLayoutManager(mContext, 3, StaggeredGridLayoutManager.VERTICAL, false));
//         recycleView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
//         recycleView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        calculatorAdapter.setOnItemClickListener((position, keyTypt) -> {
            String actVlaue = num.get(position);
            String editText = getAmount.getText().toString();
            if (keyTypt.equals("num")) {
                if (editText.lastIndexOf(".") != -1 && editText.lastIndexOf(".") < editText.length() - 2) {
                    ToastUtil.showShort("只能输入小数点后两位");
                } else {
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
                if (keyTypt.equals("←") && !editText.equals("0")) {
                    if (editText.length() > 1) {
                        getAmount.setText(editText.substring(0, editText.length() - 1));
                    } else {
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
                    mPresenter.getProductDetailsRequest(num);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SAMPLE) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                switch (resultCode) {
                    case SCAN_HINT:
                        if (result != null) {
                            LogUtils.loge("二维码解析====" + result);
                            if (result.contains("http")) {
//                                mPresenter.getProductDetailsWithScanRequest("http://weixin.qq.com/q/02gJC4lIIAdW210000g07x");
                            } else {
//                                mPresenter.getProductDetailsWithScanRequest("http://weixin.qq.com/q/02gJC4lIIAdW210000g07x");
                            }
                        } else {
                            ToastUtil.showShort(getString(R.string.scan_qrcode_failed));
                        }
                        break;
                    case CODE_HINT:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //返回根据刷卡获取的成衣情况
    @Override
    public void returnGetProductDetails(ProductDetails productDetails,String num) {
        flag = true;
        //防止重复添加同一件样衣
        for (String clothesIds : clothesIdList) {
            if (clothesIds.equals(productDetails.get_id())) {
                haveClothesIds = true;
            }
        }
        //防止重复添加同一张卡
        for (String card : cardsList) {
            if (card.equals(num)) {
                haveCard = true;
            }
        }

        if (!haveClothesIds) {
            ToastUtil.showShort("OK");
            productDetails.setClothesIdCounts(1);
            productDetailsList.add(productDetails);
            productAdapter.notifyDataSetChanged();

            goodsKindCounts.setText(String.valueOf(productDetailsList.size()));
            factPrice = factPrice + productDetails.getRetailPrice();
            goodsTotalsFee.setText(String.valueOf(factPrice));
            finalPrice = factPrice * associatorDiscount / 10;
            receivable.setText(String.valueOf(finalPrice));
            final_fact.setText(String.valueOf(finalPrice));

//        //添加clothesId给集合，上传clothesId位数组时需要
//        ClothesIdBean clothesIdBean = new ClothesIdBean();
//        clothesIdBean.setClothes_ids(productDetails.get_id());
//        clothesIdList.add(clothesIdBean);
            clothesIdList.add(productDetails.get_id());
            cardsList.add(num);
            List<String> cards = new ArrayList<>();
            cards.add(num);
            clothesIdWithCards.add(cards);
//            clothesIdWithCards.get(clothesIdList.indexOf(productDetails.get_id())).add(num);
        }else {
            if (haveCard) {
                ToastUtil.showShort("该卡绑定的样衣已经再列表中了");
            }else {
                int counts = productDetailsList.get(clothesIdList.indexOf(productDetails.get_id())).getClothesIdCounts();
                productDetailsList.get(clothesIdList.indexOf(productDetails.get_id())).setClothesIdCounts(counts+1);
                productAdapter.notifyDataSetChanged();
                ToastUtil.showShort("该样衣已在列表中"+clothesIdList.indexOf(productDetails.get_id()));
                factPrice = factPrice + productDetails.getRetailPrice();
                goodsTotalsFee.setText(String.valueOf(factPrice));
                finalPrice = factPrice * associatorDiscount / 10;
                receivable.setText(String.valueOf(finalPrice));
                final_fact.setText(String.valueOf(finalPrice));
                cardsList.add(num);
                clothesIdWithCards.get(clothesIdList.indexOf(productDetails.get_id())).add(num);
            }
        }

        haveCard = false;
        haveClothesIds = false;

    }

    //fixme 为了与卡号一对一，暂时删除了根据商品编号搜寻功能
    //返回根据商品编号获取的成衣情况
    @Override
    public void returnGetProductDetailsWithShop(ProductDetails productDetails) {
        flag = true;
        //防止重复添加同一件样衣
        for (String clothesIds : clothesIdList) {
            if (clothesIds.equals(productDetails.get_id())) {
                haveClothesIds = true;
            }
        }

        if (!haveClothesIds) {
            ToastUtil.showShort("OK");
            productDetails.setClothesIdCounts(1);
            productDetailsList.add(productDetails);
            productAdapter.notifyDataSetChanged();

            goodsKindCounts.setText(String.valueOf(productDetailsList.size()));
            factPrice = factPrice + productDetails.getRetailPrice();
            goodsTotalsFee.setText(String.valueOf(factPrice));
            finalPrice = factPrice * associatorDiscount / 10;
            receivable.setText(String.valueOf(finalPrice));
            final_fact.setText(String.valueOf(finalPrice));

//        //添加clothesId给集合，上传clothesId位数组时需要
//        ClothesIdBean clothesIdBean = new ClothesIdBean();
//        clothesIdBean.setClothes_ids(productDetails.get_id());
//        clothesIdList.add(clothesIdBean);
            clothesIdList.add(productDetails.get_id());

            productCode.setText("");
        }else {

            int counts = productDetailsList.get(clothesIdList.indexOf(productDetails.get_id())).getClothesIdCounts();
            productDetailsList.get(clothesIdList.indexOf(productDetails.get_id())).setClothesIdCounts(counts+1);
            productAdapter.notifyDataSetChanged();
            ToastUtil.showShort("该样衣已在列表中"+clothesIdList.indexOf(productDetails.get_id()));
            factPrice = factPrice + productDetails.getRetailPrice();
            goodsTotalsFee.setText(String.valueOf(factPrice));
            finalPrice = factPrice * associatorDiscount / 10;
            receivable.setText(String.valueOf(finalPrice));
            final_fact.setText(String.valueOf(finalPrice));

            productCode.setText("");
        }

        haveClothesIds = false;

    }

    //下单
    @Override
    public void returnGetProductOrder(OrderData orderData) {
        AppConstant.ORDER_ID = orderData.get_id();
        AppConstant.TOTAL_FEE = orderData.getTotal_fee();
        if (AppConstant.ORDER_ID != "" && AppConstant.AUTH_CODE != "") {
            mPresenter.getPayResultInfoRequest(AppConstant.ORDER_ID, AppConstant.AUTH_CODE);
            AppConstant.AUTH_CODE = "";
            getAmount.setText("0");
        }else {
            ToastUtil.showShort("下单失败了");
        }
    }

    //微信支付
    @Override
    public void returnGetPayResultInfo(HttpResponse httpResponse) {
//        ToastUtil.showShort("支付成功");
        productDetailsList.clear();
        clothesIdList.clear();
        productAdapter.notifyDataSetChanged();

        goodsKindCounts.setText("");
        factPrice = 0;
        finalPrice = 0;
        goodsTotalsFee.setText("");
        receivable.setText("");
        final_fact.setText("0.0");

        clothesIdWithCards.clear();
        cardsList.clear();
    }

    //库存查询返回结果
    @Override
    public void returnGetInventoryAmounts(InventoryData inventoryData) {
//        showPopupWindow();//如果要显示具体详情的话

        Toast toast = Toast.makeText(this,
                "当前门店剩下的库存数量："+String.valueOf(inventoryData.getInventory()), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
        //用户信息的token过期时
        if (msg == "token过期") {
            SPUtils.setSharedStringData(AppApplication.getAppContext(),AppConstant.TOKEN,"");
            AppManager.getAppManager().finishAllActivity();
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
            ToastUtil.showShort("用户信息已经过期,请重新登录");
            return;
        }
        ToastUtil.showShort(msg);
        //fixme Specific
        productCode.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        flag = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.loge("onSaveInstanceState1");
        if (bundle != null && registerCount != 0) {
            outState.putBundle(AppConstant.REGISTER_DETAILS_WITH_SAVEINSTANCESTATE, bundle);
            outState.putInt(AppConstant.REGISTER_MSG_WITH_SAVEINSTANCESTATE, registerCount);

            ACache.get(AppApplication.getAppContext()).put(AppConstant.ACACHE_REGISTER_DETAILS_WITH_GROUP, group);
            ACache.get(AppApplication.getAppContext()).put(AppConstant.ACACHE_REGISTER_DETAILS_WITH_CHILD, child);
            ACache.get(AppApplication.getAppContext()).put(AppConstant.ACACHE_REGISTER_DETAILS_WITH_MSG, String.valueOf(registerCount));
            LogUtils.loge("onSaveInstanceState2");
        }
    }
}
