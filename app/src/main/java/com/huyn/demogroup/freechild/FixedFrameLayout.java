package com.huyn.demogroup.freechild;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * A {@link FixedFrameLayout} that can be adjusted to a specified aspect ratio.
 */
public class FixedFrameLayout extends FrameLayout {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private float mRatio=0f;

    public FixedFrameLayout(Context context) {
        this(context, null);
    }

    public FixedFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixedFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int targetW = width;
        int targetH = height;
        if(mRatio > 0) {
            /**小数处理是为了防止height由float转int可能四舍五入导致大1px，宁愿短一点*/
            targetH = (int) (width / mRatio - 0.5f);
        } else if (0 == mRatioWidth || 0 == mRatioHeight) {
            targetH = height;
        } else {
            targetH = width * mRatioHeight / mRatioWidth;
        }

        setMeasuredDimension(targetW, targetH);
        for(int i=0; i<getChildCount(); i++)
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(targetW, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(targetH, MeasureSpec.EXACTLY));

        //Utils.sysout("onSizeChanged++++w:" + getMeasuredWidth() + "/" + getMeasuredHeight());
    }

}
