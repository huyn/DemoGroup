package com.huyn.demogroup.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by huyaonan on 16/11/14.
 */
public class RadialView extends View {

    private Paint mPaint;

    public RadialView(Context context) {
        this(context, null);
    }

    public RadialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        LinearGradient linear = new LinearGradient(0, height/2, width, height/2, new int[]{Color.RED, Color.BLUE, Color.RED}, null, Shader.TileMode.CLAMP);
        LinearGradient linear2 = new LinearGradient(width/2, 0, width/2, height, new int[]{Color.RED, Color.BLUE, Color.RED}, null, Shader.TileMode.CLAMP);
        ComposeShader shader = new ComposeShader(linear, linear2, PorterDuff.Mode.LIGHTEN);

        mPaint.setShader(shader);

        canvas.drawRect(0, 0, width, height, mPaint);
    }
}
