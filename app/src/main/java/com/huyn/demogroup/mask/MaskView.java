package com.huyn.demogroup.mask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by huyaonan on 2017/7/3.
 */

public class MaskView extends View {

    private Paint mPaint, mStrokePaint;
    private Bitmap bitmap;
    private Canvas mCanvas;

    public MaskView(Context context) {
        this(context, null);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true); //消除锯齿
        //mStrokePaint.setStyle(Paint.Style.STROKE); //绘制空心圆
        mStrokePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mStrokePaint.setColor(Color.WHITE);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0x99ff0000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight()/4;
        if (bitmap == null || canvas == null) {
            if (bitmap != null) bitmap.recycle();

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            this.mCanvas = new Canvas(bitmap);
        }

        /**
         * Draw mask
         */
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mCanvas.drawColor(0x99ff0000);

        mCanvas.drawCircle(200, 200, 100, mStrokePaint);

        canvas.drawBitmap(bitmap, 0, 0, null);

        canvas.drawRect(0, height  + 100, width, getHeight(), mPaint);

//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        canvas.drawColor(Color.RED);
//        canvas.drawCircle(200, 200, 100, mStrokePaint);
    }
}
