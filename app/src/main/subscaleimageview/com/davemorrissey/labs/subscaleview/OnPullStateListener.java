package com.davemorrissey.labs.subscaleview;

import android.animation.AnimatorListenerAdapter;
import android.graphics.PointF;

/**
 * Created by huyaonan on 2017/5/20.
 */

public interface OnPullStateListener {
    public void onPull(float fraction);
    public void onReleaseToExit(float startW, float startH, PointF startTranslate, AnimatorListenerAdapter listenerAdapter);
    public int[] getSrcParams();
}
