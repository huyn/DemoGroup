package com.huyn.demogroup.drawbitmap;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import com.huyn.demogroup.util.FileUtil;

import java.io.File;

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
                new DrawTask().execute();
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

    private class DrawTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_drawbitmap);
                String targetSrc = getCacheFile();
                FileUtil.saveBitmap(bitmap, targetSrc);
                saveAndStartScanner(DrawbitmapActivity.this, targetSrc);

                int targetW = bitmap.getWidth();
                int targetH = bitmap.getHeight();
                Bitmap dst = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(dst);

                canvas.drawBitmap(bitmap, new Rect(0, 0, targetW, targetH), new RectF(0, 0, targetW, targetH), null);

                //canvas.save();
                canvas.save(Canvas.ALL_SAVE_FLAG);
                canvas.restore();

                /**保存一张无水印的*/
                String target = getCacheFile();
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

        private String getCacheFile() {
//            String root = Environment.getExternalStorageDirectory() + "/demogroup/";
//            File rootFile = new File(root);
//            if(!rootFile.exists())
//                rootFile.mkdirs();
//            return root + System.currentTimeMillis() + ".jpg";
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + System.currentTimeMillis() + ".jpg";
        }
    }


}
