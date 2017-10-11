package com.huyn.demogroup.pulltodismiss;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/10/10.
 */

public class PullToDismissActivity extends Activity {

    private Pull2DismissScrollView mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull2dismiss);

        mView = (Pull2DismissScrollView) findViewById(R.id.pull2dismiss_scrollview);
    }
}
