package com.huyn.demogroup.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/11/14.
 */
public class StripeView extends View {

    private Paint mPaint;

    private Bitmap bitmap;
    private Shader shader;

    public StripeView(Context context) {
        this(context, null);
    }

    public StripeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StripeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.stripe);

        shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mPaint.setShader(shader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, 20, 20, mPaint);
    }
}
