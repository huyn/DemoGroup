package com.huyn.demogroup.freechild;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huyn.demogroup.R;
import com.huyn.demogroup.leaveblank.OnAnimEndListener;
import com.huyn.demogroup.particle.explosionfield.Utils;

/**
 * Created by huyaonan on 2017/7/11.
 */

public class FreeChildActivity extends Activity {

    FreeImageView mImg;
    View mButton;
    FixedFrameLayout mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_freechild);

        mLayout = (FixedFrameLayout) findViewById(R.id.framelayout);
        mLayout.setAspectRatio(1);
        mImg = (FreeImageView) findViewById(R.id.image);
        findViewById(R.id.move_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offsetImg(true);
            }
        });

        findViewById(R.id.move_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offsetImg(false);
            }
        });

        findViewById(R.id.resize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setAspectRatio(1.3f);
            }
        });

        findViewById(R.id.leftblank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchScale(0.4f, true);
            }
        });

        findViewById(R.id.fill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchScale(1f, false);
            }
        });

        findViewById(R.id.size11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSize(200, 200);
            }
        });

        findViewById(R.id.size43).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSize(300, 200);
            }
        });
    }

    private void switchSize(int width, int height) {
        final boolean stable = mImg.getStable();
        if(stable)
            mImg.setStable(false);
        mLayout.setAspectRatio(0);
        //final float scale = mImg.getScale();
        final int startW = mLayout.getWidth();
        final int startH = mLayout.getHeight();
        final int endW = Utils.dp2Px(width);
        final int endH = Utils.dp2Px(height);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLayout.getLayoutParams();
                params.width = (int) (startW + (endW - startW) * value);
                params.height = (int) (startH + (endH - startH) * value);
                mLayout.setLayoutParams(params);
            }
        });
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                mPhotoView.setScale(scale, true, new OnAnimEndListener() {
//                    @Override
//                    public void onAnimEnd() {
//                        if(stable)
//                            mPhotoView.setStable(stable);
//                    }
//                });
                mImg.setStable(stable);
            }
        });
        animator.start();
    }

    private void switchScale(float scale, final boolean stable) {
        if(!stable)
            mImg.setStable(stable);
        mImg.setScale(scale, true, new OnAnimEndListener() {
            @Override
            public void onAnimEnd() {
                mImg.setStable(stable);
            }
        });
    }

    private void offsetImg(boolean toRight) {
        float tx = mImg.getTranslationX();
        tx += toRight ? 50 : -50;
        mImg.setTranslationX(tx);
    }

}
