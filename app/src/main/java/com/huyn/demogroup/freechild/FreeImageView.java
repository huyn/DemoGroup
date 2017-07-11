package com.huyn.demogroup.freechild;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.ImageView;

import com.huyn.demogroup.leaveblank.OnAnimEndListener;
import com.huyn.demogroup.leaveblank.PhotoViewAttacher;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class FreeImageView extends ImageView {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private float mRatio=0f;

    private FixedViewAttacher attacher;

    public FreeImageView(Context context) {
        this(context, null);
    }

    public FreeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FreeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if(attacher == null)
            attacher = new FixedViewAttacher(this);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    public void setAspectRatio(float ratio) {
        this.mRatio = ratio;
        requestLayout();
    }

    public float getAspectRatio() {
        return mRatio > 0 ? mRatio : (mRatioHeight > 0 ? (mRatioWidth * 1f / mRatioHeight) : -1);
    }

    public void setScale(float scale) {
        attacher.setScale(scale);
    }

    public void setScale(float scale, boolean animate) {
        attacher.setScale(scale, animate);
    }

    public void setScale(float scale, boolean animate, OnAnimEndListener listener) {
        attacher.setScale(scale, animate, listener);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        attacher.setScale(scale, focalX, focalY, animate);
    }

    public void setStable(boolean stable) {
        attacher.setStable(stable);
    }

    public boolean getStable() {
        return attacher.getStable();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

//        if(mRatio > 0) {
//            setMeasuredDimension(width, (int) (width / mRatio));
//        } else if(mRatioHeight > 0 && mRatioWidth > 0) {
//            setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
//        } else {
//            setMeasuredDimension(width, height);
//        }
        System.out.println("++++++width:" + width + "/" + height);
        setMeasuredDimension(height*2, height);
    }

    public void reset() {
        setImageDrawable(null);
    }

}
