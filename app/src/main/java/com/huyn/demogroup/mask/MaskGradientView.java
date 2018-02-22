package com.huyn.demogroup.mask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/7/3.
 */

public class MaskGradientView extends View {

    private Paint mPaint;
    private Bitmap bitmap;

    public MaskGradientView(Context context) {
        this(context, null);
    }

    public MaskGradientView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskGradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        //mPaint.setColor(0xee000000);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setShader(new LinearGradient(0, 0, 0, 400, 0x0000ff00, 0xff00ff00, Shader.TileMode.CLAMP));

        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawRect(0, 0, getWidth(), 400, mPaint);
    }
}
