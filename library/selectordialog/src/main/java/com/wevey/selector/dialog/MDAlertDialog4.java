package com.wevey.selector.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.weavey.utils.ScreenSizeUtils;

/**
 * 弹出框2
 * Created by Weavey on 2016/9/4.
 */
public class MDAlertDialog4 implements View.OnClickListener {

    private Dialog mDialog;
    private View mDialogView;
    private TextView mTitle;
    private TextView mContent;
    private Button mLeftBtn;
    private Button mRightBtn;
    private LinearLayout mLlbox;
    private LinearLayout mLlbox1;
    private ImageView mIv;
    private Button mCenterBtn;
    private ProgressBar mPb;
    private static Context mContext;
    private Builder mBuilder;

    public MDAlertDialog4(Builder builder) {

        mBuilder = builder;
        mDialog = new Dialog(mContext, R.style.MyDialogStyle);
        mDialogView = View.inflate(mContext, R.layout.widget_md_dialog3, null);
        mTitle = (TextView) mDialogView.findViewById(R.id.md_dialog_title);
        mContent = (TextView) mDialogView.findViewById(R.id.md_dialog_content);
        mLeftBtn = (Button) mDialogView.findViewById(R.id.md_dialog3_leftbtn);
        mRightBtn = (Button) mDialogView.findViewById(R.id.md_dialog3_rightbtn);
        mLlbox = (LinearLayout) mDialogView.findViewById(R.id.md_dialog3_llbox);
        mLlbox1 = (LinearLayout) mDialogView.findViewById(R.id.md_dialog3_llbox2);
        mIv = (ImageView) mDialogView.findViewById(R.id.md_dialog3_iv);
        mCenterBtn = (Button) mDialogView.findViewById(R.id.md_dialog3_centerbtn);
        mPb = (ProgressBar) mDialogView.findViewById(R.id.pb_dialog);
        mDialogView.setMinimumHeight((int) (ScreenSizeUtils.getInstance(mContext).getScreenHeight
                () * builder.getHeight()));
        mDialog.setContentView(mDialogView);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode== KeyEvent.KEYCODE_BACK)
                {
                    return true;
                }
                else
                {
                    return false; //默认返回 false
                }
            }
        });
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(mContext).getScreenWidth() * builder.getWidth());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        initDialog();

    }

    private void initDialog() {

        mDialog.setCanceledOnTouchOutside(mBuilder.isTouchOutside());

        if (mBuilder.getTitleVisible()) {

            mTitle.setVisibility(View.VISIBLE);
        } else {

            mTitle.setVisibility(View.GONE);
        }

        mTitle.setText(mBuilder.getTitleText());
        mTitle.setTextColor(mBuilder.getTitleTextColor());
        mTitle.setTextSize(mBuilder.getTitleTextSize());
        mContent.setText(mBuilder.getContentText());
        mContent.setTextColor(mBuilder.getContentTextColor());
        mContent.setTextSize(mBuilder.getContentTextSize());
        mLeftBtn.setText(mBuilder.getLeftButtonText());
        mLeftBtn.setTextColor(mBuilder.getLeftButtonTextColor());
        mLeftBtn.setTextSize(mBuilder.getButtonTextSize());
        mRightBtn.setText(mBuilder.getRightButtonText());
        mRightBtn.setTextColor(mBuilder.getRightButtonTextColor());
        mRightBtn.setTextSize(mBuilder.getButtonTextSize());
        mCenterBtn.setText(mBuilder.getCenterButtonText());
        if (mBuilder.isPbVisible()) {

            mPb.setVisibility(View.VISIBLE);
        } else {

            mPb.setVisibility(View.GONE);
        }
        if (mBuilder.isLlboxVisible()) {

            mLlbox.setVisibility(View.VISIBLE);
        } else {

            mLlbox.setVisibility(View.GONE);
        }
        if (mBuilder.isLlbox2Visible()) {

            mLlbox1.setVisibility(View.VISIBLE);
        } else {

            mLlbox1.setVisibility(View.GONE);
        }
        if (mBuilder.isIvVisible()) {

            mIv.setVisibility(View.VISIBLE);
        } else {

            mIv.setVisibility(View.GONE);
        }
        mLeftBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
        mCenterBtn.setOnClickListener(this);
    }

    public void show() {

        mDialog.show();
    }

    public void dismiss() {

        mDialog.dismiss();
    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.md_dialog3_leftbtn && mBuilder.getListener() != null) {

            mBuilder.getListener().clickLeftButton(mLeftBtn);
            return;
        }

        if (i == R.id.md_dialog3_rightbtn && mBuilder.getListener() != null) {

            mBuilder.getListener().clickRightButton(mRightBtn);
            return;
        }

        if (i == R.id.md_dialog3_centerbtn && mBuilder.getListener() != null) {

            mBuilder.getListener().clickCenterButton(mCenterBtn);
            return;
        }
//        if(i==R.id.md_dialog_exit)
//        {
//            mDialog.dismiss();
//        }

    }

    public static class Builder {

        private String titleText;
        private int titleTextColor;
        private int titleTextSize;
        private String contentText;
        private int contentTextColor;
        private int contentTextSize;
        private String leftButtonText;
        private int leftButtonTextColor;
        private String rightButtonText;
        private int rightButtonTextColor;
        private int buttonTextSize;
        private boolean isTitleVisible;
        private boolean isTouchOutside;
        private float height;
        private float width;
        private String stringtxt1;
        private String stringtxt2;
        private String stringtxt3;
        private DialogOnClickListener1 listener;
        private String centerButtonText;
        private boolean isLlboxVisible;
        private boolean isLlbox2Visible;
        private boolean isIvVisible;
        private boolean isPbVisible;
        private int ivImageResource;


        public Builder(Context context) {

            mContext = context;
            titleText = "提示";
            titleTextColor = ContextCompat.getColor(mContext, R.color.black_light);
            contentText = "";
            contentTextColor = ContextCompat.getColor(mContext, R.color.black_light);
            leftButtonText = "确定";
            leftButtonTextColor = ContextCompat.getColor(mContext, R.color.black_light);
            rightButtonText = "取消";
            rightButtonTextColor = ContextCompat.getColor(mContext, R.color.black_light);
            listener = null;
            isTitleVisible = true;
            isTouchOutside = true;
            isIvVisible = true;
            isLlbox2Visible = false;
            isLlboxVisible = true;
            centerButtonText = "完成";
            height = 0.21f;
            width = 0.73f;
            titleTextSize = 16;
            contentTextSize = 14;
            buttonTextSize = 14;
            isPbVisible = false;

        }


        public boolean isPbVisible() {
            return isPbVisible;
        }

        public Builder setPbVisible(boolean pbVisible) {
            isPbVisible = pbVisible;
            return this;
        }

        public int getIvImageResource() {
            return ivImageResource;
        }

        public Builder setIvImageResource(int ivImageResource) {
            this.ivImageResource = ivImageResource;
            return this;
        }

        public String getCenterButtonText() {
            return centerButtonText;
        }

        public Builder setCenterButtonText(String centerButtonText) {
            this.centerButtonText = centerButtonText;
            return this;
        }

        public boolean isLlboxVisible() {
            return isLlboxVisible;
        }

        public Builder setLlboxVisible(boolean llboxVisible) {
            isLlboxVisible = llboxVisible;
            return this;
        }

        public boolean isLlbox2Visible() {
            return isLlbox2Visible;
        }

        public Builder setLlbox2Visible(boolean llbox2Visible) {
            isLlbox2Visible = llbox2Visible;
            return this;
        }

        public boolean isIvVisible() {
            return isIvVisible;
        }

        public Builder setIvVisible(boolean ivVisible) {
            isIvVisible = ivVisible;
            return this;
        }

        public String getTitleText() {
            return titleText;
        }

        public Builder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public int getTitleTextColor() {
            return titleTextColor;
        }

        public Builder setTitleTextColor(@ColorRes int titleTextColor) {
            this.titleTextColor = ContextCompat.getColor(mContext, titleTextColor);
            return this;
        }

        public String getContentText() {
            return contentText;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public int getContentTextColor() {
            return contentTextColor;
        }

        public Builder setContentTextColor(@ColorRes int contentTextColor) {
            this.contentTextColor = ContextCompat.getColor(mContext, contentTextColor);
            return this;
        }

        public String getLeftButtonText() {
            return leftButtonText;
        }

        public Builder setLeftButtonText(String leftButtonText) {
            this.leftButtonText = leftButtonText;
            return this;
        }

        public int getLeftButtonTextColor() {
            return leftButtonTextColor;
        }

        public Builder setLeftButtonTextColor(@ColorRes int leftButtonTextColor) {
            this.leftButtonTextColor = ContextCompat.getColor(mContext, leftButtonTextColor);
            return this;
        }

        public String getRightButtonText() {
            return rightButtonText;
        }

        public Builder setRightButtonText(String rightButtonText) {
            this.rightButtonText = rightButtonText;
            return this;
        }

        public int getRightButtonTextColor() {
            return rightButtonTextColor;
        }

        public Builder setRightButtonTextColor(@ColorRes int rightButtonTextColor) {
            this.rightButtonTextColor = ContextCompat.getColor(mContext, rightButtonTextColor);
            return this;
        }

        public boolean getTitleVisible() {
            return isTitleVisible;
        }

        public Builder setTitleVisible(boolean titleVisible) {
            isTitleVisible = titleVisible;
            return this;
        }

        public boolean isTouchOutside() {
            return isTouchOutside;
        }

        //设置点击外部区域不关闭
        public Builder setCanceledOnTouchOutside(boolean touchOutside) {
            isTouchOutside = touchOutside;
            return this;
        }

        public float getHeight() {
            return height;
        }

        public Builder setHeight(float height) {
            this.height = height;
            return this;
        }

        public float getWidth() {
            return width;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        public int getContentTextSize() {
            return contentTextSize;
        }

        public Builder setContentTextSize(int contentTextSize) {
            this.contentTextSize = contentTextSize;
            return this;
        }

        public int getTitleTextSize() {
            return titleTextSize;
        }

        public Builder setTitleTextSize(int titleTextSize) {
            this.titleTextSize = titleTextSize;
            return this;
        }

        public int getButtonTextSize() {
            return buttonTextSize;
        }

        public Builder setButtonTextSize(int buttonTextSize) {
            this.buttonTextSize = buttonTextSize;
            return this;
        }

        public DialogOnClickListener1 getListener() {
            return listener;
        }

        public Builder setOnclickListener(DialogOnClickListener1 listener) {
            this.listener = listener;
            return this;
        }

        public MDAlertDialog4 build() {

            return new MDAlertDialog4(this);
        }
    }


}
