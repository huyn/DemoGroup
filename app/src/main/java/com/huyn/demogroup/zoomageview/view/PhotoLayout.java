package com.huyn.demogroup.zoomageview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by huyaonan on 2017/6/20.
 */

public class PhotoLayout extends FrameLayout {

    private PhotoView mPhotoView;
    private PhotoViewAttacher mPhotoAttacher;

    public PhotoLayout(@NonNull Context context) {
        this(context, null);
    }

    public PhotoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPhotoView = new PhotoView(context);
        addView(mPhotoView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mPhotoAttacher = mPhotoView.init();
    }

    public void setImageDrawable(Drawable drawable) {
        mPhotoView.setImageDrawable(drawable);
    }

    public void setImageResource(int resId) {
        mPhotoView.setImageResource(resId);
    }

    public void setImageURI(Uri uri) {
        mPhotoView.setImageURI(uri);
    }

    public void setDragToFinishListener(int distance, OnDragToFinishListener listener) {
        mPhotoView.getAttacher().setDragToFinishListener(distance, listener);
    }

//    public void anim(final RectF src) {
//        mPhotoView.anim(src);
//    }
//
//    public void resize() {
//        mPhotoView.resize();
//    }
//
//    public boolean isVisible() {
//        return mPhotoView.isVisible();
//    }
//
//    public void reverse() {
//        mPhotoView.reverse();
//    }

    private Drawable getDrawable() {
        if(mPhotoView == null)
            return null;
        return mPhotoView.getDrawable();
    }

    private RectF mSrc;
    public void anim(final RectF src) {
        if(getDrawable() == null || src.isEmpty())
            return;

        mSrc = src;

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
        animator.setDuration(300);
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

                float mLeft = tempX;
                float mTop = tempY;
                float mRight = tempX + tempW;
                float mBottom = tempY + tempH;
                mPhotoView.updateClip(mLeft, mTop, mRight, mBottom);

                RectF mTempDst = new RectF(currentX, currentY, currentW+currentX, currentH+currentY);

                mPhotoAttacher.update(mTempSrc, mTempDst);
            }
        });
        animator.start();
    }

    RectF mTemp = new RectF();
    public void resize() {
        if(!mTemp.isEmpty()) {
            reverseResize();
            return;
        }

        Drawable drawable = getDrawable();
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        final float drawableRatio = drawableHeight * 1f / drawableWidth;

        final int startX=0;
        final int startY=0;
        final int startW = getWidth();
        final int startH = getHeight();

        final RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);

        mTemp.set(0, 0, startW, startH/2);

        float srcRatio = mTemp.height()/mTemp.width();

        float w = mTemp.width();
        float h = mTemp.height();
        float x = mTemp.left;
        float y = mTemp.top;

        //cropinside
        if(srcRatio < drawableRatio) {
            w = h / drawableRatio;
            x = mTemp.left - (w-mTemp.width())/2;
        } else {
            h = w*drawableRatio;
            y = mTemp.top - (h-mTemp.height())/2;
        }

        final float endX=x;
        final float endY=y;
        final float endW=w;
        final float endH=h;

        System.out.println("endx:" + endX + "/" + endY + "/" + endW + "/" + endH);

        final float scale = mPhotoAttacher.getScale();
        final float targetScale = mPhotoAttacher.getMinimumScale();

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float currentX = startX + (endX - startX)*value;
                float currentY = startY + (endY - startY)*value;
                float currentW = startW + (endW - startW)*value;
                float currentH = startH + (endH - startH)*value;

                float tempX = startX + (mTemp.left - startX)*value;
                float tempY = startY + (mTemp.top - startY)*value;
                float tempW = startW + (mTemp.width() - startW)*value;
                float tempH = startH + (mTemp.height() - startH)*value;

                float mLeft = tempX;
                float mTop = tempY;
                float mRight = tempX + tempW;
                float mBottom = tempY + tempH;
                mPhotoView.updateClip(mLeft, mTop, mRight, mBottom);

                RectF mTempDst = new RectF(currentX, currentY, currentW+currentX, currentH+currentY);

                float s = scale + (targetScale-scale)*value;
                mPhotoAttacher.update(mTempSrc, mTempDst, s);
            }
        });
        animator.start();
    }

    public void reverseResize() {
        if(mTemp.isEmpty())
            return;

        Drawable drawable = getDrawable();
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        final float drawableRatio = drawableHeight * 1f / drawableWidth;

        final float startX=mTemp.left;
        final float startY=mTemp.top;
        final float startW = mTemp.width();
        final float startH = mTemp.height();

        final RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);

        final int endX=0;
        final int endY=0;
        final int endW = getWidth();
        final int endH = getHeight();

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float currentX = startX + (endX - startX)*value;
                float currentY = startY + (endY - startY)*value;
                float currentW = startW + (endW - startW)*value;
                float currentH = startH + (endH - startH)*value;

                float tempX = mTemp.left + (endX - mTemp.left)*value;
                float tempY = mTemp.top + (endY - mTemp.top)*value;
                float tempW = mTemp.width() + (endW - mTemp.width())*value;
                float tempH = mTemp.height() + (endH - mTemp.height())*value;

                float mLeft = tempX;
                float mTop = tempY;
                float mRight = tempX + tempW;
                float mBottom = tempY + tempH;
                mPhotoView.updateClip(mLeft, mTop, mRight, mBottom);

                RectF mTempDst = new RectF(currentX, currentY, currentW+currentX, currentH+currentY);

//                System.out.println("+++++++++++++++++++");
//                System.out.println("left:" + mLeft + "/" + mTop + "/" + mRight + "/" + mBottom);
//                System.out.println("target:" + currentX + "/" + currentY + "/" + currentW + "/" + currentH);

                mPhotoAttacher.update(mTempSrc, mTempDst);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTemp.setEmpty();
            }
        });
        animator.start();
    }

    public boolean isVisible() {
        return mSrc != null && !mSrc.isEmpty();
    }

    public void reverse() {
        Drawable drawable = getDrawable();
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        final float drawableRatio = drawableHeight * 1f / drawableWidth;

        final int startX=0;
        final int startY=0;
        final int startW = getWidth();
        final int startH = getHeight();

        final RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);

        float srcRatio = mSrc.height()/mSrc.width();

        float w = mSrc.width();
        float h = mSrc.height();
        float x = mSrc.left;
        float y = mSrc.top;

        //centercrop
        if(srcRatio < drawableRatio) {
            h = w * drawableRatio;
            y = mSrc.top - (h-mSrc.height())/2;
        } else {
            w = h / drawableRatio;
            x = mSrc.left - (w-mSrc.width())/2;
        }

        final float endX=x;
        final float endY=y;
        final float endW=w;
        final float endH=h;

        final float scale = mPhotoAttacher.getScale();
        final float targetScale = mPhotoAttacher.getMinimumScale();

        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float currentX = startX + (endX - startX)*value;
                float currentY = startY + (endY - startY)*value;
                float currentW = startW + (endW - startW)*value;
                float currentH = startH + (endH - startH)*value;

                float tempX = startX + (mSrc.left - startX)*value;
                float tempY = startY + (mSrc.top - startY)*value;
                float tempW = startW + (mSrc.width() - startW)*value;
                float tempH = startH + (mSrc.height() - startH)*value;

                float mLeft = tempX;
                float mTop = tempY;
                float mRight = tempX + tempW;
                float mBottom = tempY + tempH;
                mPhotoView.updateClip(mLeft, mTop, mRight, mBottom);

                RectF mTempDst = new RectF(currentX, currentY, currentW+currentX, currentH+currentY);

                float s = scale + (targetScale-scale)*value;
                mPhotoAttacher.update(mTempSrc, mTempDst, s);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSrc.setEmpty();
            }
        });
        animator.start();
    }

}
