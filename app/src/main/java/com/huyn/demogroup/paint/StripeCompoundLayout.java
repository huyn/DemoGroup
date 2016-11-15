package com.huyn.demogroup.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huyn.demogroup.R;

/**
 * Created by hill on 16/11/8.
 */

public class StripeCompoundLayout extends RelativeLayout {

    private Paint mPaint;

    private Bitmap bitmap;
    private Shader shader;

    private Context mContext;

    private TextView mGoodsTitle;
    private ImageView mGoodsImg;

    private Paint mBorderPaint, mTopPaint;

    public StripeCompoundLayout(Context context) {
        this(context, null);
    }

    public StripeCompoundLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StripeCompoundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

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

        initView();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        drawBg(canvas);
        drawBorder(canvas);

        boolean ret = super.drawChild(canvas, child, drawingTime);
        return ret;
    }

    private void drawBg(Canvas canvas) {
        mPaint.setColor(Color.RED);
        Path path = new Path();
        path.addCircle(getHeight()/2, getHeight()/2, getHeight()/2, Path.Direction.CW);
        RectF rectF = new RectF(getHeight()/2, getHeight()/4, getWidth(), getHeight()*3/4);
        path.addRoundRect(rectF, 10, 10, Path.Direction.CW);
        canvas.drawPath(path, mPaint);
    }

    private void drawBorder(Canvas canvas) {
        canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2 - 10, mBorderPaint);
        RectF rectF = new RectF(getHeight()/2, getHeight()/4+10, getWidth()-10, getHeight()*3/4-10);
        canvas.drawRoundRect(rectF, 5, 5, mBorderPaint);

        canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2 - 12, mTopPaint);
        RectF rectF2 = new RectF(getHeight()/2, getHeight()/4+12, getWidth()-12, getHeight()*3/4-12);
        canvas.drawRoundRect(rectF2, 4, 4, mTopPaint);
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.stripe_layout, this);

        mGoodsTitle = (TextView) findViewById(R.id.smallgoodsitem_txt);
        mGoodsImg = (ImageView) findViewById(R.id.smallgoodsitem_img);

        mGoodsImg.setImageResource(R.drawable.cover);
        mGoodsTitle.setText("hello world");

    }

}
