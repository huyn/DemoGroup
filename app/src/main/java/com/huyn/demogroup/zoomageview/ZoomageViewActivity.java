package com.huyn.demogroup.zoomageview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageViewWrapper;
import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/5/11.
 */

public class ZoomageViewActivity extends Activity implements View.OnClickListener {

    private ImageView left, right;
    private SubsamplingScaleImageViewWrapper placeholder;
    private View mRoot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_zoomageview);

        mRoot = findViewById(R.id.root);

        left = (ImageView) findViewById(R.id.left);
        right = (ImageView) findViewById(R.id.right);
        placeholder = (SubsamplingScaleImageViewWrapper) findViewById(R.id.placeholder);
        placeholder.attachRoot(mRoot);

        left.setOnClickListener(this);
        right.setOnClickListener(this);
        placeholder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
            case R.id.right:
                placeholder.preview(v);
                break;
            case R.id.placeholder:
                placeholder.dismiss();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(placeholder.getVisibility() == View.VISIBLE) {
                placeholder.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
