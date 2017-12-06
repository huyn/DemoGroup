package com.huyn.demogroup.shadow;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/12/6.
 */

public class ShadowActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shadow);
    }
}
