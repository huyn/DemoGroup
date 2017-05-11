package com.huyn.demogroup.zoomageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/5/11.
 */

public class ZoomageViewActivity extends Activity implements View.OnClickListener {

    private ImageView left, right, placeholder;
    private View mRoot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_zoomageview);

        mRoot = findViewById(R.id.root);

        left = (ImageView) findViewById(R.id.left);
        right = (ImageView) findViewById(R.id.right);
        placeholder = (ImageView) findViewById(R.id.placeholder);

        left.setOnClickListener(this);
        right.setOnClickListener(this);
        placeholder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
            case R.id.right:
                preview(v);
                break;
            case R.id.placeholder:
                dismiss();
                break;
        }
    }

    private float getRatio(View view) {
        if(view == left)
            return 640f/1024;
        return 1334f/750;
    }

    private int getResId(View view) {
        if(view == left)
            return R.drawable.wallpaper;
        return R.drawable.bk_gallery_lightoff;
    }

    private int srcW, srcH;
    private Rect mSrcRect;
    private int ANIM_DURATION = 500;
    private void preview(View view) {
        srcW = view.getWidth();
        srcH = view.getHeight();

        float sizeRatio = getRatio(view);
        boolean isLong = sizeRatio > (mRoot.getHeight()*1f/mRoot.getWidth());

        final int mBigW = isLong ? (int) (mRoot.getHeight() / sizeRatio) : mRoot.getWidth();
        final int mBigH = isLong ? mRoot.getHeight() : (int) (mBigW * sizeRatio);

        int[] position = new int[2];
        view.getLocationInWindow(position);
        int y = position[1];
        int x = position[0];

        placeholder.setImageResource(getResId(view));

        mSrcRect = new Rect();
        mSrcRect.left = x;
        mSrcRect.top = y;
        mSrcRect.right = x + view.getWidth();
        mSrcRect.bottom = y + view.getHeight();

        final int dy = (mRoot.getHeight() - mBigH) / 2;
        final int dx = (mRoot.getWidth() - mBigW)/2;

        final float fx = x;
        final float fy = y;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float tx = fx + (dx - fx)*value;
                float ty = fy + (dy - fy)*value;

                int width = (int) (srcW + (mBigW-srcW)*value);
                int height = (int) (srcH + (mBigH-srcH)*value);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) placeholder.getLayoutParams();
                params.width = width;
                params.height = height;
                placeholder.setLayoutParams(params);
                placeholder.setTranslationX(tx);
                placeholder.setTranslationY(ty);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
                placeholder.setVisibility(View.VISIBLE);
                //showPreviewBottoom();
            }
        });
        animator.start();
    }

    private void dismiss() {
        final int mBigH = placeholder.getHeight();
        final int mBigW = placeholder.getWidth();
        final int dy = (mRoot.getHeight() - mBigH) / 2;
        final int dx = (mRoot.getWidth() - mBigW)/2;

        final float fx = mSrcRect.left;
        final float fy = mSrcRect.top;


        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float tx = dx + (fx - dx)*value;
                float ty = dy + (fy - dy)*value;

                int width = (int) (mBigW + (srcW-mBigW)*value);
                int height = (int) (mBigH + (srcH-mBigH)*value);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) placeholder.getLayoutParams();
                params.width = width;
                params.height = height;
                placeholder.setLayoutParams(params);
                placeholder.setTranslationX(tx);
                placeholder.setTranslationY(ty);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                placeholder.setVisibility(View.GONE);
                //hidePreviewBottoom();
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });
        animator.start();
    }

}
