package com.huyn.demogroup.leaveblank;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.huyn.demogroup.R;
import com.huyn.demogroup.particle.explosionfield.Utils;

/**
 * Created by huyaonan on 2017/7/10.
 */

public class LeaveBlankActivity extends Activity implements View.OnClickListener {

    PhotoView mPhotoView;

    View mSize11, mSize34, mSize43;
    View mFill, mBlank;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaveblank);

        mPhotoView = (PhotoView) findViewById(R.id.photoview);
        mPhotoView.setImageResource(R.drawable.wallpaper);

        mSize11 = findViewById(R.id.size_11);
        mSize34 = findViewById(R.id.size_34);
        mSize43 = findViewById(R.id.size_43);

        mFill = findViewById(R.id.type_fill);
        mBlank = findViewById(R.id.type_blank);

        mSize11.setOnClickListener(this);
        mSize34.setOnClickListener(this);
        mSize43.setOnClickListener(this);
        mFill.setOnClickListener(this);
        mBlank.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.size_11:
                switchSize(300, 300);
                break;
            case R.id.size_34:
                switchSize(300, 400);
                break;
            case R.id.size_43:
                switchSize(400, 300);
                break;
            case R.id.type_blank:
                switchScale(0.4f, true);
                break;
            case R.id.type_fill:
                switchScale(1f, false);
                break;
        }
    }

    private void switchScale(float scale, final boolean stable) {
        if(!stable)
            mPhotoView.setStable(stable);
        mPhotoView.setScale(scale, true, new OnAnimEndListener() {
            @Override
            public void onAnimEnd() {
                mPhotoView.setStable(stable);
            }
        });
    }

    private void switchSize(int width, int height) {
        final boolean stable = mPhotoView.getStable();
        if(stable)
            mPhotoView.setStable(false);
        final float scale = mPhotoView.getScale();
        final int startW = mPhotoView.getWidth();
        final int startH = mPhotoView.getHeight();
        final int endW = Utils.dp2Px(width);
        final int endH = Utils.dp2Px(height);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPhotoView.getLayoutParams();
                params.width = (int) (startW + (endW - startW) * value);
                params.height = (int) (startH + (endH - startH) * value);
                mPhotoView.setLayoutParams(params);
            }
        });
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPhotoView.setScale(scale, true, new OnAnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        if(stable)
                            mPhotoView.setStable(stable);
                    }
                });
            }
        });
        animator.start();
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPhotoView.getLayoutParams();
//        params.width = Utils.dp2Px(width);
//        params.height = Utils.dp2Px(height);
//        mPhotoView.setLayoutParams(params);
//        mPhotoView.setScale(scale, true, new OnAnimEndListener() {
//            @Override
//            public void onAnimEnd() {
//                if(stable)
//                    mPhotoView.setStable(stable);
//            }
//        });
        //if(stable)
        //    mPhotoView.setStable(stable);
    }

}
