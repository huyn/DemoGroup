package com.huyn.demogroup.paint;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by huyaonan on 16/11/14.
 */
public class VSProgressView extends View {

    private Paint paint;

    private static final int DURATION = 1000;
    private float mCurrentRatio = 0.5f;

    private int mColorRedBg = 0xa0ff0000;
    private int mColorRedFront = 0xffff0000;
    private int mColorBlueBg = 0xa00000ff;
    private int mColorBlueFront = 0xff0000ff;

    public VSProgressView(Context context) {
        this(context, null);
    }

    public VSProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VSProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        int mCurrent = (int) (width * mCurrentRatio);

        paint.setColor(mColorRedBg);
        canvas.drawRect(0, 0, mCurrent, height, paint);


        paint.setColor(mColorBlueBg);
        canvas.drawRect(mCurrent, 0, width, height, paint);


        paint.setColor(mColorRedFront);
        canvas.drawRect(0, 0, mCurrent, height, paint);


        paint.setColor(mColorBlueFront);
        canvas.drawRect(mCurrent, 0, width, height, paint);
    }

    public void updateProgress(float current) {
        updateProgress(current, false);
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
