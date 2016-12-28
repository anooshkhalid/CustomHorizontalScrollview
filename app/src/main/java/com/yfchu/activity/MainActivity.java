package com.yfchu.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.yfchu.adapter.HorizontalAdapter;
import com.yfchu.adapter.ScrollerAdapter;
import com.yfchu.entity.HorizontalClass;
import com.yfchu.entity.ScrollerClass;
import com.yfchu.interfaces.ScrollInterface;
import com.yfchu.utils.CommonUrl;
import com.yfchu.view.customview.R;
import com.yfchu.view.customview.ScrollerLayout;
import com.yfchu.view.customview.TabItem;
import com.yfchu.view.customview.HorizontalView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private HorizontalAdapter horizontalAdapter;
    private ScrollerAdapter scrollAdapter;
    private List<HorizontalClass> horizontalList = new ArrayList<>();
    private List<ScrollerClass> scrollList = new ArrayList<>();
    private List<TabItem> textViewList;

    private View lineView;
    private LinearLayout.LayoutParams lineViewParams;
    private ScrollerLayout scrollView;
    private HorizontalView horizontalView;

    /**
     * moveAnimTime：动画时间
     */
    private int moveAnimTime = 300;
    /**
     * movestate：滑动状态
     */
    private int moveState = -1;

    /**
     * Index：当前index
     */
    private int Index = 0;

    /**
     * targetLeftIndex：下一滑动index
     * rollBackIndex：回滚index
     */
    private int targetLeftIndex = -1, rollBackIndex = -1;

    /**
     * mTouchSlop：手机滑动最小距离
     * scale：根据touchSlop设置滑动基数
     * faultSelectScale：（选中）文字初始缩放大小
     * faultNormalScale：（默认）文字初始缩放大小
     */
    private float mTouchSlop = 0.0f, scale = 0.0f, faultSelectScale = 1.1f, faultNormalScale = 0.95f, textSelectScale, textNormalScale;

    /**
     * mXDown：手机按下时的屏幕坐标
     * XMove：滑动坐标
     */
    private float mXDown, mXMove, mOldXMove = 0;

    private int mHorizontalViewWidth, mTabItemWidth, pageItemNumber, startScroll, endScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        horizontalView = (HorizontalView) findViewById(R.id.horizontal);
        scrollView = (ScrollerLayout) findViewById(R.id.scrollView);

        scrollView.setHandler(touchHandler);
        horizontalView.setHandler(horiHandler);

        mTouchSlop = scrollView.getTouchSlop();
        if (mTouchSlop < 80) {
            scale = 0.03f;
        } else {
            scale = 0.01f;
        }

        for (int i = 0; i < 10; i++) {
            HorizontalClass c = new HorizontalClass();
            if (i % 2 == 0)
                c.setAge(i + 1 + "月龄");
            else
                c.setAge(i + 1 + "+月龄");
            horizontalList.add(c);
        }
        horizontalAdapter = new HorizontalAdapter(this, horizontalList);
        horizontalView.setAdapter(horizontalAdapter);
        textViewList = horizontalView.getTextViewList();
        lineView = horizontalView.getLineView();

        for (int i = 0; i < 10; i++) {
            ScrollerClass s = new ScrollerClass();
            s.setAge(i + 1 + "月龄接种数据");
            scrollList.add(s);
        }
        scrollAdapter = new ScrollerAdapter(this, scrollList);
        scrollView.setAdapter(scrollAdapter);
    }

    /**
     * horizontalview点击时切换scrollview对应的pager
     */
    private Handler horiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonUrl.SCROLL_ROLL:
                    scrollView.setMovePage(msg.arg1);//点击item切换Scrollview
                    CursorFollow();
                    break;
                case CommonUrl.SETDATA:
                    int width = 0;
                    for (int i = 0; i < textViewList.size(); i++) {
                        width += textViewList.get(i).getViewWidth();
                    }
                    mHorizontalViewWidth = msg.arg1;//屏幕宽度
                    mTabItemWidth = width / textViewList.size();//item宽度
                    pageItemNumber = mHorizontalViewWidth / mTabItemWidth;//页显示item数量
                    startScroll = pageItemNumber / 2 + pageItemNumber % 2;//起始滚动item
                    endScroll = textViewList.size() - pageItemNumber + startScroll;//结束滚动item
                    horizontalView.setStartScroll(startScroll, endScroll);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * scrollLayout滑动时触发horizontalview文字的颜色和大小渐变效果。
     */
    private Handler touchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MotionEvent.ACTION_DOWN:
                    mXDown = (float) msg.obj;
                    mXMove = mXDown;
                    Index = scrollView.getTargetIndex();
                    /**
                     * 按下时初始化参数
                     * */
                    rollBackIndex = -1;
                    targetLeftIndex = -1;
                    moveState = -1;
                    textSelectScale = faultSelectScale;
                    textNormalScale = faultNormalScale;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mXMove > mOldXMove && mOldXMove != 0)
                        lineView.setTranslationX(lineView.getTranslationX() - 1);
                    else {
                        if (mOldXMove == 0) {
                            if (mXMove > mXDown)
                                lineView.setTranslationX(lineView.getTranslationX() - 1);
                            else
                                lineView.setTranslationX(lineView.getTranslationX() + 1);
                        } else
                            lineView.setTranslationX(lineView.getTranslationX() + 1);
                    }
                    mOldXMove = mXMove;
                    float temp1 = (float) (textViewList.get(Index).getSelectAlpha() / 25.5 / 10);
                    if (temp1 <= 0f) temp1 = 0f;
                    if (temp1 > 1.0f) temp1 = 1.0f;

                    if (moveState == 1 && mXMove - (float) msg.obj < 0) {
                        moveState = 2;
                    } else if (moveState == 3 && mXMove - (float) msg.obj > 0) {
                        moveState = 4;
                    } else if (mXMove - mXDown < 0 && mXMove - (float) msg.obj > 0) {
                        moveState = 1;
                    } else if (mXMove - mXDown > 0 && mXMove - (float) msg.obj < 0) {
                        moveState = 3;
                    }
                    MoveState(temp1);
                    mXMove = (float) msg.obj;
                    break;
                case MotionEvent.ACTION_UP:
                    int temp = -1;
                    if (Index != scrollView.getTargetIndex()) {
                        temp = scrollView.getTargetIndex();
                        for (int i = 0; i < textViewList.size(); i++) {
                            if (i == temp) {
                                if (Index < scrollView.getTargetIndex() && scrollView.getTargetIndex() >= startScroll) {
                                    if (scrollView.getTargetIndex() == startScroll)
                                        horizontalView.ScrollBy(0, mTabItemWidth);
                                    else
                                        horizontalView.ScrollBy(mTabItemWidth);
                                } else if (Index > scrollView.getTargetIndex() && scrollView.getTargetIndex() <= endScroll) {
                                    if (scrollView.getTargetIndex() == endScroll)
                                        horizontalView.ScrollBy((endScroll - 1) * mTabItemWidth, -mTabItemWidth);
                                    else
                                        horizontalView.ScrollBy(-mTabItemWidth);
                                }
                                CursorFollow();
                                textViewList.get(i).setTabAlpha(1f);
                                ValueAnimator anim = ValueAnimator.ofFloat(textViewList.get(i).getScaleX(), faultSelectScale);
                                anim.setDuration(moveAnimTime);
                                anim.start();
                                final int finalI = i;
                                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        textViewList.get(finalI).setScaleX(Float.parseFloat(animation.getAnimatedValue().toString()));
                                        textViewList.get(finalI).setScaleY(Float.parseFloat(animation.getAnimatedValue().toString()));
                                    }
                                });
                                horizontalView.setLastView(textViewList.get(i));
                            } else {
                                textViewList.get(i).setTabAlpha(0f);
                                ValueAnimator anim = ValueAnimator.ofFloat(textViewList.get(i).getScaleX(), faultNormalScale);
                                anim.setDuration(moveAnimTime);
                                anim.start();
                                final int finalI = i;
                                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        textViewList.get(finalI).setScaleX(Float.parseFloat(animation.getAnimatedValue().toString()));
                                        textViewList.get(finalI).setScaleY(Float.parseFloat(animation.getAnimatedValue().toString()));
                                    }
                                });
                            }
                        }
                    } else if (rollBackIndex != -1) {
                        temp = Index;
                        CursorFollow();
                        for (int i = 0; i < textViewList.size(); i++) {
                            if (i == temp) {
                                textViewList.get(i).setTabAlpha(1f);
                                textViewList.get(i).setScaleX(faultSelectScale);
                                textViewList.get(i).setScaleY(faultSelectScale);
                                horizontalView.setLastView(textViewList.get(i));
                            } else {
                                textViewList.get(i).setTabAlpha(0f);
                                textViewList.get(i).setScaleX(faultNormalScale);
                                textViewList.get(i).setScaleY(faultNormalScale);
                            }
                        }
                    }
                    break;
                case CommonUrl.FASTMOVE: //快速滑动时的up
                    if (Index < scrollView.getTargetIndex() && scrollView.getTargetIndex() >= startScroll) {
                        if (scrollView.getTargetIndex() == startScroll)
                            horizontalView.ScrollBy(0, mTabItemWidth);
                        else
                            horizontalView.ScrollBy(mTabItemWidth);
                    } else if (Index > scrollView.getTargetIndex() && scrollView.getTargetIndex() <= endScroll) {
                        if (scrollView.getTargetIndex() == endScroll)
                            horizontalView.ScrollBy((endScroll - 1) * mTabItemWidth, -mTabItemWidth);
                        else
                            horizontalView.ScrollBy(-mTabItemWidth);
                    }
                    CursorFollow();
                    horizontalView.setLastView(textViewList.get(scrollView.getTargetIndex()));
                    ValueAnimator anim = ValueAnimator.ofFloat(textViewList.get(scrollView.getTargetIndex()).getScaleX(), faultSelectScale);
                    anim.setDuration(moveAnimTime);
                    anim.start();
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            textViewList.get(scrollView.getTargetIndex()).setScaleX((Float) animation.getAnimatedValue());
                            textViewList.get(scrollView.getTargetIndex()).setScaleY((Float) animation.getAnimatedValue());
                            if ((Float) animation.getAnimatedValue() > 1.0f)
                                textViewList.get(scrollView.getTargetIndex()).setTabAlpha(((Float) animation.getAnimatedValue() - 1.0f) * 10);
                        }
                    });
                    ValueAnimator anim1 = ValueAnimator.ofFloat(textViewList.get(Index).getScaleX(), faultNormalScale);
                    anim1.setDuration(moveAnimTime);
                    anim1.start();
                    anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            textViewList.get(Index).setScaleX((Float) animation.getAnimatedValue());
                            textViewList.get(Index).setScaleY((Float) animation.getAnimatedValue());
                            if ((Float) animation.getAnimatedValue() > 1.0f)
                                textViewList.get(Index).setTabAlpha(((Float) animation.getAnimatedValue() - 1.0f) * 10);
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 滑动的四种状态
     * */
    private void MoveState(float temp1) {
        switch (moveState) {
            case 1:
                if (textSelectScale > faultNormalScale) {
                    textSelectScale = textSelectScale - 0.01f < faultNormalScale ? faultNormalScale : textSelectScale - 0.001f;
                    textViewList.get(Index).setScaleX(textSelectScale);
                    textViewList.get(Index).setScaleY(textSelectScale);
                }
                textViewList.get(Index).setTabAlpha(temp1 - scale < 0f ? 0f : temp1 - scale);
                targetLeftIndex = Index + 1;
                textViewList.get(targetLeftIndex).setTabAlpha(1f - temp1);
                if (textNormalScale < faultSelectScale) {
                    textNormalScale = textNormalScale + 0.01f > faultSelectScale ? faultSelectScale : textNormalScale + 0.001f;
                    textViewList.get(targetLeftIndex).setScaleX(textNormalScale);
                    textViewList.get(targetLeftIndex).setScaleY(textNormalScale);
                }
                if (Index - 1 >= 0) {
                    textViewList.get(Index - 1).setTabAlpha(0f);
                    textViewList.get(Index - 1).setTextSize(20);
                }
                rollBackIndex = targetLeftIndex;
                break;
            case 2:
                if (textNormalScale <= faultSelectScale) {
                    textNormalScale = textNormalScale - 0.01f < faultNormalScale ? faultNormalScale : textNormalScale - 0.001f;
                    textViewList.get(targetLeftIndex).setScaleX(textNormalScale);
                    textViewList.get(targetLeftIndex).setScaleY(textNormalScale);
                }
                textViewList.get(Index).setTabAlpha(temp1 + scale > 1f ? 1f : temp1 + scale);
                targetLeftIndex = Index + 1;
                textViewList.get(targetLeftIndex).setTabAlpha(1f - temp1);
                if (textSelectScale >= faultNormalScale) {
                    textSelectScale = textSelectScale + 0.01f > faultSelectScale ? faultSelectScale : textSelectScale + 0.001f;
                    textViewList.get(Index).setScaleX(textSelectScale);
                    textViewList.get(Index).setScaleY(textSelectScale);
                }
                if (Index - 1 >= 0) {
                    textViewList.get(Index - 1).setTabAlpha(0f);
                    textViewList.get(Index - 1).setTextSize(20);
                }
                rollBackIndex = targetLeftIndex;
                break;
            case 3:
                if (textSelectScale > faultNormalScale) {
                    textSelectScale = textSelectScale - 0.01f < faultNormalScale ? faultNormalScale : textSelectScale - 0.001f;
                    textViewList.get(Index).setScaleX(textSelectScale);
                    textViewList.get(Index).setScaleY(textSelectScale);
                }
                textViewList.get(Index).setTabAlpha(temp1 - scale < 0f ? 0f : temp1 - scale);
                targetLeftIndex = Index - 1;
                if (textNormalScale < faultSelectScale) {
                    textNormalScale = textNormalScale + 0.01f > faultSelectScale ? faultSelectScale : textNormalScale + 0.001f;
                    textViewList.get(targetLeftIndex).setScaleX(textNormalScale);
                    textViewList.get(targetLeftIndex).setScaleY(textNormalScale);
                }
                if (Index + 1 <= textViewList.size() - 1) {
                    textViewList.get(Index + 1).setTabAlpha(0f);
                    textViewList.get(Index + 1).setTextSize(20);
                }
                textViewList.get(targetLeftIndex).setTabAlpha(1f - temp1);
                rollBackIndex = targetLeftIndex;
                break;
            case 4:
                if (textNormalScale <= faultSelectScale) {
                    textNormalScale = textNormalScale - 0.01f < faultNormalScale ? faultNormalScale : textNormalScale - 0.001f;
                    textViewList.get(targetLeftIndex).setScaleX(textNormalScale);
                    textViewList.get(targetLeftIndex).setScaleY(textNormalScale);
                }
                textViewList.get(Index).setTabAlpha(temp1 + scale > 1f ? 1f : temp1 + scale);
                if (textSelectScale >= faultNormalScale) {
                    textSelectScale = textSelectScale + 0.01f > faultSelectScale ? faultSelectScale : textSelectScale + 0.001f;
                    textViewList.get(Index).setScaleX(textSelectScale);
                    textViewList.get(Index).setScaleY(textSelectScale);
                }
                targetLeftIndex = Index - 1;
                if (Index + 1 <= textViewList.size() - 1) {
                    textViewList.get(Index + 1).setTabAlpha(0f);
                    textViewList.get(Index + 1).setTextSize(20);
                }
                textViewList.get(targetLeftIndex).setTabAlpha(1f - temp1);
                rollBackIndex = targetLeftIndex;
                break;
        }
    }

    /**
     * Horizontalview游标跟随
     * */
    private void CursorFollow() {
        ValueAnimator anim = ValueAnimator.ofFloat(lineView.getTranslationX(),//游标当前位置
                getScrollDistance(Index, scrollView.getTargetIndex()));//目标位置
        anim.setDuration(moveAnimTime);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lineView.setTranslationX((float) animation.getAnimatedValue());//设置平移过渡
            }
        });
        lineViewParams = (LinearLayout.LayoutParams) lineView.getLayoutParams();
        anim = ValueAnimator.ofFloat(lineViewParams.width,
                textViewList.get(scrollView.getTargetIndex()).getViewWidth());//游标宽度过渡
        anim.setDuration(moveAnimTime);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lineViewParams.width = Math.round(Float.parseFloat(animation.getAnimatedValue().toString()));
                lineView.setLayoutParams(lineViewParams);
            }
        });
    }

    /**
     * 返回目标X值
     * */
    private float getScrollDistance(int index, int targetIndex) {
        float width = 0;
        for (int i = 0; i < targetIndex; i++) {
            width += textViewList.get(i).getViewWidth()+1; //1是中间的竖线宽度
        }
        return width < 0 ? 0 : width;
    }
}
