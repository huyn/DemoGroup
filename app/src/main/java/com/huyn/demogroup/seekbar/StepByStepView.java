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

public class StepByStepView extends View {

    private int mSelectedColor;
    private int mBorderColor;
    private int mUnselectedColor;
    private int mBorderSize;
    private int mRadius;

    private Paint mPaint;

    public StepByStepView(Context context) {
        this(context, null);
    }

    public StepByStepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepByStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StepByStepView, defStyleAttr, 0);
        mSelectedColor = a.getColor(R.styleable.StepByStepView_sbsv_selected_color, getResources().getColor(R.color.coloredroundview_color_default_selected));
        mBorderColor = a.getColor(R.styleable.StepByStepView_sbsv_border_color, getResources().getColor(R.color.coloredroundview_color_default_border));
        mUnselectedColor = a.getColor(R.styleable.StepByStepView_sbsv_unselected_color, getResources().getColor(R.color.coloredroundview_color_default_unselected));
        mBorderSize = a.getDimensionPixelSize(R.styleable.StepByStepView_sbsv_border_size, dp2px(2));
        mRadius = a.getDimensionPixelSize(R.styleable.StepByStepView_sbsv_radius, dp2px(20));
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int heightMeasureSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMeasureSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        if(heightMeasureSpecMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), mRadius);
        }

        if(widthMeasureSpecMode == MeasureSpec.UNSPECIFIED) {
        }

        float radius = getMeasuredHeight()/2f;
        mRadius = (int) radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        float centerX = width/2F;
        float centerY = height/2F;
//        if(mSelected) {
            mPaint.setColor(mSelectedColor);
            canvas.drawCircle(centerX, centerY, mRadius, mPaint);
//        } else {
            mPaint.setColor(mBorderColor);
            canvas.drawCircle(centerX, centerY, mRadius, mPaint);

            mPaint.setColor(mUnselectedColor);
            canvas.drawCircle(centerX, centerY, mRadius-mBorderSize, mPaint);
//        }
    }

}
