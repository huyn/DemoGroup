package com.huyn.demogroup.clipchild;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/11/29.
 */
public class ClipChildAnimActivity extends Activity {

    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipchild_layout);

        mImg = (ImageView) findViewById(R.id.icon);
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorHelper.voteAnimation(mImg, true, true);
            }
        });
    }
}
