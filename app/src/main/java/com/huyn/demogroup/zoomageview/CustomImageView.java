package com.huyn.demogroup.zoomageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huyn.demogroup.R;

import java.util.Random;

/**
 * Created by huyaonan on 2017/5/12.
 */

public class CustomImageView extends View {

    Bitmap src, mask;
    Paint p = new Paint();

    public CustomImageView(Context context) {
        super(context);
        init();
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        src = BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper);
        mask = BitmapFactory.decodeResource(getResources(), R.drawable.bk_gallery_lightoff);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        canvas.drawBitmap(src, 0, 0, p);
        canvas.save();
        //随机旋转
        //canvas.rotate((float) (90 * Math.PI / 180));
        //canvas.translate(w/2, h/2);
        //canvas.rotate(90, w/2, h/2);

        int degree = 180;//new Random().nextInt(4) * 90;
        boolean reverse = degree==90||degree==270;
        if(reverse) {
            Matrix matrix = new Matrix();
            //normal
            //matrix.setScale(w*1f/mask.getWidth(), h*1f/mask.getHeight());
            //90
            //matrix.setRotate(90);
            matrix.setScale(h * 1f / mask.getWidth(), w * 1f / mask.getHeight());
            matrix.postRotate(degree, h / 2, w / 2);
            matrix.postTranslate(w / 2 - h / 2, h / 2 - w / 2);
            //matrix.postTranslate(50, 50);
            //matrix.postRotate(90);
            //matrix.setRectToRect(new RectF(0, 0, w, h), new RectF(0, 0, mask.getWidth(), mask.getHeight()), Matrix.ScaleToFit.FILL);
            canvas.drawBitmap(mask, matrix, p);
        } else {
            canvas.rotate(degree, w/2, h/2);
            canvas.drawBitmap(mask, new Rect(0, 0, mask.getWidth(), mask.getHeight()), new Rect(0, 0, w, h), p);
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
    }
}
