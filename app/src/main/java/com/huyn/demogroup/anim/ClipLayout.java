package com.huyn.demogroup.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/11/2.
 */
public class ClipLayout extends FrameLayout {

    private Rect mTmpRect;
    private Path mPath;
    private Paint mPaint;

    private ImageView mImageView;

    public ClipLayout(Context context) {
        this(context, null);
    }

    public ClipLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTmpRect = new Rect();
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);

        mImageView = new ImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageResource(R.drawable.cover);
        addView(mImageView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        canvas.save();

        drawMask(canvas);
        boolean ret = super.drawChild(canvas, child, drawingTime);
        child.getHitRect(mTmpRect);

        canvas.restore();
        return ret;
    }

    private void drawMask(Canvas canvas) {
//        canvas.clipRect(0, 0, getWidth(), getHeight());

        if(mPath == null) {
            mPath = new Path();
            mPath.reset();
            mPath.addCircle(getWidth()/2, getHeight()/2, getWidth()/2, Path.Direction.CW);
        }

        canvas.clipPath(mPath, Region.Op.INTERSECT);

//        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
    }

    public void startAnim(final IAnimEndCallback endCallback) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 1f, 2f);
        animator.setDuration(1000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(endCallback != null)
                    endCallback.endAnim();
            }
        });
        animator.start();

        Rotate3DAnimation anim = new Rotate3DAnimation(0, 360, getWidth()/2, getHeight()/2, 1, false);
        anim.setDuration(1000);
        startAnimation(anim);
    }

    public void startAnim(final int width, final int height, final int x, final int y, final IAnimEndCallback endCallback) {
        final float startX = getTranslationX();
        final float startY = getTranslationY();
        final int startWidth = getWidth();
        final int startHeight = getHeight();
        final int startRadius = getWidth()/2;
        final Camera camera = new Camera();
        final Matrix matrix = new Matrix();
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float targetX = startX + (x - startX) * value;
                float targetY = startY + (y - startY) * value;
                int targetWidth = (int) (startWidth + (width -startWidth) * value);
                int targetHeight = (int) (startHeight + (height -startHeight) * value);

                FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
                params.width = targetWidth;
                params.height = targetHeight;
                setTranslationX(targetX);
                setTranslationY(targetY);
                setLayoutParams(params);

//                setRotationY(360*value);
                rotate(targetX + targetWidth/2, targetY + targetHeight/2, camera, matrix, 360*value);

                mPath.reset();
                float radius = startRadius*(1-value);
                mPath.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), radius, radius, Path.Direction.CW);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(endCallback != null)
                    endCallback.endAnim();
            }
        });
        animator.start();

        System.out.println("------------------" + startWidth + "/" + startHeight + "/" + width + "/" + height);
//
//        Rotate3DAnimation anim = new Rotate3DAnimation(0, 360, getWidth()/2, getHeight()/2, 1, false);
//        anim.setDuration(1000);
//        startAnimation(anim);
    }

    private void rotate(float centerX, float centerY, Camera camera, Matrix matrix, float degrees) {
        // 将当前的摄像头位置保存下来，以便变换进行完成后恢复成原位，
        camera.save();
        camera.translate(0.0f, 0.0f, 0f);

        // 是给我们的View加上旋转效果，在移动的过程中，视图还会移Y轴为中心进行旋转。
        camera.rotateY(degrees);
        // 是给我们的View加上旋转效果，在移动的过程中，视图还会移X轴为中心进行旋转。
        // camera.rotateX(degrees);

        // 这个是将我们刚才定义的一系列变换应用到变换矩阵上面，调用完这句之后，我们就可以将camera的位置恢复了，以便下一次再使用。
        camera.getMatrix(matrix);
        // camera位置恢复
        camera.restore();

        // 以View的中心点为旋转中心,如果不加这两句，就是以（0,0）点为旋转中心
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }

    public void setProgress(float percent) {
        mPath.reset();
//        mPath.addCircle(centerX, centerY, percent / 2, Path.Direction.CW);
        float radius = getWidth()/2/percent;
        mPath.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), radius, radius, Path.Direction.CW);



//        setRotationY((percent-1)*90);

        invalidate();
    }

    public void reverse(final int width, final int height, final int x, final int y) {
        final float startX = getTranslationX();
        final float startY = getTranslationY();
        final int startWidth = getWidth();
        final int startHeight = getHeight();
        final int targetRadius = width/2;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float targetX = startX + (x - startX) * value;
                float targetY = startY + (y - startY) * value;
                int targetWidth = (int) (startWidth + (width -startWidth) * value);
                int targetHeight = (int) (startHeight + (height -startHeight) * value);

                FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
                params.width = targetWidth;
                params.height = targetHeight;
                setTranslationX(targetX);
                setTranslationY(targetY);
                setLayoutParams(params);

                System.out.println("------" + targetWidth + "/" + targetHeight);

                setRotationY(-360*(1-value));

                mPath.reset();
                float radius = targetRadius*value;
                mPath.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), radius, radius, Path.Direction.CW);
            }
        });
        animator.start();
    }

    public interface IAnimEndCallback {
        public void endAnim();
    }

}
