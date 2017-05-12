package com.huyn.demogroup.paint;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by huyaonan on 16/11/14.
 */
public class ProgressView extends View {

    private Paint paint;

    private static final int DURATION = 1000;
    private float mCurrentRatio = 0.5f;
    private int mCurrentColor = 0xffcccccc;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        int mCurrent = (int) (width * mCurrentRatio);

        //画当前
        paint.setColor(mCurrentColor);
        canvas.drawRect(0, 0, mCurrent + 1, height, paint);
    }

    public void updateProgress(float current) {
        updateProgress(current, false);
    }

    public void updateColor(int color) {
        mCurrentColor = color;
        invalidate();
    }

    public void updateProgress(float current, boolean anim) {
        if(anim) {
            ValueAnimator animator = ValueAnimator.ofFloat(mCurrentRatio, current);
            animator.setDuration((long) (DURATION * Math.abs(current - mCurrentRatio)));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    updateProgress(value, false);
                }
            });
            animator.start();
        } else {
            this.mCurrentRatio = current;
            invalidate();
        }
    }

}
