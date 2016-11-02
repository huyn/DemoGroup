package com.huyn.demogroup.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by huyaonan on 16/11/2.
 */
public class ClipView extends View {
    private Path path;
//	private Paint paint;

    int width, height;

    int centerX, centerY;

    int shaderAlpha = 0;
    int px, py, shaderW, shaderH, shaderWidth = 0;

    Paint shaderPaint;

    private int topMaskHeight = 0;

    private Paint paintTop;
    private Paint paintBottom;

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayer();
//		paint = new Paint();
//		paint.setAntiAlias(true);
//		paint.setColor(Color.WHITE);
        initPath(getWidth(), getHeight());

        paintTop = new Paint();
        paintTop.setColor(0xffd7d7d7);
        paintBottom = new Paint();
        paintBottom.setColor(0xffe9e8e8);

        shaderPaint = new Paint();
        shaderPaint.setAntiAlias(true);

//		centerX = centerY = 300;

        topMaskHeight = 400;
    }

    /**
     * setShadowLayer这个方法不支持硬件加速，必须先关闭硬件加速
     */
    @TargetApi(11)
    private void setLayer() {
//		if(Build.VERSION.SDK_INT >= 11) {
//			// If the application is hardware accelerated,
//			// must disable it for this view.
//			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//		}
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            initPath(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.clipRect(0, 0, width, height);
//        canvas.clipPath(path, Region.Op.XOR);
        canvas.clipPath(path, Region.Op.INTERSECT);

//		canvas.drawPath(path, paint);

        canvas.drawRect(0, 0, width, topMaskHeight, paintTop);
        canvas.drawRect(0, topMaskHeight, width, height, paintBottom);

//		shaderPaint.setShadowLayer(shaderWidth, shaderWidth, shaderWidth, Color.argb(shaderAlpha, 0, 0, 0));
//		canvas.drawRect(px, py, px + shaderW, py + shaderH, shaderPaint);

        canvas.restore();
    }

    private void initPath(int width, int height) {
        this.width = width;
        this.height = height;

        path = new Path();
//		path.addRect(new RectF(0, 0, width, height), Path.Direction.CW);
        path.addCircle(0, 0, 0, Path.Direction.CW);
    }

    public void drawShader(int alpha, int px, int py, int w, int h, int shaderWidth) {
        this.shaderAlpha = alpha;
        this.px = px;
        this.py = py;
        this.shaderW = w;
        this.shaderH = h;
        this.shaderWidth = shaderWidth;
        invalidate();
    }

    public void anim(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 200, width * 3);
        animator.setDuration(1000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);
                setAlpha(0);
            }
        });
        animator.start();
    }

    public void setVisible(int type) {
        if (type == View.VISIBLE) {
            initPath(width, height);
        }
        setVisibility(View.VISIBLE);
    }

    public void setProgress(float percent) {
        path.reset();
//			path.setFillType(FillType.EVEN_ODD);
//			path.addCircle(centerX, centerY, percent/2, Path.Direction.CW);
//			path.addRect(new RectF(0, 0, width, height), Path.Direction.CW);

        path.addCircle(centerX, centerY, percent / 2, Path.Direction.CW);

        invalidate();
    }
}
