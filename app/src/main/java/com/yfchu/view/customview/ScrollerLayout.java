package com.yfchu.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.yfchu.adapter.ScrollerAdapter;
import com.yfchu.utils.CommonUrl;
import com.yfchu.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * yfchu 2016/11/5.
 */
public class ScrollerLayout extends ViewGroup {

    private Context mContext;
    private ScrollerLayout contain;
    private List<View> layoutList = new ArrayList<>();
    private boolean isAdd = false;
    /**
     * 用于完成滚动操作的实例
     */
    private Scroller mScroller;

    /**
     * 判定为拖动的最小移动像素数
     */
    private int mTouchSlop;

    /**
     * 手机按下时的屏幕坐标
     */
    private float mXDown;

    /**
     * 手机当时所处的屏幕坐标
     */
    private float mXMove;

    /**
     * 上次触发ACTION_MOVE事件时的屏幕坐标
     */
    private float mXLastMove;

    /**
     * 界面可滚动的左边界
     */
    private int leftBorder;

    /**
     * 界面可滚动的右边界
     */
    private int rightBorder;

    /**
     * 当前页,上一页
     */
    private int targetIndex = 0, lastIndex = 1;

    /**
     * 左右页留出距离:相较xml的layout_marginLeft，越小露出的下一页越宽
     */
    private int pageShowPadding = 20;

    /**
     * 标准滑动翻页距离，快速滑动翻页距离
     */
    private int scollerNumber = 200;

    /**
     * 目标缩放值: 大和小
     */
    private float targetLargeScale = 1.1f, targetSmallScale = 1.0f, targetSmallScaleY = 0.95f;

    /**
     * 缩放临时变量
     */
    private float targetScale = 1.0f, lastScale = 1.1f, lastScaleY = 1.1f;

    /**
     * 缩放基数
     */
    private float scaleX = 0f, scaleY = 0f;

    /**
     * 快速滑动翻页 true为翻页，false不够快速。isPage是否翻页成功
     */
    private boolean isPage = false, pageEnd = false;

    /**
     * 滑动速度
     */
    private VelocityTracker mVelocityTracker;

    /**
     * Main的Handler传过来
     * */
    private Handler mHandler;

    public ScrollerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 第一步，创建Scroller的实例
        mContext = context;
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        // 获取TouchSlop值
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    public void setHandler(Handler m) {
        this.mHandler = m;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 为ScrollerLayout中的每一个子控件测量大小
            childView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 设置数据源
     */
    public void setAdapter(ScrollerAdapter adapter) {
        contain = (ScrollerLayout) this;
        contain.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            LinearLayout layout = null;
            try {
                layout = (LinearLayout) adapter.getView(i, layoutList.get(i), contain);
                isAdd = true;
            } catch (Exception e) {
                layout = (LinearLayout) adapter.getView(i, null, contain);
                isAdd = false;
            }
            contain.addView(layout);
            if (isAdd == false) {
                layoutList.add(layout);
            }
        }
        requestLayout();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                // 为ScrollerLayout中的每一个子控件在水平方向上进行布局
                childView.layout(i * childView.getMeasuredWidth() + CommonUtil.convertDpToPx(mContext, pageShowPadding), 0, (i + 1) * childView.getMeasuredWidth() - CommonUtil.convertDpToPx(mContext, pageShowPadding), childView.getMeasuredHeight());
                if (i == targetIndex) { //默认缩放第一个页面
                    childView.setScaleX(targetLargeScale);
                    childView.setScaleY(targetLargeScale);
                } else {
                    childView.setScaleX(targetSmallScale);
                    childView.setScaleY(targetSmallScaleY);
                }
                childView.setOnClickListener(null);//为非button添加点击事件
            }
            // 初始化左右边界值
            leftBorder = getChildAt(0).getLeft() - CommonUtil.convertDpToPx(mContext, pageShowPadding);
            rightBorder = getChildAt(getChildCount() - 1).getRight() + CommonUtil.convertDpToPx(mContext, pageShowPadding);
        //}
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                targetScale = targetSmallScale; //缩放变量初始化
                lastScale = targetLargeScale;
                lastScaleY = targetLargeScale;
                isPage = false;
                pageEnd = true;
                //根据最小像素设置缩放基数
                if (mTouchSlop < 80) {
                    scaleX = 0.006f;
                    scaleY = 0.007f;
                } else {
                    scaleX = 0.003f;
                    scaleY = 0.004f;
                }

                mXDown = ev.getRawX();
                mXLastMove = mXDown;
                mHandler.obtainMessage(MotionEvent.ACTION_DOWN, mXDown).sendToTarget();
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getRawX();
                float diff = Math.abs(mXMove - mXDown);
                mXLastMove = mXMove;
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > 1) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(event);
                mXMove = event.getRawX();
                int scrolledX = (int) (mXLastMove - mXMove);

                if (getScrollX() + scrolledX < leftBorder) {
                    scrollTo(leftBorder, 0);
                    pageEnd = false;
                    return true;
                } else if (getScrollX() + getWidth() + scrolledX > rightBorder) {
                    scrollTo(rightBorder - getWidth(), 0);
                    pageEnd = false;
                    return true;
                }
                ScaleNarrow(targetIndex);
                scrollBy(scrolledX, 0);
                mHandler.obtainMessage(MotionEvent.ACTION_MOVE, mXMove).sendToTarget();
                mXLastMove = mXMove;
                break;
            case MotionEvent.ACTION_UP:
                // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
//                int targetIndex = (int) ((getScrollX() + getWidth() / 2) / getWidth());
//                int dx = targetIndex * getWidth() - getScrollX();
//                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
//                mScroller.startScroll(getScrollX(), 0, dx, 0);
//                invalidate();

                mVelocityTracker.computeCurrentVelocity(1000);
                if (mXDown - mXLastMove > CommonUtil.convertDpToPx(mContext, scollerNumber)) {
                    pageAdd(0);
                } else if (mXDown - mXLastMove < CommonUtil.convertDpToPx(mContext, -scollerNumber)) {
                    pageAdd(1);
                } else if (Math.abs(mVelocityTracker.getXVelocity()) > 200 && pageEnd == true) {
                    scaleX = 0.02f;
                    scaleY = 0.03f;
                    if (mXDown - mXLastMove > 0) {
                        pageAdd(0);
                    } else {
                        pageAdd(1);
                    }
                }
                if (targetIndex >= (float) rightBorder / getWidth())
                    targetIndex = rightBorder / getWidth() - 1;
                else if (targetIndex < 0)
                    targetIndex = 0;
                int dx = targetIndex * getWidth() - getScrollX();

                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
                mScroller.startScroll(getScrollX(), 0, dx, 0);
                invalidate();
                if (scaleX == 0.02f)
                    mHandler.obtainMessage(CommonUrl.FASTMOVE, MotionEvent.ACTION_UP).sendToTarget();
                else
                    mHandler.obtainMessage(MotionEvent.ACTION_UP, MotionEvent.ACTION_UP).sendToTarget();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 上一页、下一页
     */
    private void pageAdd(int add) {
        lastIndex = targetIndex;
        if (add == 0) {
            targetIndex++;
            isPage = true;
        } else {
            targetIndex--;
            isPage = true;
        }
    }

    /**
     * 放大
     */
    private void ScaleEnlarge() {
        View childView = getChildAt(targetIndex);
        if (childView.getScaleY() < targetLargeScale) {
            targetScale += 0.01f;
            if (targetScale > targetLargeScale) {
                targetScale = targetLargeScale;
            }
//            Log.i("yfchu放大", targetScale + "");
            childView.setScaleX(targetScale);
            childView.setScaleY(targetScale);
        }
    }

    /**
     * 缩小
     */
    private void ScaleNarrow(int index) {
        View childView = getChildAt(index);
        if (childView.getScaleY() > targetSmallScaleY) {
            lastScale -= scaleX;
            lastScaleY -= scaleY;
            if (lastScale < targetSmallScale)
                lastScale = targetSmallScale;
            if (lastScaleY < targetSmallScaleY)
                lastScaleY = targetSmallScaleY;
//            Log.i("yfchu缩小X", lastScale + "");
//            Log.i("yfchu缩小Y", lastScaleY + "");
            childView.setScaleX(lastScale);
            childView.setScaleY(lastScaleY);
        }
    }

    /**
     * 点击horizonta时更新Scrolllayout
     */
    public void setMovePage(int target) {
        scaleX = 0.02f;
        scaleY = 0.03f;
        if (target >= (float) rightBorder / getWidth())
            target = rightBorder / getWidth() - 1;
        else if (target < 0)
            target = 0;
        isPage = true;
        lastIndex = targetIndex;
        targetIndex = target;
        int dx = target * getWidth() - getScrollX();
        // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
        mScroller.startScroll(getScrollX(), 0, dx, 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            if (isPage == true)
                ScaleNarrow(lastIndex);
            ScaleEnlarge();
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    public float getTouchSlop() {
        return mTouchSlop;
    }
}
