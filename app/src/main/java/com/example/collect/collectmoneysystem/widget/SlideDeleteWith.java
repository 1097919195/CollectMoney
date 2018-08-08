package com.example.collect.collectmoneysystem.widget;

import android.content.Context;
import android.support.v13.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jaydenxiao.common.commonutils.LogUtils;

/**
 * Created by Administrator on 2018/6/8 0008.
 */

public class SlideDeleteWith extends ViewGroup {
    private View mContent; // 内容部分
    private View mDelete;  // 删除部分
    private View mMinus;  // 减部分
    private View mPlus;  // 加部分
    private ViewDragHelper viewDragHelper;
    private int mContentWidth;
    private int mContentHeight;
    private int mDeleteWidth;
    private int mDeleteHeight;
    private int mMinusWidth;
    private int mMinusHeight;
    private int mPlusWidth;
    private int mPlusHeight;

    int mPlusLeft;//标记判断down下的时候是不是展开状态

    public SlideDeleteWith(Context context) {
        super(context);
    }

    public SlideDeleteWith(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideDeleteWith(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private OnSlideDeleteListener onSlideDeleteListener;

    public void setOnSlideDeleteListener(OnSlideDeleteListener onSlideDeleteListener) {
        this.onSlideDeleteListener = onSlideDeleteListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContent = getChildAt(0);
        mDelete = getChildAt(1);
        mMinus = getChildAt(2);
        mPlus = getChildAt(3);
        //public static ViewDragHelper create(ViewGroup forParent, Callback cb)
        viewDragHelper = ViewDragHelper.create(this, new MyDrawHelper());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 这跟mContent的父亲的大小有关，父亲是宽填充父窗体，高度是和孩子一样是60dp
        mContent.measure(widthMeasureSpec, heightMeasureSpec); // 测量内容部分的大小

        LayoutParams layoutParams = mDelete.getLayoutParams();
        int deleteWidth = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
        int deleteHeight = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
        // 这个参数就需要指定为精确大小
        mDelete.measure(deleteWidth, deleteHeight); // 测量删除部分的大小

        LayoutParams paramMinus = mMinus.getLayoutParams();
        int minusWidth = MeasureSpec.makeMeasureSpec(paramMinus.width, MeasureSpec.EXACTLY);
        int minusHeight = MeasureSpec.makeMeasureSpec(paramMinus.height, MeasureSpec.EXACTLY);
        // 这个参数就需要指定为精确大小
        mMinus.measure(minusWidth, minusHeight); // 测量删除部分的大小

        LayoutParams paramPlus = mPlus.getLayoutParams();
        int plusWidth = MeasureSpec.makeMeasureSpec(paramPlus.width, MeasureSpec.EXACTLY);
        int plusHeight = MeasureSpec.makeMeasureSpec(paramPlus.height, MeasureSpec.EXACTLY);
        // 这个参数就需要指定为精确大小
        mPlus.measure(plusWidth, plusHeight); // 测量删除部分的大小

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mContentWidth = mContent.getMeasuredWidth();
        mContentHeight = mContent.getMeasuredHeight();
        mContent.layout(0, 0, mContentWidth, mContentHeight); // 摆放内容部分的位置

        mDeleteWidth = mDelete.getMeasuredWidth();
        mDeleteHeight = mDelete.getMeasuredHeight();
        mDelete.layout(mContentWidth, 0,
                mContentWidth + mDeleteWidth, mContentHeight); // 摆放删除部分的位置

        mMinusWidth = mMinus.getMeasuredWidth();
        mMinusHeight = mMinus.getMeasuredHeight();
        mMinus.layout(mContentWidth + mDeleteWidth, 0,
                mContentWidth + mDeleteWidth + mMinusWidth, mContentHeight); // 摆放减部分的位置

        mPlusWidth = mPlus.getMeasuredWidth();
        mPlusHeight = mPlus.getMeasuredHeight();
        mPlus.layout(mContentWidth + mDeleteWidth + mMinusWidth, 0,
                mContentWidth + mDeleteWidth + mMinusWidth + mPlusWidth, mContentHeight); // 摆放加部分的位置
    }

    class MyDrawHelper extends ViewDragHelper.Callback {
        /**
         * Touch的down事件会回调这个方法 tryCaptureView
         *
         * @return : ViewDragHelper是否继续分析处理 child的相关touch事件
         * @Child：指定要动的孩子 （哪个孩子需要动起来）
         * @pointerId: 点的标记
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            System.out.println("调用tryCaptureView");
            System.out.println("contentView : " + (mContent == child));
            mPlusLeft = mPlus.getLeft();
            return mContent == child || mDelete == child || mMinus == child || mPlus == child;
        }
        // Touch的move事件会回调这面这几个方法
        // clampViewPositionHorizontal
        // clampViewPositionVertical
        // onViewPositionChanged

        /**
         * 捕获了水平方向移动的位移数据
         *
         * @param child 移动的孩子View
         * @param left  父容器的左上角到孩子View的距离
         * @param dx    增量值，其实就是移动的孩子View的左上角距离控件（父亲）的距离，包含正负
         * @return 如何动
         * <p>
         * 调用完此方法，在android2.3以上就会动起来了，2.3以及以下是海动不了的
         * 2.3不兼容怎么办？没事，我们复写onViewPositionChanged就是为了解决这个问题的
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //Log.d("Slide", "增量值：   " + left);
            if (child == mContent) { // 解决内容部分左右拖动的越界问题
                if (left > 0) {
                    return 0;
                } else if (-left > mDeleteWidth + mMinusWidth + mPlusWidth) {
                    return -mDeleteWidth - mMinusWidth - mPlusWidth;
                }
            }
            if (mDelete == child) { // 解决删除部分左右拖动的越界问题
                if (left < mContentWidth - mDeleteWidth) {
                    return mContentWidth - mDeleteWidth;
                } else if (left > mContentWidth) {
                    return mContentWidth;
                }
            }
            if (mMinus == child) { // 解决删除部分左右拖动的越界问题
                if (left < mContentWidth - mDeleteWidth - mMinusWidth) {
                    return mContentWidth - mDeleteWidth - mMinusWidth;
                } else if (left > mContentWidth + mMinusWidth) {
                    return mContentWidth + mMinusWidth;
                }
            }
            if (mPlus == child) { // 解决删除部分左右拖动的越界问题
                if (left < mContentWidth - mDeleteWidth - mMinusWidth - mPlusWidth) {
                    return mContentWidth - mDeleteWidth - mMinusWidth - mPlusWidth;
                } else if (left > mContentWidth + mMinusWidth + mPlusWidth) {
                    return mContentWidth + mMinusWidth + mPlusWidth;
                }
            }
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return super.clampViewPositionVertical(child, top, dy);
        }

        /**
         * 当View的位置改变时的回调  这个方法的价值是结合clampViewPositionHorizontal或者clampViewPositionVertical
         *
         * @param changedView 哪个View的位置改变了
         * @param left        changedView的left
         * @param top         changedView的top
         * @param dx          x方向的上的增量值
         * @param dy          y方向上的增量值
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //super.onViewPositionChanged(changedView, left, top, dx, dy);
            invalidate();
            if (changedView == mContent) { // 如果移动的是mContent
                //我们移动mContent的实惠要相应的联动改变mDelete的位置
                // 怎么改变mDelete的位置，当然是mDelete的layout方法啦
                int tempDeleteLeft = mContentWidth + left;
                int tempMinusLeft = mContentWidth + left + mDeleteWidth;
                int tempPlusLeft = mContentWidth + left + mDeleteWidth + mMinusWidth;
                int tempDeleteRight = mContentWidth + left + mDeleteWidth;
                int tempMinusRight = mContentWidth + left + mDeleteWidth + mMinusWidth;
                int tempPlusRight = mContentWidth + left + mDeleteWidth + mMinusWidth + mPlusWidth;
                mDelete.layout(tempDeleteLeft, 0, tempDeleteRight, mDeleteHeight);
                mMinus.layout(tempMinusLeft, 0, tempMinusRight, mDeleteHeight);
                mPlus.layout(tempPlusLeft, 0, tempPlusRight, mDeleteHeight);
            }
            if (changedView == mDelete) { // 如果移动的是mContent
                //我们移动mContent的实惠要相应的联动改变mDelete的位置
                // 怎么改变mDelete的位置，当然是mDelete的layout方法啦
                int tempContentLeft = left - mContentWidth;
                int tempMinusLeft = mContentWidth + left + mDeleteWidth;
                int tempPlusLeft = mContentWidth + left + mDeleteWidth + mMinusWidth;
                int tempContentRight = left;
                int tempMinusRight = mContentWidth + left + mDeleteWidth + mMinusWidth;
                int tempPlusRight = mContentWidth + left + mDeleteWidth + mMinusWidth + mPlusWidth;
                mContent.layout(tempContentLeft, 0, tempContentRight, mDeleteHeight);
                mMinus.layout(tempMinusLeft, 0, tempMinusRight, mDeleteHeight);
                mPlus.layout(tempPlusLeft, 0, tempPlusRight, mDeleteHeight);
            }
            if (changedView == mMinus) { // 如果移动的是mContent
                //我们移动mContent的实惠要相应的联动改变mDelete的位置
                // 怎么改变mDelete的位置，当然是mDelete的layout方法啦
                int tempDeleteLeft = mContentWidth + left;
                int tempContentLeft = left - mContentWidth - mDeleteWidth;
                int tempPlusLeft = mContentWidth + left + mDeleteWidth + mMinusWidth;
                int tempDeleteRight = mContentWidth + left + mDeleteWidth;
                int tempContentRight = left;
                int tempPlusRight = mContentWidth + left + mDeleteWidth + mMinusWidth + mPlusWidth;
                mDelete.layout(tempDeleteLeft, 0, tempDeleteRight, mDeleteHeight);
                mContent.layout(tempContentLeft, 0, tempContentRight, mDeleteHeight);
                mPlus.layout(tempPlusLeft, 0, tempPlusRight, mDeleteHeight);
            }
            if (changedView == mPlus) { // 如果移动的是mContent
                //我们移动mContent的实惠要相应的联动改变mDelete的位置
                // 怎么改变mDelete的位置，当然是mDelete的layout方法啦
                int tempDeleteLeft = mContentWidth + left;
                int tempMinusLeft = mContentWidth + left + mDeleteWidth;
                int tempContentLeft = left - mContentWidth - mDeleteWidth - mMinusWidth;
                int tempDeleteRight = mContentWidth + left + mDeleteWidth;
                int tempMinusRight = mContentWidth + left + mDeleteWidth + mMinusWidth;
                int tempContentRight = left;
                mDelete.layout(tempDeleteLeft, 0, tempDeleteRight, mDeleteHeight);
                mMinus.layout(tempMinusLeft, 0, tempMinusRight, mDeleteHeight);
                mContent.layout(tempContentLeft, 0, tempContentRight, mDeleteHeight);
            }
        }

        /**
         * 相当于Touch的up的事件会回调onViewReleased这个方法
         *
         * @param releasedChild
         * @param xvel          x方向的速率
         * @param yvel          y方向的速率
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //super.onViewReleased(releasedChild, xvel, yvel);
            // 方法的参数里面没有left，那么我们就采用 getLeft()这个方法
            int mConLeft = mContent.getLeft();
            if (mPlusLeft == mContentWidth-mPlusWidth && -mConLeft < mDeleteWidth + mMinusWidth + mPlusWidth / 2) {
                isShowDelete(false);
                if (onSlideDeleteListener != null) {
                    onSlideDeleteListener.onClose(SlideDeleteWith.this); // 调用接口的关闭的方法
                }
            }else if (-mConLeft > mPlusWidth / 2) {  // mDelete展示起来
                isShowDelete(true);
                if (onSlideDeleteListener != null) {
                    onSlideDeleteListener.onOpen(SlideDeleteWith.this); // 调用接口打开的方法
                }
            } else {
                isShowDelete(false);
                if (onSlideDeleteListener != null) {
                    onSlideDeleteListener.onClose(SlideDeleteWith.this); // 调用接口的关闭的方法
                }
            }
//            super.onViewReleased(releasedChild, xvel, yvel);
        }
    }

    /**
     * 是否展示delete部分
     *
     * @param isShowDelete
     */
    public void isShowDelete(boolean isShowDelete) {
        if (isShowDelete) {
            //mContent.layout(-mDeleteWidth,0,mContentWidth-mDeleteWidth,mContentHeight);
            //mDelete.layout(mContentWidth-mDeleteWidth,0,mContentWidth,mDeleteHeight);
            //采用ViewDragHelper的 smoothSlideViewTo 方法让移动变得顺滑自然，不会太生硬
            //smoothSlideViewTo只是模拟了数据，但是不会真正的动起来，动起来需要调用 invalidate
            // 而 invalidate 通过调用draw()等方法之后最后还是还是会调用 computeScroll 这个方法
            // 所以，使用 smoothSlideViewTo 做过渡动画需要结合  invalidate方法 和 computeScroll方法
            // smoothSlideViewTo的动画执行时间没有暴露的参数可以设置，但是这个时间是google给我们经过大量计算给出合理时间

            viewDragHelper.smoothSlideViewTo(mDelete, -mDeleteWidth - mMinusWidth - mPlusWidth + mContentWidth, 0);
            viewDragHelper.smoothSlideViewTo(mMinus, -mMinusWidth - mPlusWidth + mContentWidth, 0);
            viewDragHelper.smoothSlideViewTo(mPlus, -mPlusWidth + mContentWidth, 0);
            viewDragHelper.smoothSlideViewTo(mContent, -mDeleteWidth - mMinusWidth - mPlusWidth, 0);
        } else {
            //mContent.layout(0,0,mContentWidth,mContentHeight);
            //mDelete.layout(mContentWidth, 0, mContentWidth + mDeleteWidth, mDeleteHeight);

            viewDragHelper.smoothSlideViewTo(mDelete, mContentWidth, 0);
            viewDragHelper.smoothSlideViewTo(mMinus, mContentWidth + mDeleteWidth, 0);
            viewDragHelper.smoothSlideViewTo(mPlus, mContentWidth + mDeleteWidth + mMinusWidth, 0);
            viewDragHelper.smoothSlideViewTo(mContent, 0, 0);
        }
        invalidate();
    }

    @Override
    public void computeScroll() {
        //super.computeScroll();
        // 把捕获的View适当的时间移动，其实也可以理解为 smoothSlideViewTo 的模拟过程还没完成
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);//刷新带有动画
            invalidate();
        }
        // 其实这个动画过渡的过程大概在怎么走呢？
        // 1、smoothSlideViewTo方法进行模拟数据，模拟后就就调用invalidate();
        // 2、invalidate()最终调用computeScroll，computeScroll做一次细微动画，
        //    computeScroll判断模拟数据是否彻底完成，还没完成会再次调用invalidate
        // 3、递归调用，知道数据noni完成。
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        /**Process a touch event received by the parent view. This method will dispatch callback events
         as needed before returning. The parent view's onTouchEvent implementation should call this. */
        viewDragHelper.processTouchEvent(event); // 使用ViewDragHelper必须复写onTouchEvent并调用这个方法
        return true; //消费这个touch
    }

    // SlideDlete的接口
    public interface OnSlideDeleteListener {
        void onOpen(SlideDeleteWith slideDelete);

        void onClose(SlideDeleteWith slideDelete);
    }
}
