package com.huyn.demogroup.rectscale;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by huyaonan on 2017/6/20.
 */

@SuppressLint("AppCompatCustomView")
public class RectScaleView extends ImageView {

    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();

    public RectScaleView(Context context) {
        this(context, null);
    }

    public RectScaleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        super.setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);

        mTop = 0;
        mLeft = 0;
        mRight = 100;
        mBottom = 100;
//        updateRect();

//        anim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        Drawable mDrawable = getDrawable();
        if(mDrawable == null)
            return;

        final int saveCount = canvas.getSaveCount();
        canvas.save();

//        if (mCropToPadding) {
//            final int scrollX = mScrollX;
//            final int scrollY = mScrollY;
//            canvas.clipRect(scrollX + mPaddingLeft, scrollY + mPaddingTop,
//                    scrollX + mRight - mLeft - mPaddingRight,
//                    scrollY + mBottom - mTop - mPaddingBottom);
//        }
        //System.out.println("--------mRight:" + mRight + "/mBottom:" + mBottom);
        canvas.clipRect(mLeft, mTop, mRight, mBottom);

        //canvas.clipRect(200, 200, 500, 500);
        //canvas.translate(200, 200);

        if (getImageMatrix() != null) {
            canvas.concat(getImageMatrix());
        }
        mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private float mLeft=0, mTop=0, mRight=0, mBottom=0;
    public void anim(final RectF src) {
        if(getDrawable() == null || src.isEmpty())
            return;

        Drawable drawable = getDrawable();
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        final float drawableRatio = drawableHeight * 1f / drawableWidth;

        final int endX=0;
        final int endY=0;
        final int endW = getWidth();
        final int endH = getHeight();

        final RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);

        float srcRatio = src.height()/src.width();

        float w = src.width();
        float h = src.height();
        float x = src.left;
        float y = src.top;

        if(srcRatio < drawableRatio) {
            h = w * drawableRatio;
            y = src.top - (h-src.height())/2;
        } else {
            w = h / drawableRatio;
            x = src.left - (w-src.width())/2;
        }

        final float startX=x;
        final float startY=y;
        final float startW=w;
        final float startH=h;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float currentX = startX + (endX - startX)*value;
                float currentY = startY + (endY - startY)*value;
                float currentW = startW + (endW - startW)*value;
                float currentH = startH + (endH - startH)*value;

                float tempX = src.left + (endX - src.left)*value;
                float tempY = src.top + (endY - src.top)*value;
                float tempW = src.width() + (endW - src.width())*value;
                float tempH = src.height() + (endH - src.height())*value;

                mLeft = tempX;
                mTop = tempY;
                mRight = tempX + tempW;
                mBottom = tempY + tempH;

                mBaseMatrix.reset();

                RectF mTempDst = new RectF(currentX, currentY, currentW+currentX, currentH+currentY);

                mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER);
                setImageViewMatrix(getDrawMatrix());
            }
        });
        animator.start();
    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    private void setImageViewMatrix(Matrix matrix) {
        setImageMatrix(matrix);
    }
}
