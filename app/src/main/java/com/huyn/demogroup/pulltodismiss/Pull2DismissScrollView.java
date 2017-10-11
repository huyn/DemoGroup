package com.huyn.demogroup.pulltodismiss;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by huyaonan on 2017/10/10.
 */

public class Pull2DismissScrollView extends NestedScrollView {

    private int mTouchSlop;

    public Pull2DismissScrollView(Context context) {
        this(context, null);
    }

    public Pull2DismissScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Pull2DismissScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    private boolean mIsDragging = false;
    private float startX, startY;
    private float mLastTouchY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int scrollY = getScrollY();
        mIsDragging = false;
        System.out.println("++++++scrollY..." +scrollY);
        if(scrollY == 0) {
            int action = ev.getAction();
            System.out.println("++++++action..." +action);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    startX = ev.getX();
                    startY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = ev.getX();
                    float y = ev.getY();
                    float dx = x - startX;
                    float dy = y - startY;
                    mLastTouchY = ev.getY();
                    if(!mIsDragging) {
                        boolean isDragging = Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
                        System.out.println("++++++isDragging..." +isDragging + "/" + dy);
                        if(isDragging && dy > 0) {
                            mIsDragging = true;
                            return true;
                        }
                    }
                    break;
            }
        }
        boolean interceptTouch = super.onInterceptTouchEvent(ev);
        System.out.println("+++++interceptTouch:" + interceptTouch);
        return interceptTouch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        System.out.println("++++++mIsDragging..." +mIsDragging);
        if(mIsDragging) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mLastTouchY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dy = ev.getY() - mLastTouchY;
                    getChildAt(0).setTranslationY(dy);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    reverseChild();
                    break;
            }
            return true;
        }
        boolean touch = super.onTouchEvent(ev);
        System.out.println("+++++touch:" + touch);
        return touch;
    }

    private void reverseChild() {
        View child = getChildAt(0);
        float ty = child.getTranslationY();
        ObjectAnimator animator = ObjectAnimator.ofFloat(child, "translationY", ty, 0);
        animator.setDuration(200);
        animator.start();
    }

}
