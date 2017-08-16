package com.huyn.demogroup.lottie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 执行擦除动画的layout
 * 原理是重写dispatchDraw方法，替换canvas
 * 然后通过clipPath方法指定canvas的绘制区域，用path的好处是可以定制各种形状
 * 显示全图的话，path就是[0, 0, width, height]这个区域
 * 全部不显示的话，path就是[0, 0, 0, 0]
 * 执行擦除动画就是动态改变path的区域！
 * 以上
 * Created by huyaonan on 2017/8/1.
 */

public class WipableLayout extends LinearLayout {

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint mPaint;

    private Path mPath;

    public WipableLayout(Context context) {
        super(context);
        init();
    }

    public WipableLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WipableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPath = new Path();
    }

    /**
     * TODO 动画有时候会不动，没关系，动画执行的时候，用继承的canvas，等动画执行完毕后再替换canvas
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
//        int width = getWidth();
//        int height = getHeight();
//
//        if(bitmap == null) {
//            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            bitmapCanvas = new Canvas(bitmap);//该画布为bitmap。
//            mPaint = new Paint();
//        }
//        canvas.drawBitmap(bitmap, 0, 0, mPaint);
//        //final int saveCount = bitmapCanvas.getSaveCount();
//        //bitmapCanvas.save();
////        bitmapCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
////
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.lineTo(300, 0);
        mPath.lineTo(400, 400);
        mPath.lineTo(0, 300);
        mPath.close();
////        bitmapCanvas.clipPath(mPath);
//        super.dispatchDraw(bitmapCanvas);
//        //bitmapCanvas.restoreToCount(saveCount);

        System.out.println("++++++dispatch draw");
        canvas.clipPath(mPath);
//        canvas.clipRect(0, 0, 200, 200);
        super.dispatchDraw(canvas);
    }
}
