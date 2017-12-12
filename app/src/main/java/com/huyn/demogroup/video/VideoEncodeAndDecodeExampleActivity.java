package com.huyn.demogroup.video;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import com.huyn.demogroup.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by huyaonan on 2017/12/11.
 */

public class VideoEncodeAndDecodeExampleActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_example);

        findViewById(R.id.to_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runExtract();
            }
        });
        findViewById(R.id.to_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encodeToVideo();
            }
        });
    }

    private void runExtract() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    new DecodeToImages().extractMpegFrames();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void encodeToVideo() {
        new Thread(new Runnable() {
            public void run() {
                new EncodeVideoByMediaCodec().testEncodeVideoToMp4();
                //new EncodeToVideo().encode(new File(Environment.getExternalStorageDirectory() + "/testvideo"), Environment.getExternalStorageDirectory() + "/testvideo/result.mp4");
            }
        }).start();
    }

}
