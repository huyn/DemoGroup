package com.huyn.demogroup.pathcrop;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/7/31.
 */

public class PathCropActivity extends Activity {
    ImageView mResult;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pathcrop);
        mResult = (ImageView) findViewById(R.id.crop_result);

        findViewById(R.id.crop_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CropTask().execute();
            }
        });
    }

    private class CropTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            long start = System.currentTimeMillis();
            System.out.println("++++++++++start:" + start);
            Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper);
            int width = src.getWidth();
            int height = src.getHeight();

            int[] input = new int[width * height];
            src.getPixels(input, 0, width, 0, 0, width, height);

            Bitmap target = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            int[] result = new int[300*300];
            parse(input, result, width);
            target.setPixels(result, 0, 300, 0, 0, 300, 300);
            System.out.println("++++++cose : " + (System.currentTimeMillis() - start));
//            Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            target.setPixels(input, 0, width, 0, 0, width, height);
            return target;
        }

        private void parse(int[] src, int[] target, int srcW) {
            for(int w=0; w<300; w++) {
                for(int h=0; h<300; h++) {
                    int index = w*300+h;
//                    if(w > 100 && w < 200 && h < 200 && h > 100) {
//                        target[index] = src[w*srcW+h];
//                    } else {
//                        target[index] = 0x00000000;
//                    }
                    if(w < 100 && h < 100) {
                        target[index] = 0x00000000;
                        continue;
                    } else if(w < 100 && h > 200) {
                        target[index] = 0x00000000;
                        continue;
                    } else if(w > 200 && h < 100) {
                        target[index] = 0x00000000;
                        continue;
                    } else if(w > 200 && h > 200) {
                        target[index] = 0x00000000;
                        continue;
                    }
                    target[index] = src[w*srcW+h];
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null)
                mResult.setImageBitmap(bitmap);
        }
    }

}
