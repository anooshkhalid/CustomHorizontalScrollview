package com.yfchu.view.customview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yfchu.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/5.
 */
public class HorizontalView extends HorizontalScrollView {

    private Context mContext;
    private LinearLayout contain;
    private TabItem lastTextView;
    private List<TabItem> textViewList = new ArrayList<>();

    private int initNum = 0;
    private Handler mHandler;

    public HorizontalView(Context context) {
        super(context);
        mContext = context;
    }

    public HorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public HorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setHandler(Handler m) {
        this.mHandler = m;
    }

    public List<TabItem> getTextViewList() {
        return textViewList;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (initNum < 1) {
            init();
            initNum++;
        }
    }

    private void init() {
        contain = (LinearLayout) getChildAt(0);
        for (int i = 0; i < 5; i++) {
            TabItem horizontal_contain = (TabItem) LayoutInflater.from(mContext).inflate(R.layout.horizontal_contain, null);
            horizontal_contain.setId(i);
            horizontal_contain.setTextValue(i + 1 + "月龄");
            horizontal_contain.setTextColorSelect(getResources().getColor(R.color.textSelectColor));
            horizontal_contain.setTextColorNormal(getResources().getColor(R.color.textColor));
//            horizontal_contain.setTextSize(smallTextSize);
            if (i == 0) {
//                horizontal_contain.setTextSize(normalTextSize);
                horizontal_contain.setScaleX(1.1f);
                horizontal_contain.setScaleY(1.1f);
                horizontal_contain.setTabAlpha(1.0f);
                lastTextView = horizontal_contain;
            } else {
                horizontal_contain.setScaleX(1.0f);
                horizontal_contain.setScaleY(1.0f);
            }
            horizontal_contain.setOnClickListener(new OnClickListener());
            contain.addView(horizontal_contain);

            TextView line = new TextView(mContext);
            line.setWidth(1);
            line.setHeight(CommonUtil.convertDpToPx(mContext, 20));
            line.setGravity(Gravity.CENTER_VERTICAL);
            line.setBackgroundColor(mContext.getResources().getColor(R.color.line));
            contain.addView(line);

            textViewList.add(horizontal_contain);
        }
    }

    public void setLastView(TabItem v) {
        lastTextView = v;
    }

    class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (lastTextView.getId() == v.getId())
                return;
            ValueAnimator anim = ValueAnimator.ofFloat(1.1f, 1.0f);
            anim.setDuration(200);
            anim.start();
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
//                    if (textSelectAlpha > 0.3)
//                        lastTextView.setTabAlpha(textSelectAlpha -= 0.1f);
//                    Log.i("textSelectAlpha", "textSelectAlpha:" + textSelectAlpha);
                    lastTextView.setTabAlpha(((Float) animation.getAnimatedValue() - 1.0f) * 10);
                    lastTextView.setScaleX((Float) animation.getAnimatedValue());
                    lastTextView.setScaleY((Float) animation.getAnimatedValue());
                }
            });
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
//                    lastTextView.setTabAlpha(textSelectAlpha -= 0.1f);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
//                    lastTextView.setTabAlpha(textSelectAlpha = 0f);
//                    Log.i("textSelectAlpha", "textSelectAlpha:" + textSelectAlpha);
//                    textSelectAlpha = 1f;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            ValueAnimator anim1 = ValueAnimator.ofFloat(1.0f, 1.1f);
            anim1.setDuration(200);
            anim1.start();
            anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
//                    if (textNormalAlpha < 0.7)
//                        ((TabItem) v).setTabAlpha(textNormalAlpha += 0.1f);
//                    Log.i("textNormalAlpha", "textNormalAlpha:" + textNormalAlpha);
                    ((TabItem) v).setTabAlpha(((Float) animation.getAnimatedValue() - 1.0f) * 10);
                    ((TabItem) v).setScaleX((Float) animation.getAnimatedValue());
                    ((TabItem) v).setScaleY((Float) animation.getAnimatedValue());
                }
            });
            anim1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
//                    ((TabItem) v).setTabAlpha(textNormalAlpha += 0.1f);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mHandler != null)
                        mHandler.sendEmptyMessage(v.getId());
                    lastTextView = (TabItem) v;
//                    ((TabItem) v).setTabAlpha(textNormalAlpha = 1.0f);
//                    Log.i("textNormalAlpha", "textNormalAlpha:" + textNormalAlpha);
//                    textNormalAlpha = 0f;
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
}
