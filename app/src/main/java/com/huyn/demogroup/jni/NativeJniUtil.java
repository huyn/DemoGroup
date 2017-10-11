package com.huyn.demogroup.jni;

import android.graphics.Bitmap;

/**
 * Created by huyaonan on 2017/8/16.
 */

public class NativeJniUtil {
    /**
     * 加载lib下两个so文件
     */
//    static {
//        System.loadLibrary("jpegbither");
//        System.loadLibrary("jnisave");
//    }

    public static native int save(Bitmap bit, String filaPath, int quality);

}
