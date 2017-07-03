package com.huyn.demogroup.mask;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/7/3.
 */

public class TestMaskActivity extends Activity {

    protected MaskView mMask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mask);

        mMask = (MaskView) findViewById(R.id.mask_view);
    }
}
