package com.huyn.demogroup.rectscale;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.huyn.demogroup.R;
import com.huyn.demogroup.zoomageview.view.OnDragToFinishListener;
import com.huyn.demogroup.zoomageview.view.PhotoLayout;

/**
 * Created by huyaonan on 2017/6/20.
 */

public class RectScaleActivity extends Activity {

    private RectScaleView rectScaleView;
    private Button fill, resize;
    private ImageView img;
    private PhotoLayout mPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rectscale);
        rectScaleView = (RectScaleView) findViewById(R.id.rect_scale);
        img = (ImageView) findViewById(R.id.rect_img);
        mPhoto = (PhotoLayout) findViewById(R.id.photoview);

        fill = (Button) findViewById(R.id.fill);
        fill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rect rect = new Rect();
                img.getGlobalVisibleRect(rect);
                RectF rectF = new RectF();
                rectF.set(rect);
                //rectScaleView.setImageResource(R.drawable.bk_front);
                //rectScaleView.anim(rectF);
                mPhoto.setImageResource(R.drawable.bk_front);
                mPhoto.anim(rectF);
            }
        });

        resize = (Button) findViewById(R.id.resize);
        resize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoto.resize();
            }
        });

        mPhoto.setDragToFinishListener(500, new OnDragToFinishListener() {

            @Override
            public void onDragged(float fraction) {
                //System.out.println("=====" + fraction);
            }

            @Override
            public void onDismiss() {
                //System.out.println("=====dismiss");
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mPhoto.isVisible()) {
                mPhoto.reverse();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
