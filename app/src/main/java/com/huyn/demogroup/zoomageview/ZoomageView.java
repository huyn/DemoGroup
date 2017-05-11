package com.huyn.demogroup.zoomageview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by huyaonan on 2017/5/11.
 */

public class ZoomageView extends android.support.v7.widget.AppCompatImageView {
    public ZoomageView(Context context) {
        this(context, null);
    }

    public ZoomageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.CENTER_CROP);
    }
}
