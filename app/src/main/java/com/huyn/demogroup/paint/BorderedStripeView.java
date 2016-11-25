package com.huyn.demogroup.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/11/24.
 */
public class BorderedStripeView extends View {

    private Paint mPaint, mBorderPaint, mTopPaint;

    private Bitmap bitmap;
    private Shader shader;

    public BorderedStripeView(Context context) {
        this(context, null);
    }

    public BorderedStripeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderedStripeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.stripe);

        shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mPaint.setShader(shader);

        mBorderPaint = new Paint();
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setAntiAlias(true);

        mTopPaint = new Paint();
        mTopPaint.setColor(Color.WHITE);
        mTopPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, 20, 20, mPaint);

        RectF rectFBorder = new RectF(10, 10, width-10, height-10);
        canvas.drawRoundRect(rectFBorder, 10, 10, mBorderPaint);

        RectF rectTop = new RectF(12, 12, width-12, height-12);
        canvas.drawRoundRect(rectTop, 8, 8, mTopPaint);
    }
}
