package com.huyn.demogroup.shadow;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by huyaonan on 2017/12/6.
 */

public class ShadowFrameLayout extends FrameLayout {

    private Paint mPaint;

    public ShadowFrameLayout(Context context) {
        this(context, null);
    }

    public ShadowFrameLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int padding = getPaddingLeft();
        mPaint.setShadowLayer(padding/2, 0, padding/4, Color.BLACK);
        canvas.drawRect(padding, padding, getWidth()-padding, getHeight()-padding, mPaint);
        super.dispatchDraw(canvas);
    }
}
