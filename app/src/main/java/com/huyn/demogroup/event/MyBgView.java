package com.huyn.demogroup.event;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by huyaonan on 16/10/17.
 */
public class MyBgView extends FrameLayout {
    public MyBgView(Context context) {
        super(context);
    }

    public MyBgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyBgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        System.out.println("++++++++++++++onInterceptTouchEvent");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("++++++++++++++onInterceptTouchEvent");
        return super.onTouchEvent(event);
    }


    public boolean dispatchTouchEvent(MotionEvent ev) {
        System.out.println("++++++++++++++dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }
}
