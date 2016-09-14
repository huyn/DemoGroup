package com.huyn.demogroup;

import android.app.Application;
import android.view.LayoutInflater;

import com.huyn.demogroup.util.Static;

/**
 * Created by huyaonan on 16/9/14.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Static.CONTEXT = this;
        Static.INFLATER = LayoutInflater.from(this);
    }
}
