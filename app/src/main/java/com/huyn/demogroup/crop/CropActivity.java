package com.huyn.demogroup.crop;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/5/23.
 */

public class CropActivity extends Activity {

    private CropRectView mCropView;
    private View doCrop;
    private ImageView mResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        mCropView = (CropRectView) findViewById(R.id.cropview);

        mCropView.setOverlayViewChangeListener(new OverlayViewChangeListener() {
            @Override
            public void onCropRectUpdated(RectF cropRect) {
                System.out.println("+++++++croprect:" + cropRect.left + "/" + cropRect.top + "/" + cropRect.right + "/" + cropRect.bottom);
            }

            @Override
            public void onConfirmed() {
                System.out.println("+++++++confirmed...");
            }
        });

        doCrop = findViewById(R.id.do_crop);
        mResult = (ImageView) findViewById(R.id.result);

        doCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //doCrop(mCropView.getCropViewRect());
                mCropView.clear();
            }
        });
    }

    private void doCrop(RectF cropRect) {
        System.out.println("+++++++doCrop:" + cropRect.left + "/" + cropRect.top + "/" + cropRect.right + "/" + cropRect.bottom);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cover);

        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        int viewW = mCropView.getWidth();
        int viewH = mCropView.getHeight();
        float ratioView = viewW*1f/viewH;
        float ratioBitmap = bitmapW*1f/bitmapH;

        float offsetTop = 0;
        float offsetLeft = 0;

        //宽度被截取
        if(ratioBitmap > ratioView) {
            offsetLeft = (bitmapW - bitmapH*ratioView)/2;
        } else {
            offsetTop = (bitmapH - bitmapW/ratioView)/2;
        }

        int targetW = 0;
        int targetH = 0;
        int targetLeft = 0;
        int targetTop = 0;
        if(offsetTop > 0) {
            float ratio = bitmapW*1f/viewW;
            targetLeft = (int) (cropRect.left * ratio);
            targetW = (int) ((cropRect.right - cropRect.left) * ratio);

            targetTop = (int) (cropRect.top * ratio + offsetTop);
            targetH = (int) ((cropRect.bottom - cropRect.top) * ratio);
        } else {
            float ratio = bitmapH*1f/viewH;
            targetLeft = (int) (cropRect.left * ratio + offsetLeft);
            targetW = (int) ((cropRect.right - cropRect.left) * ratio);

            targetTop = (int) (cropRect.top * ratio);
            targetH = (int) ((cropRect.bottom - cropRect.top) * ratio);
        }

        if(targetLeft + targetW > bitmapW)
            targetW = bitmapW - targetLeft;
        if(targetTop + targetH  > bitmapH)
            targetH = bitmapH - targetTop;
        Bitmap target = Bitmap.createBitmap(bitmap, targetLeft, targetTop, targetW, targetH);
        mResult.setImageBitmap(target);
        mResult.setVisibility(View.VISIBLE);
    }

}
