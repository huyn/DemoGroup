package com.huyn.demogroup.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.nio.ByteBuffer;

import jp.co.cyberagent.android.gpuimage.GPUImageNativeLibrary;

/**
 * Created by huyaonan on 2017/12/15.
 */

public class SegmentUtil {

    public static Bitmap doSegment(File mSrc, File mask, File mPaint) {
        return doSegment(mSrc.getAbsolutePath(), mask.getAbsolutePath(), mPaint.getAbsolutePath());
    }

    public static Bitmap doSegment(String mSrc, String mask, String mPaint) {
        Bitmap srcBitmap = decodeFile(mSrc, false);
        ByteBuffer bufferA = GPUImageNativeLibrary.storeBitmapData(srcBitmap);
        srcBitmap.recycle();

        Bitmap maskBitmap = decodeFile(mask, false);
        ByteBuffer bufferB = GPUImageNativeLibrary.storeBitmapData(maskBitmap);
        maskBitmap.recycle();

        Bitmap paintBitmap = decodeFile(mPaint);
        GPUImageNativeLibrary.segmentBitmap(paintBitmap, bufferA, bufferB);
        GPUImageNativeLibrary.freeBitmapData(bufferA);
        GPUImageNativeLibrary.freeBitmapData(bufferB);
        return paintBitmap;
    }

    public static Bitmap decodeFile(String path) {
        return decodeFile(path, true);
    }

    public static Bitmap decodeFile(String path, boolean toRgb565) {
        if(toRgb565) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFile(path, options);
        } else {
            return BitmapFactory.decodeFile(path);
        }
    }

    public static Bitmap decodeFile(String path, BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap decodeFile(String path, int maxSize) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, decodeOptions);

        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        decodeOptions.inInputShareable = true;
        decodeOptions.inPurgeable = true;
        // Do we need this or is it okay since API 8 doesn't support it?
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        int inSampleSize = findSampleSize(actualWidth, actualHeight, maxSize, maxSize);
        decodeOptions.inSampleSize = inSampleSize;
        return decodeFile(path, decodeOptions);
    }

    public static int findSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        return (int) ratio;
    }

}
