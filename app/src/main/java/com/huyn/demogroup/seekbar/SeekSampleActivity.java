package com.huyn.demogroup.seekbar;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.huyn.demogroup.R;

/**
 * see https://github.com/woxingxiao/BubbleSeekBar
 * Created by huyaonan on 2017/5/26.
 */

public class SeekSampleActivity extends Activity {

    ColoredRoundView coloredRoundView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seekbar);

        coloredRoundView = (ColoredRoundView) findViewById(R.id.colored_view);
        coloredRoundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coloredRoundView.toggle();
            }
        });
    }
}
