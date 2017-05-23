package com.huyn.demogroup.crop;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/5/23.
 */

public class CropActivity extends Activity {

    private CropRectView mCropView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        mCropView = (CropRectView) findViewById(R.id.cropview);
        mCropView.postRefrsh();

        mCropView.setOverlayViewChangeListener(new OverlayViewChangeListener() {
            @Override
            public void onCropRectUpdated(RectF cropRect) {
                System.out.println("+++++++croprect:" + cropRect.left + "/" + cropRect.top + "/" + cropRect.right + "/" + cropRect.bottom);
            }
        });
    }
}
