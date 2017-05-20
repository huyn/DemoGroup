package com.davemorrissey.labs.subscaleview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.huyn.demogroup.R;

import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP;

/**
 * Created by huyaonan on 2017/5/20.
 */

public class SubsamplingScaleImageViewWrapper extends FrameLayout implements OnPullStateListener {
    
    private SubsamplingScaleImageView subsamplingScaleImageView;
    private View mRoot;
    
    public SubsamplingScaleImageViewWrapper(@NonNull Context context) {
        this(context, null);
    }

    public SubsamplingScaleImageViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubsamplingScaleImageViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        subsamplingScaleImageView = new SubsamplingScaleImageView(context);
        addView(subsamplingScaleImageView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        subsamplingScaleImageView.setOnPullStateListener(this);
        subsamplingScaleImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    
    public void attachRoot(View view) {
        mRoot = view;
    }

    private float getRatio(View view) {
        if(view.getId() == R.id.left)
            return 640f/1024;
        return 1334f/750;
    }

    private int getResId(View view) {
        if(view.getId() == R.id.left)
            return R.drawable.wallpaper;
        return R.drawable.bk_gallery_lightoff;
    }

    private int srcW, srcH, targetW, targetH;
    private Rect mSrcRect;
    private int ANIM_DURATION = 400;
    public void preview(View view) {
        srcW = view.getWidth();
        srcH = view.getHeight();

        float sizeRatio = getRatio(view);
        boolean isLong = sizeRatio > (mRoot.getHeight()*1f/mRoot.getWidth());

        final int mBigW = isLong ? (int) (mRoot.getHeight() / sizeRatio) : mRoot.getWidth();
        final int mBigH = isLong ? mRoot.getHeight() : (int) (mBigW * sizeRatio);

        targetW = mBigW;
        targetH = mBigH;

        int[] position = new int[2];
        mRoot.getLocationInWindow(position);
        int rootY = position[1];
        view.getLocationInWindow(position);
        int y = position[1] - rootY;
        int x = position[0];

        //subsamplingScaleImageView.setImageResource(getResId(view));
        subsamplingScaleImageView.setImage(ImageSource.resource(getResId(view)));
        subsamplingScaleImageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP);
        subsamplingScaleImageView.reverse(false);
        subsamplingScaleImageView.animToScale(true);

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

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) subsamplingScaleImageView.getLayoutParams();
                params.width = width;
                params.height = height;
                subsamplingScaleImageView.setLayoutParams(params);
                subsamplingScaleImageView.setTranslationX(tx);
                subsamplingScaleImageView.setTranslationY(ty);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                subsamplingScaleImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) subsamplingScaleImageView.getLayoutParams();
                params.width = mRoot.getWidth();
                params.height = mRoot.getHeight();
                subsamplingScaleImageView.setLayoutParams(params);
                subsamplingScaleImageView.setTranslationX(0);
                subsamplingScaleImageView.setTranslationY(0);

                subsamplingScaleImageView.animToScale(false);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(View.VISIBLE);
            }
        });
        animator.start();
    }

    public void dismiss() {
        final int dy = (mRoot.getHeight() - targetH) / 2;
        final int dx = (mRoot.getWidth() - targetW)/2;

        final float fx = mSrcRect.left;
        final float fy = mSrcRect.top;

        subsamplingScaleImageView.reverse(true);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) subsamplingScaleImageView.getLayoutParams();
        params.width = srcW;
        params.height = srcH;
        subsamplingScaleImageView.setLayoutParams(params);
        subsamplingScaleImageView.setTranslationX(dx);
        subsamplingScaleImageView.setTranslationY(dy);

        subsamplingScaleImageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float tx = dx + (fx - dx)*value;
                float ty = dy + (fy - dy)*value;

                int width = (int) (targetW + (srcW-targetW)*value);
                int height = (int) (targetH + (srcH-targetH)*value);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) subsamplingScaleImageView.getLayoutParams();
                params.width = width;
                params.height = height;
                subsamplingScaleImageView.setLayoutParams(params);
                subsamplingScaleImageView.setTranslationX(tx);
                subsamplingScaleImageView.setTranslationY(ty);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });
        animator.start();
    }

    @Override
    public void onPull(float fraction) {
        System.out.println("++++++++++++++++++++++++fraction:" + fraction);
    }

    @Override
    public void onReleaseToExit(final float startW, final float startH, PointF startTranslate, final AnimatorListenerAdapter listenerAdapter) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) subsamplingScaleImageView.getLayoutParams();
        params.width = (int) startW;
        params.height = (int) startH;
        setLayoutParams(params);
        setTranslationX(startTranslate.x);
        setTranslationY(startTranslate.y);
        subsamplingScaleImageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP);

        final float dy = startTranslate.y;
        final float dx = startTranslate.x;

        final float fx = mSrcRect.left;
        final float fy = mSrcRect.top;

        subsamplingScaleImageView.reverse(true);
        //subsamplingScaleImageView.reset(false);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(ANIM_DURATION*4);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float tx = dx + (fx - dx)*value;
                float ty = dy + (fy - dy)*value;

                int width = (int) (startW + (srcW-startW)*value);
                int height = (int) (startH + (srcH-startH)*value);

                System.out.println("+++++++++++++++++++++++++++++++++++++++" + width + "/" + height + "__" + tx + "/" + ty);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) subsamplingScaleImageView.getLayoutParams();
                params.width = width;
                params.height = height;
                subsamplingScaleImageView.setLayoutParams(params);
                subsamplingScaleImageView.setTranslationX(tx);
                subsamplingScaleImageView.setTranslationY(ty);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //setVisibility(View.GONE);
                listenerAdapter.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });
        animator.start();
    }

    @Override
    public int[] getSrcParams() {
        int[] result = new int[4];
        result[0] = mSrcRect.left;
        result[1] = mSrcRect.top;
        result[2] = srcW;
        result[3] = srcH;
        return result;
    }
}
