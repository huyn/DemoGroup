package com.huyn.demogroup.paint;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.huyn.demogroup.R;

public class DashLineView extends View {
    private int y;
    Paint paint = new Paint();
    Path path = new Path();
    PathEffect effects = new DashPathEffect(new float[]{3, 3, 3, 3}, 1);
    private int lineColor;

    public DashLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typeArray = context.obtainStyledAttributes(attrs,
                R.styleable.DashLineView);
        lineColor = typeArray.getInteger(R.styleable.DashLineView_lineColor, 0xffcccccc);
        int effect = typeArray.getInteger(R.styleable.DashLineView_patheffect, 3);
        effects = new DashPathEffect(new float[]{effect, effect, effect, effect}, 1);
        typeArray.recycle();
    }

    public DashLineView(Context context, int y) {
        super(context);
        this.y = y;
    }

    public void setY(int y) {
        this.y = y;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        y = getMeasuredWidth();
        int x = getMeasuredHeight() / 2;

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(lineColor);
        paint.setStrokeWidth(2);

        path.moveTo(0, x);
        path.lineTo(y, x);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }

}
