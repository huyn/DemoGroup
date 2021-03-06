package com.huyn.demogroup.shadow;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by huyaonan on 2017/12/6.
 */

public class ShadowLinearLayout extends LinearLayout {

    private Paint mPaint;

    public ShadowLinearLayout(Context context) {
        this(context, null);
    }

    public ShadowLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        int padding = getPaddingLeft();
        mPaint.setMaskFilter(new BlurMaskFilter(padding/2, BlurMaskFilter.Blur.SOLID));
        canvas.drawRect(padding, padding, w-padding, h-padding, mPaint);
        super.dispatchDraw(canvas);
    }
}
