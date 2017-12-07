package com.huyn.demogroup.shadow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/12/7.
 */

public class ShadowView extends View {

    private Bitmap bitmap;

    public ShadowView(Context context) {
        this(context, null);
    }

    public ShadowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cover);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 建立Paint 物件
        Paint paint1 = new Paint();
        // 设定颜色
        paint1.setColor(0xFFFFFF00);
        // 设定阴影(柔边, X 轴位移, Y 轴位移, 阴影颜色)
        paint1.setShadowLayer(5, 3, 3, 0xFFFF00FF);
        // 实心矩形& 其阴影
        canvas.drawText("我很爱你", 20,40,paint1);
        Paint paint2 = new Paint();
        paint2.setColor(Color.GREEN);
        paint2.setShadowLayer(10, 5, 2, Color.YELLOW);
        canvas.drawText("你真傻", 20,60,paint2);

        Paint paint3 = new Paint();
        paint3.setColor(Color.RED);
        paint3.setAntiAlias(true);
        paint3.setShadowLayer(30, 5, 2, Color.GREEN);
        canvas.drawCircle(50, 130,30, paint3);

        paint3.setShadowLayer(30, 0, 10, Color.BLACK);
        paint3.setColor(Color.WHITE);
        canvas.drawRect(200, 100, 500, 400, paint3);

        /*Paint paint4 = new Paint();
        paint4.setShadowLayer(5, 8, 7, Color.DKGRAY);
        canvas.drawBitmap(bitmap, 50, 200, paint4);*/
    }
}
