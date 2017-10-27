package com.huyn.demogroup;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by huyaonan on 2017/5/11.
 */

public class DemoLifeCirleCallback implements Application.ActivityLifecycleCallbacks {

    public static final String ACTION_RETURN_FOREGROUND = "ACTION_RETURN_FOREGROUND";

    private List<Activity> activities = new LinkedList<>();

    private Context mContext;
    private boolean foreground = false, paused = true;
    private Handler handler = new Handler();
    private Runnable check;
    private static final long CHECK_DELAY = 500;

    private static final String TAG = "ActivityLifecycleCallbacks";

    public DemoLifeCirleCallback(Context context) {
        mContext = context;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;
        if (check != null)
            handler.removeCallbacks(check);
        if (wasBackground){
            onBecameForeground();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;
        if (check != null)
            handler.removeCallbacks(check);
        handler.postDelayed(check = new Runnable(){
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;

                    onBecameBackground();
                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        removeActivity(activity);
    }

    private void onBecameForeground() {
        mContext.sendOrderedBroadcast(new Intent(ACTION_RETURN_FOREGROUND), null);
    }

    private void onBecameBackground() {

    }
    /**
     * 添加Activity
     */
    public void addActivity(Activity activity) {
        if (activities == null) {
            activities = new LinkedList<>();
        }

        if (!activities.contains(activity)) {
            activities.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 移除Activity
     */
    public void removeActivity(Activity activity) {
        if (activities.contains(activity)) {
            activities.remove(activity);
        }

        if (activities.size() == 0) {
            activities = null;
        }
    }

    /**
     * 销毁所有activity
     */
    public void removeAllActivities() {
        for (Activity activity : activities) {
            if (null != activity) {
                activity.finish();
            }
        }
    }


}
