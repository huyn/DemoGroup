package com.huyn.demogroup.outline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huyn.demogroup.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by huyaonan on 2017/10/11.
 */

public class OutlineView extends View {

    private Bitmap mBg;
    private Bitmap mMask;
    private Bitmap mShareBitmap;
    private Bitmap mResult, mOutline;

    private Paint mDstPaint, mSrcPaint;
    private Bitmap mSrcBitmap, mDstBitmap;
    private Canvas mSrcCanvas, mDstCanvas;

    public OutlineView(Context context) {
        this(context, null);
    }

    public OutlineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OutlineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBg = BitmapFactory.decodeResource(getResources(), R.drawable.pic_origin);

        mShareBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic_break);

        InputStream is= null;
        try {
            is = context.getAssets().open("pic_outline.png");
            mMask=BitmapFactory.decodeStream(is);
            is.close();

            initBitmap();
        } catch (IOException e) {
            e.printStackTrace();
        }


        mDstPaint = new Paint();
        mSrcPaint = new Paint();
        mDstPaint.setColor(Color.YELLOW);
        mSrcPaint.setColor(Color.BLUE);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mSrcBitmap = Bitmap.createBitmap(600,600, Bitmap.Config.ARGB_8888);
        mSrcCanvas = new Canvas(mSrcBitmap);

        mDstBitmap= Bitmap.createBitmap(600,600, Bitmap.Config.ARGB_8888);
        mDstCanvas = new Canvas(mDstBitmap);
    }

    private void initBitmap() {
        int w=mMask.getWidth();
        int h=mMask.getHeight();

        mResult = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(mResult);
        canvas.drawBitmap(mMask, 0, 0, null);

        int[] input = new int[w*h];
        mResult.getPixels(input, 0, w, 0, 0, w, h);
        changeBlackPixel2Transparent(input);
        mResult.setPixels(input, 0, w, 0, 0, w, h);

        mOutline = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas canvasOutline = new Canvas(mOutline);
        canvasOutline.drawBitmap(mMask, 0, 0, null);

        int[] inputOutline = new int[w*h];
        mOutline.getPixels(inputOutline, 0, w, 0, 0, w, h);
        showBorder(inputOutline);
        mOutline.setPixels(inputOutline, 0, w, 0, 0, w, h);
    }

    private static final int VALUE = 100;
    public static void changeBlackPixel2Transparent(int[] input) {
        int alpha;
        int value;
        int r, g, b;
        for(int i=0; i<input.length; i++) {
            alpha = input[i] & 0xff000000;
            value =  input[i] & 0x00ffffff;
            r = (input[i] & 0x00ff0000)>>16;
            g = (input[i] & 0x0000ff00)>>8;
            b = input[i] & 0x000000ff;
            //Utils.Log(TAG, "pixel value : " + value + "/at " + i + "///" + r + "/" + g + "/" + b);
            //if(r < VALUE && g < VALUE && b < VALUE)
            if(value < VALUE)
                alpha = 0x00 << 24;
//            if(r > VALUE || g > VALUE || b > VALUE)
//                alpha = 0x00 << 24;
            input[i] = alpha | value;
        }
    }

    public static void showBorder(int[] input) {
        int alpha;
        int value;
        int r, g, b;
        for(int i=0; i<input.length; i++) {
            alpha = input[i] & 0xff000000;
            value =  input[i] & 0x00ffffff;
            r = (input[i] & 0x00ff0000)>>16;
            g = (input[i] & 0x0000ff00)>>8;
            b = input[i] & 0x000000ff;
            //Utils.Log(TAG, "pixel value : " + value + "/at " + i + "///" + r + "/" + g + "/" + b);
            if(r == 0 && g == 0 && b == 0) {
            //if(r < VALUE || g < VALUE || b < VALUE) {
                alpha = 0x00 << 24;
            } else if(r > 100 || g > 100 || b > 100) {
                alpha = 0x00 << 24;
            } else {
                value = 0x00ff0000;
            }
//            if(value < VALUE || value > 100) {
//                alpha = 0x00 << 24;
//            } else {
//                value = 0x00ff0000;
//            }
            input[i] = alpha | value;
        }
    }

    /**
     * 不能为此view设置背景，否则无效，会产生黑色边框
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawBitmap(mBg, 0, 0, null);
//
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setDither(true);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
////        canvas.drawBitmap(mResult, 100, 100, paint);
//
//        canvas.drawRect(100, 100, 400, 400, paint);

        //canvas.drawColor(Color.RED);

        //works
//        //dst yellow
//        mDstCanvas.drawColor(Color.TRANSPARENT);
//        mDstCanvas.drawBitmap(mBg, 0, 0, mDstPaint);
//        //mDstCanvas.drawRect(200,200,600,600,mDstPaint);
//        canvas.drawBitmap(mDstBitmap,0,0,mDstPaint);
//        //canvas.drawRect(200,200,600,600,mDstPaint);
//        //canvas.drawBitmap(mBg,0,0,mDstPaint);
//
//        //src blue
//        mSrcCanvas.drawColor(Color.TRANSPARENT);
//        //mSrcCanvas.drawCircle(250,250,250,mSrcPaint);
//        mSrcCanvas.drawBitmap(mResult, 0, 0, mSrcPaint);
//        mSrcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//        canvas.drawBitmap(mSrcBitmap,0,0,mSrcPaint);

        //canvas.drawBitmap(mBg,0,0,null);


        if(mLinearGradient == null) {
            mLinearGradient = new LinearGradient(getWidth() / 2, -getHeight(), getWidth() / 2, 0, new int[]{0x66ff3366, 0x66ff3366, 0xffff3366, 0x00ff3366}, new float[]{0, 0.95f, 0.975f, 1f}, Shader.TileMode.CLAMP);
//            mSrcPaint.setShader(mLinearGradient);
            mGradientMatrix = new Matrix();
            bitmapShader = new BitmapShader(mShareBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            float ratio = getWidth()*1f/getHeight();
            float bRatio = mShareBitmap.getWidth()*1f/mShareBitmap.getHeight();
            float scale;
            if(ratio > bRatio) {
                scale = getWidth() * 1f/ mShareBitmap.getWidth();
            } else {
                scale = getHeight() * 1f / mShareBitmap.getHeight();
            }
            if(scale > 1) {
                // shader的变换矩阵，我们这里主要用于放大或者缩小
                Matrix mMatrix = new Matrix();
                mMatrix.setScale(scale, scale);
                // 设置变换矩阵
                bitmapShader.setLocalMatrix(mMatrix);
            }
        }

        mSrcPaint.setShader(bitmapShader);
        canvas.drawRect(0, 0, getWidth(), pos - getHeight()*0.025f, mSrcPaint);

        //每帧刷新
        mGradientMatrix.setTranslate(0, pos);
        mLinearGradient.setLocalMatrix(mGradientMatrix);
        mSrcPaint.setShader(mLinearGradient);
        canvas.drawRect(0, 0, getWidth(), pos, mSrcPaint);
        pos+=5;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mResult, 0, 0,paint);

        canvas.drawBitmap(mOutline, 0, 0, null);

        invalidate();
    }
    private int pos = 0;
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private BitmapShader bitmapShader;
}
