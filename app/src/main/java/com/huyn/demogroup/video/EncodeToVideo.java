package com.huyn.demogroup.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.AndroidUtil;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

import java.io.File;
import java.io.IOException;

/**
 * Created by huyaonan on 2017/12/12.
 */

public class EncodeToVideo {

    private static final String TAG = "EncodeToVideo";

    void encode(File srcFile, String targetFile) {
        long start = System.currentTimeMillis();
        SequenceEncoder se = null;
        try {
            File target = new File(targetFile);
            if(target.exists())
                target.delete();
            se = SequenceEncoder.create24Fps(target);
            File[] files = srcFile.listFiles();
            int count = 0;
            for (int i = 0;i<files.length; i++) {
                if (!files[i].exists() || !(files[i].getName().endsWith(".png") || files[i].getName().endsWith(".jpg")))
                    break;
                System.out.println("encode : " + files[i].getName());

                Bitmap frame = BitmapFactory.decodeFile(files[i].getAbsolutePath());
                Picture picture = AndroidUtil.fromBitmap(frame, ColorSpace.RGB);
                se.encodeNativeFrame(picture);

                //System.out.println("+++finish " + (count++));
            }
            se.finish();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IO", e);
        }
        System.out.println("+++++cost : " + (System.currentTimeMillis() - start));
    }

}
