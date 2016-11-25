package com.huyn.demogroup.paint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.huyn.demogroup.R;

import java.util.Random;

/**
 * Created by huyaonan on 16/11/14.
 */
public class PaintShaderActivity extends Activity {

    ProgressView mProgress;
    VSProgressView mVSProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_shader);

        mProgress = (ProgressView) findViewById(R.id.progress_1);
        mVSProgress = (VSProgressView) findViewById(R.id.progress_2);

        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.updateProgress(Math.abs(new Random(1).nextFloat()), true);
            }
        });

        findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVSProgress.updateProgress(Math.abs(new Random(1).nextFloat()), true);
            }
        });

        findViewById(R.id.to_cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(PaintShaderActivity.this, CardViewActivity.class));
//                startActivity(new Intent(PaintShaderActivity.this, TestWebViewActivity.class));
                startActivity(new Intent(PaintShaderActivity.this, TestStripeAnimActivity.class));
            }
        });
    }
}
