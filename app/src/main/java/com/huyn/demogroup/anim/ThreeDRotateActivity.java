package com.huyn.demogroup.anim;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/11/2.
 */
public class ThreeDRotateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rotate);

//        final ClipView clipView = (ClipView) findViewById(R.id.clipview);
//        final ClipLayout clipLayout = (ClipLayout) findViewById(R.id.cliplayout);
//
//        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clipView.anim(300, 300);
//            }
//        });
//
//        findViewById(R.id.start_cliplayout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clipLayout.startAnim(null);
//            }
//        });
        final GoodsLayout layout = (GoodsLayout) findViewById(R.id.goods_layout);
        findViewById(R.id.start_goods).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.showClip(500, 400);
            }
        });
    }
}
