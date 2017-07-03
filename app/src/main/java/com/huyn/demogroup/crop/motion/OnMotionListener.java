package com.huyn.demogroup.crop.motion;

/**
 * Created by huyaonan on 2017/6/8.
 */

public interface OnMotionListener {
    public void onScale(float scaleFactor, float focusX, float focusY);
    public void onDrag(float dx, float dy);
    public void onRelease();
    public void onDragStart();
}
