package com.huyn.demogroup.perspective;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.huyn.demogroup.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huyaonan on 2017/10/26.
 */

public class PerspectiveView extends View {
    /**
     * Logging tag.
     */
    public static final String LOG_TAG = "PerspectiveView";
    /**
     * The paint for the path.
     */
    private Paint mPaintCover;
    /**
     * All the paths provided to the view. Both from Path and Svg.
     */
    private List<SvgUtils.SvgPath> paths = new ArrayList<>();
    /**
     * This is a lock before the view is redrawn
     * or resided it must be synchronized with this object.
     */
    private final Object mSvgLock = new Object();
    /**
     * Thread for working with the object above.
     */
    private Thread mLoader;

    /**
     * The svg image from the raw directory.
     */
    private int svgResourceId;

    /**
     * The width of the view.
     */
    private int width;
    /**
     * The height of the view.
     */
    private int height;

    /**
     * Default constructor.
     *
     * @param context The Context of the application.
     */
    public PerspectiveView(Context context) {
        this(context, null);
    }

    /**
     * Default constructor.
     *
     * @param context The Context of the application.
     * @param attrs   attributes provided from the resources.
     */
    public PerspectiveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Default constructor.
     *
     * @param context  The Context of the application.
     * @param attrs    attributes provided from the resources.
     * @param defStyle Default style.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PerspectiveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getFromAttributes(context, attrs);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic_human_cat);
    }

    private Bitmap mBitmap;

    /**
     * Get all the fields from the attributes .
     *
     * @param context The Context of the application.
     * @param attrs   attributes provided from the resources.
     */
    private void getFromAttributes(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PerspectiveView);
        try {
            if (a != null) {
                svgResourceId = a.getResourceId(R.styleable.PerspectiveView_svg, 0);
            }
        } finally {
            if (a != null) {
                a.recycle();
            }
            //to draw the svg in first show , if we set fill to true
            invalidate();
        }

        mPaintCover = new Paint();
        mPaintCover.setAntiAlias(true);
        mPaintCover.setColor(Color.GRAY);
        //设置图形混合方式，这里使用PorterDuff.Mode.XOR模式，与底层重叠部分设为透明
        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
        mPaintCover.setXfermode(mode);
    }

    /**
     * This refreshes the paths before draw and resize.
     */
    private void updatePathsPhaseLocked() {
        final int count = paths.size();
        for (int i = 0; i < count; i++) {
            SvgUtils.SvgPath svgPath = paths.get(i);
            svgPath.path.reset();
            svgPath.measure.getSegment(0.0f, svgPath.length, svgPath.path, true);
            // Required only for Android 4.4 and earlier
            svgPath.path.rLineTo(0.0f, 0.0f);
        }
    }

    public void directlyShow() {
        synchronized (mSvgLock) {
            updatePathsPhaseLocked();
        }
        invalidate();
    }

    private Path mPath = new Path();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mSvgLock) {
            final int count = paths.size();
            if(count == 0)
                return;
            canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()), new RectF(0, 0, getWidth(), getHeight()), null);
            mPath.reset();
            for (int i = 0; i < count; i++) {
                final SvgUtils.SvgPath svgPath = paths.get(i);
                final Path path = svgPath.path;
                mPath.addPath(path);
            }
            canvas.drawPath(mPath, mPaintCover);
        }
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mLoader != null) {
            try {
                mLoader.join();
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "Unexpected error", e);
            }
        }
        if (svgResourceId != 0) {
            mLoader = new Thread(new Runnable() {
                @Override
                public void run() {

                    SvgUtils svgUtils = new SvgUtils();
                    svgUtils.load(getContext(), svgResourceId);

                    synchronized (mSvgLock) {
                        width = w - getPaddingLeft() - getPaddingRight();
                        height = h - getPaddingTop() - getPaddingBottom();
                        System.out.println("+++++width/height:" + width + "/" + height);
                        paths = svgUtils.getPathsForViewport(width, height);
                        updatePathsPhaseLocked();

                        post(new Runnable() {
                            @Override
                            public void run() {
                                directlyShow();
                            }
                        });
                    }
                }
            }, "SVG Loader");
            mLoader.start();
        }
    }

    public void update(List<SvgUtils.SvgPath> paths) {
        this.paths = paths;
        invalidate();
    }

    public void startAnim(final List<List<SvgUtils.SvgPath>> paths) {
        final int size = paths.size();
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(600);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                int index = (int) (value * size);
                if(index >= size -1)
                    index = size - 1;

                update(paths.get(index));
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                paths.clear();
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

}
