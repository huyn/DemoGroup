package com.huyn.demogroup.drawbitmap;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import com.huyn.demogroup.R;
import com.huyn.demogroup.jni.NativeJniUtil;
import com.huyn.demogroup.util.FileUtil;

import net.bither.util.NativeUtil;

/**
 * Created by huyaonan on 2017/8/15.
 */

public class DrawbitmapActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawbitmap);

        findViewById(R.id.drawbitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DrawTask("test_drawbitmap", "room").execute();
                //new DrawTask(R.drawable.test_new, "girl").execute();
            }
        });

        if(!hasSDWritePermission(this))
            requestSDWritePermission(this);

        System.out.println("++++isSDCardAvailable: " + isSDCardAvailable());
    }

    @TargetApi(23)
    public static boolean hasSDWritePermission(Context context) {
        if(Build.VERSION.SDK_INT < 23)
            return true;
        PackageManager pm = context.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName()));
    }

    @TargetApi(23)
    public static void requestSDWritePermission(Context context) {
        if(Build.VERSION.SDK_INT < 23)
            return;
        ((Activity) context).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
            return true;
        return false;
    }

    /**
     * 加载lib下两个so文件
     */
    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("bitherjni");
//        System.loadLibrary("jnisave");
    }

    private class DrawTask extends AsyncTask<Void, Void, Boolean> {

        private String fileName;
        private String tag;
        public DrawTask(String fileName, String tag) {
            this.fileName = fileName;
            this.tag = tag;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + fileName + ".jpg");
                int targetW = bitmap.getWidth();
                int targetH = bitmap.getHeight();
                System.out.println("++++" + targetW + "/" + targetH);

                String targetSrc = getCacheFile(1);
                long startTime = System.currentTimeMillis();
                FileUtil.saveBitmap(bitmap, targetSrc);
                System.out.println("skia save cost : " + (System.currentTimeMillis() - startTime));
                saveAndStartScanner(DrawbitmapActivity.this, targetSrc);

                startTime = System.currentTimeMillis();
                String jniPath = getCacheFile(2);
                String result = NativeUtil.saveBitmap(bitmap, 100, jniPath, true);
//                int result = NativeJniUtil.save(bitmap, getCacheFile(2), 100);
                System.out.println("libjpeg save cost : " + (System.currentTimeMillis() - startTime) + "/result : " + result);
                saveAndStartScanner(DrawbitmapActivity.this, jniPath);

                Bitmap dst = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(dst);

                canvas.drawBitmap(bitmap, new Rect(0, 0, targetW, targetH), new RectF(0, 0, targetW, targetH), null);

                //canvas.save();
                canvas.save(Canvas.ALL_SAVE_FLAG);
                canvas.restore();

                /**保存一张无水印的*/
                String target = getCacheFile(3);
                System.out.println("++++targetfile : " + target);
                FileUtil.saveBitmap(dst, target);
                saveAndStartScanner(DrawbitmapActivity.this, target);

                bitmap.recycle();
                dst.recycle();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean.booleanValue()) {
                System.out.println("success...");
            }
        }

        public void saveAndStartScanner(final Context context, String... path) {
            try {
                MediaScannerConnection.scanFile(context, path, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getCacheFile(int type) {
//            String root = Environment.getExternalStorageDirectory() + "/demogroup/";
//            File rootFile = new File(root);
//            if(!rootFile.exists())
//                rootFile.mkdirs();
//            return root + System.currentTimeMillis() + ".jpg";
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + type + "_" + System.currentTimeMillis() + ".jpg";
        }

        private String getCacheFileAsPng(int type) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + type + "_" + System.currentTimeMillis() + ".png";
        }
    }


}
