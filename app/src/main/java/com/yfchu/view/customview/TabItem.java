package com.yfchu.view.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.yfchu.utils.CommonUtil;

/**
 * yfchu
 */
public class TabItem extends TextView {

    private Context mContext;
    private float mTextSize = 20;
    private int mTextColorSelect = 0xff45c01a;
    private int mTextColorNormal = 0xff777777;
    private Paint mTextPaintNormal;
    private Paint mTextPaintSelect;
    private int mViewHeight, mViewWidth;
    private String mTextValue = "";
    private Bitmap mIconNormal;
    private Bitmap mIconSelect;
    private Rect mBoundText;

    public TabItem(Context context) {
        this(context, null);
    }

    public TabItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        initView();
        initText();
    }

    private void initView() {
        mBoundText = new Rect();
    }

    private void initText() {
        mTextPaintNormal = new Paint();
        mTextPaintNormal.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getResources().getDisplayMetrics()));
        mTextPaintNormal.setColor(mTextColorNormal);
        mTextPaintNormal.setAntiAlias(true);
        mTextPaintNormal.setAlpha(0xff);

        mTextPaintSelect = new Paint();
        mTextPaintSelect.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getResources().getDisplayMetrics()));
        mTextPaintSelect.setColor(mTextColorSelect);
        mTextPaintSelect.setAntiAlias(true);
        mTextPaintSelect.setAlpha(0);
    }

    private void measureText() {
        mTextPaintNormal.getTextBounds(mTextValue, 0, mTextValue.length(), mBoundText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0, height = 0;

        measureText();
        int contentWidth = mBoundText.width();//Math.max(mBoundText.width(), mIconNormal.getWidth());
        int desiredWidth = getPaddingLeft() + getPaddingRight() + contentWidth;
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                width = Math.min(widthSize, desiredWidth);
                break;
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                width = desiredWidth;
                break;
        }
        int contentHeight = mBoundText.height();//+ mIconNormal.getHeight();
        int desiredHeight = getPaddingTop() + getPaddingBottom() + contentHeight;
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(heightSize, desiredHeight);
                break;
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                height = contentHeight;
                break;
        }
        setMeasuredDimension(CommonUtil.convertDpToPx(mContext, 80), CommonUtil.convertDpToPx(mContext, 40));
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBitmap(canvas);
        drawText(canvas);
    }

    private void drawBitmap(Canvas canvas) {
//        int left = (mViewWidth - mIconNormal.getWidth())/2 ;
//        int top = (mViewHeight - mIconNormal.getHeight() - mBoundText.height()) /2 ;
//        canvas.drawBitmap(mIconNormal, left, top ,mIconPaintNormal);
//        canvas.drawBitmap(mIconSelect, left, top , mIconPaintSelect);
    }

    private void drawText(Canvas canvas) {
        float x = (mViewWidth - mBoundText.width() - getPaddingLeft() - getPaddingRight()) / 2.0f - 2;
        float y = (mViewHeight + mBoundText.height() + getPaddingTop() + getPaddingBottom()) / 2.0f - 2;//(mViewHeight + mIconNormal.getHeight() + mBoundText.height()) /2.0F ;
        canvas.drawText(mTextValue, x, y, mTextPaintNormal);
        canvas.drawText(mTextValue, x, y, mTextPaintSelect);
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
        mTextPaintNormal.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getResources().getDisplayMetrics()));
        mTextPaintSelect.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getResources().getDisplayMetrics()));
        invalidate();
    }

    public void setTextColorSelect(int mTextColorSelect) {
        this.mTextColorSelect = mTextColorSelect;
        mTextPaintSelect.setColor(mTextColorSelect);
        mTextPaintSelect.setAlpha(0);
    }

    public void setTextColorNormal(int mTextColorNormal) {
        this.mTextColorNormal = mTextColorNormal;
        mTextPaintNormal.setColor(mTextColorNormal);
        mTextPaintNormal.setAlpha(0xff);
    }

    public void setTextValue(String TextValue) {
        this.mTextValue = TextValue;
    }

    public void setIconText(int[] iconSelId, String TextValue) {
        this.mIconSelect = BitmapFactory.decodeResource(getResources(), iconSelId[0]);
        this.mIconNormal = BitmapFactory.decodeResource(getResources(), iconSelId[1]);
        this.mTextValue = TextValue;
    }

    public void setTabAlpha(float alpha) {
        int paintAlpha = (int) (alpha * 255);
//        mIconPaintSelect.setAlpha(paintAlpha);
//        mIconPaintNormal.setAlpha(255-paintAlpha);
        mTextPaintSelect.setAlpha(paintAlpha);
        mTextPaintNormal.setAlpha(255 - paintAlpha);
        invalidate();
    }

    public float getSelectAlpha(){
        return mTextPaintSelect.getAlpha();
    }
}
