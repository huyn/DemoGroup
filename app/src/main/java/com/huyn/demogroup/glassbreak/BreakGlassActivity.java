package com.huyn.demogroup.glassbreak;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.huyn.demogroup.R;
import com.huyn.demogroup.glassbreak.view.BrokenAnimator;
import com.huyn.demogroup.glassbreak.view.BrokenConfig;
import com.huyn.demogroup.glassbreak.view.BrokenView;

/**
 * Created by huyaonan on 2017/10/11.
 */

public class BreakGlassActivity extends Activity implements View.OnClickListener {

    private ImageView mImg;

    private BrokenView brokenView;
    private BrokenConfig config;
    private BrokenAnimator brokenAnim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakglass);
        mImg = (ImageView) findViewById(R.id.break_img);

        brokenView = BrokenView.add2Window(this);
        config = new BrokenConfig();
        mImg.setOnClickListener(this);

        findViewById(R.id.break_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImg.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(config.childView != null){
            config.region = new Region(config.childView.getLeft(),
                    config.childView.getTop(),
                    config.childView.getRight(),
                    config.childView.getBottom());
        }
        Rect rect = new Rect();
        mImg.getGlobalVisibleRect(rect);
        //Point point = new Point(mImg.getWidth()/2, mImg.getHeight()/2);
        Point point = new Point(rect.left + rect.width()/2, rect.top + rect.height()/2);
        brokenAnim = brokenView.getAnimator(v);
        if (brokenAnim == null)
            brokenAnim = brokenView.createAnimator(v, point, config);
        if (brokenAnim == null)
            return;
        if (!brokenAnim.isStarted()) {
            brokenAnim.start();
        }
    }
}
