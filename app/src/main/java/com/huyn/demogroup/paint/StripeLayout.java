package com.huyn.demogroup.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/11/14.
 */
public class StripeLayout extends FrameLayout {

    private Paint mPaint;

    private Bitmap bitmap;
    private Shader shader;

    public StripeLayout(Context context) {
        this(context, null);
    }

    public StripeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StripeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.stripe);

        shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mPaint.setShader(shader);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//        Rect mTmpRect = new Rect();
//        child.getHitRect(mTmpRect);

        Rect mTmpRect = new Rect(0, 0, getWidth(), getHeight());

        drawBg(canvas, mTmpRect);

        boolean ret = super.drawChild(canvas, child, drawingTime);
        return ret;
    }

    private void drawBg(Canvas canvas, Rect rect) {
        RectF rectF = new RectF(rect.left, rect.top, rect.right, rect.bottom);
        canvas.drawRoundRect(rectF, 20, 20, mPaint);
    }

}
