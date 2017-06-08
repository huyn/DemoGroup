package com.huyn.demogroup.scale;

/**
 * Created by huyaonan on 2017/6/8.
 */

public interface OnScaleListener {
    public void onScale(float scaleFactor, float focusX, float focusY);
    public void onDrag(float dx, float dy);
    public void onRelease();
}
