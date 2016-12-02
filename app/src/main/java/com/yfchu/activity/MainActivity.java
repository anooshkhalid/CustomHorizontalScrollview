package com.yfchu.activity;

import android.animation.ValueAnimator;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yfchu.adapter.HorizontalAdapter;
import com.yfchu.entity.HorizontalClass;
import com.yfchu.view.customview.R;
import com.yfchu.view.customview.ScrollerLayout;
import com.yfchu.view.customview.TabItem;
import com.yfchu.view.customview.HorizontalView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private HorizontalAdapter horizontalAdapter;
    private List<HorizontalClass> horizontalList = new ArrayList<>();

    private TextView line;
    private ScrollerLayout scrollView;
    private HorizontalView horizontalView;

    /**
     * movestate：滑动状态
     */
    private int moveState = -1;

    /**
     * lastIndex：当前index
     */
    private int lastIndex = 0;

    /**
     * targetLeftIndex：下一滑动index
     * rollBackIndex：回滚index
     */
    private int targetLeftIndex = -1, rollBackIndex = -1;

    /**
     * mTouchSlop：手机滑动最小距离
     * scale：根据touchSlop设置滑动基数
     * textSelectScale：（选中）文字初始缩放大小
     * textNormalScale：（默认）文字初始缩放大小
     */
    private float mTouchSlop = 0.0f, scale = 0.0f, textSelectScale = 1.1f, textNormalScale = 1.0f;

    /**
     * textViewList：横向listview集合
     */
    private List<TabItem> textViewList;

    /**
     * mXDown：手机按下时的屏幕坐标
     * 没XMove：滑动坐标
     */
    private float mXDown, mXMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        line = (TextView) findViewById(R.id.line);
        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) line.getLayoutParams();
        rl.height = 1;
        line.setLayoutParams(rl);

        horizontalView = (HorizontalView) findViewById(R.id.horizontal);
        scrollView = (ScrollerLayout) findViewById(R.id.scrollView);

        scrollView.setHandler(touchHandler);
        horizontalView.setHandler(horiHandler);
        textViewList = horizontalView.getTextViewList();

        mTouchSlop = scrollView.getTouchSlop();
        if (mTouchSlop < 80) {
            scale = 0.03f;
        } else {
            scale = 0.01f;
        }

        for (int i = 0; i < 5; i++) {
            HorizontalClass c = new HorizontalClass();
            c.setAge(i + 1 + "月龄");
            horizontalList.add(c);
        }
        horizontalAdapter=new HorizontalAdapter(this,horizontalList);
        horizontalView.setAdapter(horizontalAdapter);
    }

    /**
     * horizontalview点击时切换scrollview对应的pager
     */
    private Handler horiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            scrollView.setMovePage(msg.what);
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
                    lastIndex = scrollView.getTargetIndex();

                    /**
                     * 按下时初始化参数
                     * */
                    rollBackIndex = -1;
                    targetLeftIndex = -1;
                    moveState = -1;
                    textSelectScale = 1.1f;
                    textNormalScale = 1.0f;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float temp1 = (float) (textViewList.get(lastIndex).getSelectAlpha() / 25.5 / 10);
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
                    switch (moveState) {
                        case 1:
                            if (textSelectScale > 1.0f) {
                                textSelectScale = textSelectScale - 0.01f < 1.0f ? 1.0f : textSelectScale - 0.001f;
                                textViewList.get(lastIndex).setScaleX(textSelectScale);
                                textViewList.get(lastIndex).setScaleY(textSelectScale);
                            }
                            textViewList.get(lastIndex).setTabAlpha(temp1 - scale < 0f ? 0f : temp1 - scale);
                            targetLeftIndex = lastIndex + 1;
                            textViewList.get(targetLeftIndex).setTabAlpha(1f - temp1);
                            if (textNormalScale < 1.1f) {
                                textNormalScale = textNormalScale + 0.01f > 1.1f ? 1.1f : textNormalScale + 0.001f;
                                textViewList.get(targetLeftIndex).setScaleX(textNormalScale);
                                textViewList.get(targetLeftIndex).setScaleY(textNormalScale);
                            }
                            if (lastIndex - 1 >= 0) {
                                textViewList.get(lastIndex - 1).setTabAlpha(0f);
                                textViewList.get(lastIndex - 1).setTextSize(20);
                            }
                            rollBackIndex = targetLeftIndex;
                            break;
                        case 2:
                            if (textNormalScale <= 1.1f) {
                                textNormalScale = textNormalScale - 0.01f < 1.0f ? 1.0f : textNormalScale - 0.001f;
                                textViewList.get(targetLeftIndex).setScaleX(textNormalScale);
                                textViewList.get(targetLeftIndex).setScaleY(textNormalScale);
                            }
                            textViewList.get(lastIndex).setTabAlpha(temp1 + scale > 1f ? 1f : temp1 + scale);
                            targetLeftIndex = lastIndex + 1;
                            textViewList.get(targetLeftIndex).setTabAlpha(1f - temp1);
                            if (textSelectScale >= 1.0f) {
                                textSelectScale = textSelectScale + 0.01f > 1.1f ? 1.1f : textSelectScale + 0.001f;
                                textViewList.get(lastIndex).setScaleX(textSelectScale);
                                textViewList.get(lastIndex).setScaleY(textSelectScale);
                            }
                            if (lastIndex - 1 >= 0) {
                                textViewList.get(lastIndex - 1).setTabAlpha(0f);
                                textViewList.get(lastIndex - 1).setTextSize(20);
                            }
                            rollBackIndex = targetLeftIndex;
                            break;
                        case 3:
                            if (textSelectScale > 1.0f) {
                                textSelectScale = textSelectScale - 0.01f < 1.0f ? 1.0f : textSelectScale - 0.001f;
                                textViewList.get(lastIndex).setScaleX(textSelectScale);
                                textViewList.get(lastIndex).setScaleY(textSelectScale);
                            }
                            textViewList.get(lastIndex).setTabAlpha(temp1 - scale < 0f ? 0f : temp1 - scale);
                            targetLeftIndex = lastIndex - 1;
                            if (textNormalScale < 1.1f) {
                                textNormalScale = textNormalScale + 0.01f > 1.1f ? 1.1f : textNormalScale + 0.001f;
                                textViewList.get(targetLeftIndex).setScaleX(textNormalScale);
                                textViewList.get(targetLeftIndex).setScaleY(textNormalScale);
                            }
                            if (lastIndex + 1 <= textViewList.size() - 1) {
                                textViewList.get(lastIndex + 1).setTabAlpha(0f);
                                textViewList.get(lastIndex + 1).setTextSize(20);
                            }
                            textViewList.get(targetLeftIndex).setTabAlpha(1f - temp1);
                            rollBackIndex = targetLeftIndex;
                            break;
                        case 4:
                            if (textNormalScale <= 1.1f) {
                                textNormalScale = textNormalScale - 0.01f < 1.0f ? 1.0f : textNormalScale - 0.001f;
                                textViewList.get(targetLeftIndex).setScaleX(textNormalScale);
                                textViewList.get(targetLeftIndex).setScaleY(textNormalScale);
                            }
                            textViewList.get(lastIndex).setTabAlpha(temp1 + scale > 1f ? 1f : temp1 + scale);
                            if (textSelectScale >= 1.0f) {
                                textSelectScale = textSelectScale + 0.01f > 1.1f ? 1.1f : textSelectScale + 0.001f;
                                textViewList.get(lastIndex).setScaleX(textSelectScale);
                                textViewList.get(lastIndex).setScaleY(textSelectScale);
                            }
                            targetLeftIndex = lastIndex - 1;
                            if (lastIndex + 1 <= textViewList.size() - 1) {
                                textViewList.get(lastIndex + 1).setTabAlpha(0f);
                                textViewList.get(lastIndex + 1).setTextSize(20);
                            }
                            textViewList.get(targetLeftIndex).setTabAlpha(1f - temp1);
                            rollBackIndex = targetLeftIndex;
                            break;
                    }
                    mXMove = (float) msg.obj;
                    break;
                case MotionEvent.ACTION_UP:
                    int temp = -1;
                    if (lastIndex != scrollView.getTargetIndex()) {
                        temp = scrollView.getTargetIndex();
                        for (int i = 0; i < textViewList.size(); i++) {
                            if (i == temp) {
                                textViewList.get(i).setTabAlpha(1f);
                                textViewList.get(i).setScaleX(1.1f);
                                textViewList.get(i).setScaleY(1.1f);
                                horizontalView.setLastView(textViewList.get(i));
                            } else {
                                textViewList.get(i).setTabAlpha(0f);
                                textViewList.get(i).setScaleX(1.0f);
                                textViewList.get(i).setScaleY(1.0f);
                            }
                        }
                    } else if (rollBackIndex != -1) {
                        temp = lastIndex;
                        for (int i = 0; i < textViewList.size(); i++) {
                            if (i == temp) {
                                textViewList.get(i).setTabAlpha(1f);
                                textViewList.get(i).setScaleX(1.1f);
                                textViewList.get(i).setScaleY(1.1f);
                                horizontalView.setLastView(textViewList.get(i));
                            } else {
                                textViewList.get(i).setTabAlpha(0f);
                                textViewList.get(i).setScaleX(1.0f);
                                textViewList.get(i).setScaleY(1.0f);
                            }
                        }
                    }
                    break;
                case ScrollerLayout.FirstMove: //快速滑动时的up
                    horizontalView.setLastView(textViewList.get(scrollView.getTargetIndex()));
                    ValueAnimator anim = ValueAnimator.ofFloat(1.0f, 1.1f);
                    anim.setDuration(200);
                    anim.start();
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            textViewList.get(scrollView.getTargetIndex()).setScaleX((Float) animation.getAnimatedValue());
                            textViewList.get(scrollView.getTargetIndex()).setScaleY((Float) animation.getAnimatedValue());
                            textViewList.get(scrollView.getTargetIndex()).setTabAlpha(((Float) animation.getAnimatedValue() - 1.0f) * 10);
                        }
                    });
                    ValueAnimator anim1 = ValueAnimator.ofFloat(1.1f, 1.0f);
                    anim1.setDuration(200);
                    anim1.start();
                    anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            textViewList.get(lastIndex).setScaleX((Float) animation.getAnimatedValue());
                            textViewList.get(lastIndex).setScaleY((Float) animation.getAnimatedValue());
                            textViewList.get(lastIndex).setTabAlpha(((Float) animation.getAnimatedValue() - 1.0f) * 10);
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
