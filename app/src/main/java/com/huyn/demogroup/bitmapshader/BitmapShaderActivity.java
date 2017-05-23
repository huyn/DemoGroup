package com.huyn.demogroup.bitmapshader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/5/22.
 */

public class BitmapShaderActivity extends Activity {

    private View mClick;
    private ImageView mMask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmapshader);

        mClick = findViewById(R.id.click_to_convert);
        mClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImgProcessor(R.drawable.timg_face).startTask(BitmapShaderActivity.this, new ImgProcessor.OnProcessListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        mMask.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }
        });

        mMask = (ImageView) findViewById(R.id.mask);
    }
}
