package com.huyn.demogroup.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huyn.demogroup.R;

import static com.huyn.demogroup.seekbar.BubbleUtils.dp2px;

/**
 * Created by huyaonan on 2017/5/26.
 */

public class ColoredRoundView extends View {

    private int mSelectedColor;
    private int mBorderColor;
    private int mUnselectedColor;
    private int mBorderSize;
    private int mRadius;
    private boolean mSelected = false;

    private Paint mPaint;

    public ColoredRoundView(Context context) {
        this(context, null);
    }

    public ColoredRoundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColoredRoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColoredRoundView, defStyleAttr, 0);
        mSelectedColor = a.getColor(R.styleable.ColoredRoundView_crv_selected_color, getResources().getColor(R.color.coloredroundview_color_default_selected));
        mBorderColor = a.getColor(R.styleable.ColoredRoundView_crv_border_color, getResources().getColor(R.color.coloredroundview_color_default_border));
        mUnselectedColor = a.getColor(R.styleable.ColoredRoundView_crv_unselected_color, getResources().getColor(R.color.coloredroundview_color_default_unselected));
        mBorderSize = a.getDimensionPixelSize(R.styleable.ColoredRoundView_crv_border_size, dp2px(2));
        mRadius = a.getDimensionPixelSize(R.styleable.ColoredRoundView_crv_radius, dp2px(20));
        mSelected = a.getBoolean(R.styleable.ColoredRoundView_crv_selected, true);
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int heightMeasureSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        if(heightMeasureSpecMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), mRadius);
        }

        float radius = Math.min(getMeasuredWidth()/2, getMeasuredHeight()/2);
        mRadius = (int) radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        float centerX = width/2F;
        float centerY = height/2F;
        if(mSelected) {
            mPaint.setColor(mSelectedColor);
            canvas.drawCircle(centerX, centerY, mRadius, mPaint);
        } else {
            mPaint.setColor(mBorderColor);
            canvas.drawCircle(centerX, centerY, mRadius, mPaint);

            mPaint.setColor(mUnselectedColor);
            canvas.drawCircle(centerX, centerY, mRadius-mBorderSize, mPaint);
        }
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
        invalidate();
    }

    public void toggle() {
        if(mSelected)
            mSelected = false;
        else
            mSelected = true;
        invalidate();
    }

}
