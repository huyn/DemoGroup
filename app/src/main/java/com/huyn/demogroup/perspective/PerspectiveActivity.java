package com.huyn.demogroup.perspective;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.huyn.demogroup.R;

import java.util.List;

/**
 * Created by huyaonan on 2017/10/26.
 */

public class PerspectiveActivity extends Activity {
    private PerspectiveView mPerspectiveView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perspective);

        mPerspectiveView = (PerspectiveView) findViewById(R.id.pathView);
        findViewById(R.id.path_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSvgRes();
            }
        });
    }

    private void loadSvgRes() {
        SvgMaskLoader.loadSvgMasks(this, R.raw.img_10, mPerspectiveView.getWidth(), mPerspectiveView.getHeight(), new SvgMaskLoader.OnSvgLoadListener() {
            @Override
            public void onSvgLoadSuccess(List<SvgUtils.SvgPath> paths) {
                mPerspectiveView.update(paths);
            }

            @Override
            public void onSvgLoadFail() {

            }
        });
    }

}
