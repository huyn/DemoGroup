package com.huyn.demogroup.event;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by huyaonan on 16/10/17.
 */
public class MySecondLayout extends LinearLayout {
    public MySecondLayout(Context context) {
        super(context);
    }

    public MySecondLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySecondLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        System.out.println("..............onInterceptTouchEvent");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("..............onInterceptTouchEvent");
        return super.onTouchEvent(event);
    }
}
