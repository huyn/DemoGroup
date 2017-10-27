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
                //loadSvgRes();
                loadSvgResList();
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
            public void onSvgListLoadSuccess(List<List<SvgUtils.SvgPath>> paths) {

            }

            @Override
            public void onSvgLoadFail() {

            }
        });
    }

    private void loadSvgResList() {
        int[] resIds = {R.raw.img_0,
                        R.raw.img_1,
                        R.raw.img_2,
                        R.raw.img_3,
                        R.raw.img_4,
                        R.raw.img_5,
                        R.raw.img_6,
                        R.raw.img_7,
                        R.raw.img_8,
                        R.raw.img_9,
                        R.raw.img_10,
                        R.raw.img_11,
                        R.raw.img_12,
                        R.raw.img_13,
                        R.raw.img_14,
                        R.raw.img_15};
        SvgMaskLoader.loadSvgMasks(this, resIds, mPerspectiveView.getWidth(), mPerspectiveView.getHeight(), new SvgMaskLoader.OnSvgLoadListener() {
            @Override
            public void onSvgLoadSuccess(List<SvgUtils.SvgPath> paths) {
            }

            @Override
            public void onSvgListLoadSuccess(List<List<SvgUtils.SvgPath>> paths) {
                mPerspectiveView.startAnim(paths);
            }

            @Override
            public void onSvgLoadFail() {

            }
        });
    }

}
