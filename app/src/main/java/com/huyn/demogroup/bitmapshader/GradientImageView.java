package com.huyn.demogroup.bitmapshader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/5/22.
 */

public class GradientImageView extends View {
    Bitmap bitmap;
    Paint paint = new Paint();

    public GradientImageView(Context context) {
        this(context, null);
    }

    public GradientImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.wallpaper);
        Shader shaderA = new LinearGradient(0, 0, bitmap.getWidth(), 0, 0xffffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        Shader shaderB = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(new ComposeShader(shaderA, shaderB, PorterDuff.Mode.SRC_IN));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
    }
}
