package com.huyn.demogroup.glassbreak.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class BrokenView extends View {

    private HashMap<View,BrokenAnimator> animMap;
    private LinkedList<BrokenAnimator> animList;
    private BrokenCallback callBack;
    private boolean enable;

    public BrokenView(Context context) {
        this(context, null);
    }

    public BrokenView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrokenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // KITKAT(API 19) and earlier need to set it when use
        // PathMeasure.getSegment to display resulting path.
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        enable = true;
        animMap = new HashMap<>();
        animList = new LinkedList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ListIterator<BrokenAnimator> iterator = animList.listIterator(animList.size());
        while(iterator.hasPrevious()) {
            iterator.previous().draw(canvas);
        }
    }

    public BrokenAnimator getAnimator(View view) {
        BrokenAnimator bAnim = animMap.get(view);
        return bAnim;
    }

    public BrokenAnimator createAnimator(final View view, Point point, BrokenConfig config){
        Bitmap bitmap = BrokenUtils.convertViewToBitmap(view);
        if(bitmap == null)
            return null;
        BrokenAnimator bAnim = new BrokenAnimator(this,view,bitmap,point,config);
        bAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                BrokenAnimator bAnim = (BrokenAnimator)animation;
                animMap.remove(view);
                animList.remove(bAnim);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                onBrokenFalling(view);
            }
        });
        animList.addLast(bAnim);
        animMap.put(view, bAnim);
        return bAnim;
    }

    public static BrokenView add2Window(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        BrokenView brokenView = new BrokenView(activity);
        rootView.addView(brokenView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        BrokenUtils.screenWidth = dm.widthPixels;
        BrokenUtils.screenHeight = dm.heightPixels;
        return brokenView;
    }

    public void reset(){
        ListIterator<BrokenAnimator> iterator = animList.listIterator();
        while(iterator.hasNext()) {
            BrokenAnimator bAnim = iterator.next();
            bAnim.removeAllListeners();
            bAnim.cancel();
        }
        animList.clear();
        animMap.clear();
        invalidate();
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setCallback(BrokenCallback c){
        callBack = c;
    }

    protected void onBrokenFallingEnd(View v){
        if(callBack != null)
            callBack.onFallingEnd(v);
    }

    protected void onBrokenFalling(View v){
        v.setVisibility(View.INVISIBLE);
        if(callBack != null){
            callBack.onFalling(v);
        }
    }
}
