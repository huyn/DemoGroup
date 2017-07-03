package com.huyn.demogroup.crop.motion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;

import com.huyn.demogroup.crop.CropRectView;

/**
 * Created by huyaonan on 2017/6/8.
 */

public class MotionWrapper {

    private View mTarget;
    private CropRectView mMotionLayout;
    private OnMotionStateListener mListener;

    private float scale = 1f;
    private float startX, startY;
    private float translationX=0f;
    private float translationY=0f;

    public MotionWrapper(CropRectView motionLayout, View target, final OnMotionStateListener listener) {
        mMotionLayout = motionLayout;
        mTarget = target;
        mListener = listener;
        mMotionLayout.setOnScaleListener(new OnMotionListener() {
            @Override
            public void onScale(float scaleFactor, float focusX, float focusY) {
                //Utils.sysout("======scaleFactor..." + scaleFactor);
                scale = scale * scaleFactor;
                mTarget.setScaleX(scale);
                mTarget.setScaleY(scale);
            }

            @Override
            public void onDrag(float dx, float dy) {
                translationX += dx;
                translationY += dy;
                mTarget.setTranslationX(translationX);
                mTarget.setTranslationY(translationY);
                //Utils.sysout("======drag...x:" + translationX + "/" + translationY);
            }

            @Override
            public void onRelease() {
                //Utils.sysout("======onrelease");
                reverse();

                mListener.onMotionEnd();
            }

            @Override
            public void onDragStart() {
                mListener.onMotionStart();
            }
        });
    }

    public void init(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
        this.translationX = startX;
        this.translationY = startY;
    }

    private void reverse() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float s = scale + (1-scale) * value;
                mTarget.setScaleX(s);
                mTarget.setScaleY(s);

                float x = translationX + (startX - translationX) * value;
                float y = translationY + (startY - translationY) * value;
                mTarget.setTranslationX(x);
                mTarget.setTranslationY(y);

                //Utils.sysout("======anim...x:" + x + "/" + y);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scale = 1f;
                translationX = startX;
                translationY = startY;
            }
        });
        animator.setDuration(200);
        animator.start();
    }

}
