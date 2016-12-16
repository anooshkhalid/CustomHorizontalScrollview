package com.yfchu.view.customview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.yfchu.adapter.HorizontalAdapter;
import com.yfchu.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * yfchu 2016/11/5.
 */
public class HorizontalView extends HorizontalScrollView {

    private Context mContext;
    private Scroller mScroller;
    private Handler mHandler;

    private LinearLayout contain;
    private TabItem lastTextView;
    private List<TabItem> textViewList = new ArrayList<>();
    private List<View> layoutList = new ArrayList<>();

    private boolean isAdd = false;
    private int currX;
    private int mViewWidth;
    private int startScroll, endScroll;

    /**
     * 点击滚动ScrollView
     * */
    public static final int SCROLL_ROLL = 0x01;

    /**
     * 发送宽度数据
     * */
    public static final int SETDATA = 0x02;

    public int getmViewWidth() {
        return mViewWidth;
    }

    public int getmTabItemWidth() {
        return textViewList.get(0).getmViewWidth();
    }

    public void setStartScroll(int startScroll, int endScroll) {
        this.startScroll = startScroll;
        this.endScroll = endScroll;
    }

    public HorizontalView(Context context) {
        super(context);
        mContext = context;
    }

    public HorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScroller = new Scroller(mContext);
    }

    public HorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setHandler(Handler m) {
        this.mHandler = m;
    }

    public void setLastView(TabItem v) {
        lastTextView = v;
    }

    public List<TabItem> getTextViewList() {
        return textViewList;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mHandler.obtainMessage(SETDATA, mViewWidth, getmTabItemWidth()).sendToTarget();
    }

    /**
     * 设置数据源
     */
    public void setAdapter(HorizontalAdapter adapter) {
        contain = (LinearLayout) getChildAt(0);
        contain.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            LinearLayout layout = null;
            try {
                layout = (LinearLayout) adapter.getView(i, layoutList.get(i), contain, lastTextView.getId());
                isAdd = true;
            } catch (Exception e) {
                layout = (LinearLayout) adapter.getView(i, null, contain, 0);
                isAdd = false;
                if (i == 0)
                    lastTextView = (TabItem) layout.getChildAt(0);
            }
            contain.addView(layout);
            if (i + 1 != adapter.getCount()) {
                TextView line = new TextView(mContext);
                line.setWidth(1);
                line.setHeight(CommonUtil.convertDpToPx(mContext, 20));
                line.setGravity(Gravity.CENTER_VERTICAL);
                line.setBackgroundColor(mContext.getResources().getColor(R.color.line));
                contain.addView(line);
            }
            if (isAdd == false) {
                layout.getChildAt(0).setOnClickListener(new OnClickListener());
                textViewList.add((TabItem) layout.getChildAt(0));
                layoutList.add(layout);
            }
        }
        requestLayout();
    }

    public class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (lastTextView.getId() == v.getId())
                return;
            ValueAnimator anim = ValueAnimator.ofFloat(1.1f, 0.95f);
            anim.setDuration(200);
            anim.start();
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if ((Float) animation.getAnimatedValue() >= 1.0f)
                        lastTextView.setTabAlpha(((Float) animation.getAnimatedValue() - 1.0f) * 10);
                    lastTextView.setScaleX((Float) animation.getAnimatedValue());
                    lastTextView.setScaleY((Float) animation.getAnimatedValue());
                }
            });
            ValueAnimator anim1 = ValueAnimator.ofFloat(0.95f, 1.1f);
            anim1.setDuration(200);
            anim1.start();
            anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if ((Float) animation.getAnimatedValue() >= 1.0f)
                        ((TabItem) v).setTabAlpha(((Float) animation.getAnimatedValue() - 1.0f) * 10);
                    ((TabItem) v).setScaleX((Float) animation.getAnimatedValue());
                    ((TabItem) v).setScaleY((Float) animation.getAnimatedValue());
                }
            });
            anim1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mHandler != null) {
                        mHandler.obtainMessage(SCROLL_ROLL, v.getId(), 0).sendToTarget();
                        Log.i("yfchu",getScrollX()+"");
                        if (lastTextView.getId() < ((TabItem) v).getId()
                                && ((TabItem) v).getId() - lastTextView.getId() == 1 && ((TabItem) v).getId() >= startScroll) {
                            if (((TabItem) v).getId() == startScroll)
                                ScrollBy(0, getmTabItemWidth());
                            else
                                ScrollBy(getmTabItemWidth());
                        } else if (lastTextView.getId() > ((TabItem) v).getId()
                                && lastTextView.getId() - ((TabItem) v).getId() == 1 && ((TabItem) v).getId() <= endScroll) {
                            if (((TabItem) v).getId() == endScroll)
                                ScrollBy((endScroll - 1) * getmTabItemWidth(), -getmTabItemWidth());
                            else
                                ScrollBy(-getmTabItemWidth());
                        } else if (lastTextView.getId() < ((TabItem) v).getId() && ((TabItem) v).getId() >= startScroll) {
                            currX = v.getId() * getmTabItemWidth() - (v.getId() - lastTextView.getId()) * getmTabItemWidth()
                                    - (startScroll - 1) * getmTabItemWidth();
                            ScrollBy((v.getId() - lastTextView.getId()) * getmTabItemWidth());
                            if (currX >= textViewList.size() * getmTabItemWidth())
                                currX = textViewList.size() * getmTabItemWidth();
                        } else if (lastTextView.getId() > ((TabItem) v).getId() && ((TabItem) v).getId() <= endScroll) {
                            currX = lastTextView.getId() * getmTabItemWidth() - (startScroll - 1) * getmTabItemWidth();
                            ScrollBy(-((lastTextView.getId() - ((TabItem) v).getId()) * getmTabItemWidth()));
                            if (currX <= 0) currX = 0;
                        }
                    }
                    invalidate();
                    lastTextView = (TabItem) v;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

        }
    }

    public void ScrollBy(int startX, int endX) {
        mScroller.startScroll(startX, 0, endX, 0, 1000);
        invalidate();
    }

    public void ScrollBy(int endX) {
        mScroller.startScroll(currX, 0, endX, 0, 1000);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            currX = mScroller.getCurrX();
            invalidate();
        }
    }
}
