package com.huyn.demogroup.util;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by huyaonan on 17/2/8.
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    public static String saveBitmap(Bitmap b, File target) {
        try {
            if(!target.exists()) {
                target.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(target);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            //b.recycle();
            return target.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveBitmap(Bitmap b, String path) {
        File target = new File(path);
        return saveBitmap(b, target);
    }

    public static String savePng(Bitmap b, String path) {
        try {
            File target = new File(path);
            if(!target.exists())
                target.createNewFile();
            FileOutputStream fout = new FileOutputStream(target);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
//            b.recycle();
            return target.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
