package com.huyn.demogroup.crop.motion;

public interface OnMotionGestureListener {

    public void onDragStart();

    public void onDrag(float dx, float dy);

    public void onDragEnd();

    public void onFling(float startX, float startY, float velocityX, float velocityY);

    public void onScale(float scaleFactor, float focusX, float focusY);

}