package com.huyn.demogroup.scale;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/6/8.
 */

public class ScaleDemoActivity extends Activity {
    private ScaleLayout mScaleLayout;
    private ImageView mScaleImg;

    private float scale = 1f;
    private float startY = 300;
    private float translationX=0f;
    private float translationY=startY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);
        mScaleLayout = (ScaleLayout) findViewById(R.id.scale_layout);
        mScaleImg = (ImageView) findViewById(R.id.scale_img);
        mScaleImg.setTranslationY(startY);
        mScaleLayout.setOnScaleListener(new OnScaleListener() {
            @Override
            public void onScale(float scaleFactor, float focusX, float focusY) {
                System.out.println("======scaleFactor..." + scaleFactor);
                scale = scale * scaleFactor;
                mScaleImg.setScaleX(scale);
                mScaleImg.setScaleY(scale);
            }

            @Override
            public void onDrag(float dx, float dy) {
                translationX += dx;
                translationY += dy;
                mScaleImg.setTranslationX(translationX);
                mScaleImg.setTranslationY(translationY);
                System.out.println("======drag...x:" + translationX + "/" + translationY);
            }

            @Override
            public void onRelease() {
                System.out.println("======onrelease");
                reverse();
            }
        });
    }

    private void reverse() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float s = scale + (1-scale) * value;
                mScaleImg.setScaleX(s);
                mScaleImg.setScaleY(s);

                float x = translationX*(1-value);
                float y = translationY + (startY - translationY) * value;//translationY*(1-value);
                mScaleImg.setTranslationX(x);
                mScaleImg.setTranslationY(y);

                System.out.println("======anim...x:" + x + "/" + y);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scale = 1f;
                translationX = 0;
                translationY = startY;
            }
        });
        animator.setDuration(400);
        animator.start();
    }

}
