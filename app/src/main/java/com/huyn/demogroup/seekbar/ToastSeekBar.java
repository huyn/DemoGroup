package com.huyn.demogroup.seekbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.huyn.demogroup.R;

import java.math.BigDecimal;

import static com.huyn.demogroup.seekbar.BubbleUtils.dp2px;
import static com.huyn.demogroup.seekbar.BubbleUtils.sp2px;

public class ToastSeekBar extends View {

    private float mMin; // min
    private float mMax; // max
    private float mProgress; // real time value
    private int mTrackSize; // height of right-track(on the right of thumb)
    private int mSecondTrackSize; // height of left-track(on the left of thumb)
    private int mThumbRadius; // radius of thumb
    private boolean mThumbSolid=true;//soid
    private int mTrackColor; // color of right-track
    private int mSecondTrackColor; // color of left-track
    private int mThumbColor; // color of thumb
    private long mAnimDuration; // duration of animation
    private boolean isAlwaysShowBubble; // bubble shows all time

    private int mToastTextSize; // text size of bubble-progress
    private int mToastTextColor; // text color of bubble-progress

    private float mDelta; // max - min
    private float mThumbCenterX; // X coordinate of thumb's center
    private float mTrackLength; // pixel length of whole track
    private boolean isThumbOnDragging; // is thumb on dragging or not
    private int mTextSpace; // space between text and track
    private boolean triggerBubbleShowing;
    private boolean isTouchToSeek; // touch anywhere on track to quickly seek

    private OnProgressChangedListener mProgressListener; // progress changing listener
    private float mLeft; // space between left of track and left of the view
    private float mRight; // space between right of track and left of the view
    private Paint mPaint;
    private Rect mRectText;

    private WindowManager mWindowManager;
    private ToastView mToastView;
    private int mToastRadius;
    private float mToastCenterRawSolidX;
    private float mToastCenterRawSolidY;
    private float mToastCenterRawX;
    private WindowManager.LayoutParams mLayoutParams;
    private int[] mPoint = new int[2];
    private int minHeight;

    public ToastSeekBar(Context context) {
        this(context, null);
    }

    public ToastSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToastSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        minHeight = dp2px(2);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToastSeekBar, defStyleAttr, 0);
        mMin = a.getFloat(R.styleable.ToastSeekBar_tsb_min, 0.0f);
        mMax = a.getFloat(R.styleable.ToastSeekBar_tsb_max, 100.0f);
        mProgress = a.getFloat(R.styleable.ToastSeekBar_tsb_progress, mMin);
        mTrackSize = a.getDimensionPixelSize(R.styleable.ToastSeekBar_tsb_track_size, minHeight);
        mSecondTrackSize = a.getDimensionPixelSize(R.styleable.ToastSeekBar_tsb_second_track_size, minHeight);
        if(mSecondTrackSize < mTrackSize)
            mSecondTrackSize = mTrackSize;
        mThumbRadius = a.getDimensionPixelSize(R.styleable.ToastSeekBar_tsb_thumb_radius, mSecondTrackSize + minHeight);
        if(mThumbRadius < mSecondTrackSize)
            mThumbRadius = mSecondTrackSize + minHeight;
        mThumbSolid = a.getBoolean(R.styleable.ToastSeekBar_tsb_thumb_solid, false);
        mTrackColor = a.getColor(R.styleable.ToastSeekBar_tsb_track_color,
                ContextCompat.getColor(context, R.color.colorPrimary));
        mSecondTrackColor = a.getColor(R.styleable.ToastSeekBar_tsb_second_track_color,
                ContextCompat.getColor(context, R.color.colorAccent));
        mThumbColor = a.getColor(R.styleable.ToastSeekBar_tsb_thumb_color, mSecondTrackColor);
        mToastTextSize = a.getDimensionPixelSize(R.styleable.ToastSeekBar_tsb_toast_text_size, sp2px(14));
        mToastTextColor = a.getColor(R.styleable.ToastSeekBar_tsb_toast_text_color, Color.WHITE);
        int duration = a.getInteger(R.styleable.ToastSeekBar_tsb_anim_duration, -1);
        mAnimDuration = duration < 0 ? 200 : duration;
        isAlwaysShowBubble = a.getBoolean(R.styleable.ToastSeekBar_tsb_always_show_bubble, false);
        isTouchToSeek = a.getBoolean(R.styleable.ToastSeekBar_tsb_touch_to_seek, true);
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mRectText = new Rect();

        mTextSpace = minHeight;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // init ToastView
        mToastView = new ToastView(context);
        mToastView.setProgressText(String.valueOf(getProgress()));

        initConfigByPriority();
        calculateRadiusOfBubble();
    }

    private void initConfigByPriority() {
        if (mMin == mMax) {
            mMin = 0.0f;
            mMax = 100.0f;
        }
        if (mMin > mMax) {
            float tmp = mMax;
            mMax = mMin;
            mMin = tmp;
        }
        if (mProgress < mMin) {
            mProgress = mMin;
        }
        if (mProgress > mMax) {
            mProgress = mMax;
        }
        if (mSecondTrackSize < mTrackSize) {
            mSecondTrackSize = mTrackSize + dp2px(2);
        }
        if (mThumbRadius <= mSecondTrackSize) {
            mThumbRadius = mSecondTrackSize + dp2px(2);
        }
        mDelta = mMax - mMin;

        if (isAlwaysShowBubble) {
            setProgress(mProgress);
        }
    }

    /**
     * Calculate radius of bubble according to the Min and the Max
     */
    private void calculateRadiusOfBubble() {
        mPaint.setTextSize(mToastTextSize);

        // 计算滑到两端气泡里文字需要显示的宽度，比较取最大值为气泡的半径
        String text;
            text = getMinText();
        mPaint.getTextBounds(text, 0, text.length(), mRectText);
        int w1 = (mRectText.width() + mTextSpace * 2) >> 1;

            text = getMaxText();
        mPaint.getTextBounds(text, 0, text.length(), mRectText);
        int w2 = (mRectText.width() + mTextSpace * 2) >> 1;

        mToastRadius = dp2px(14); // default 14dp
        int max = Math.max(mToastRadius, Math.max(w1, w2));
        mToastRadius = max + mTextSpace;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = mThumbRadius * 2; // 默认高度为拖动时thumb圆的直径
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);

        mLeft = getPaddingLeft() + mThumbRadius;
        mRight = getMeasuredWidth() - getPaddingRight() - mThumbRadius;

        mTrackLength = mRight - mLeft;

        mToastView.measure(widthMeasureSpec, heightMeasureSpec);

        locatePositionOnScreen();
    }

    /**
     * In fact there two parts of the BubbleSeeBar, they are the BubbleView and the SeekBar.
     * <p>
     * The BubbleView is added to Window by the WindowManager, so the only connection between
     * BubbleView and SeekBar is their origin raw coordinates on the screen.
     * <p>
     * It's easy to compute the coordinates(mToastCenterRawSolidX, mToastCenterRawSolidY) of point
     * when the Progress equals the Min. Then compute the pixel length increment when the Progress is
     * changing, the result is mToastCenterRawX. At last the WindowManager calls updateViewLayout()
     * to update the LayoutParameter.x of the BubbleView.
     * <p>
     * 气泡BubbleView实际是通过WindowManager动态添加的一个视图，因此与SeekBar唯一的位置联系就是它们在屏幕上的
     * 绝对坐标。
     * 先计算进度mProgress为mMin时BubbleView的中心坐标（mToastCenterRawSolidX，mToastCenterRawSolidY），
     * 然后根据进度来增量计算横坐标mToastCenterRawX，再动态设置LayoutParameter.x，就实现了气泡跟随滑动移动。
     */
    private void locatePositionOnScreen() {
        getLocationOnScreen(mPoint);

        mToastCenterRawSolidX = mPoint[0] + mLeft - mToastView.getMeasuredWidth() / 2f;
        mToastCenterRawX = mToastCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;
        mToastCenterRawSolidY = mPoint[1] - mToastView.getMeasuredHeight();
        mToastCenterRawSolidY -= dp2px(24);
        if (BubbleUtils.isMIUI()) {
            mToastCenterRawSolidY += dp2px(4);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float xLeft = mLeft;
        float xRight = mRight;
        float yTop = getPaddingTop() + mThumbRadius;

        if (!isThumbOnDragging || isAlwaysShowBubble) {
            mThumbCenterX = mTrackLength / mDelta * (mProgress - mMin) + xLeft;
        }

        // draw track
        mPaint.setColor(mSecondTrackColor);
        mPaint.setStrokeWidth(mSecondTrackSize);
        canvas.drawLine(xLeft, yTop, mThumbCenterX, yTop, mPaint);

        // draw second track
        mPaint.setColor(mTrackColor);
        mPaint.setStrokeWidth(mTrackSize);
        canvas.drawLine(mThumbCenterX, yTop, xRight, yTop, mPaint);

        // draw thumb
        mPaint.setColor(mThumbColor);
        canvas.drawCircle(mThumbCenterX, yTop, mThumbRadius, mPaint);
        if(!mThumbSolid) {
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(mThumbCenterX, yTop, mThumbRadius - minHeight, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (!isAlwaysShowBubble)
            return;

        if (visibility != VISIBLE) {
            hideBubble();
        } else {
            if (triggerBubbleShowing) {
                showBubble();
            }
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        hideBubble();
        super.onDetachedFromWindow();
    }

    float dx;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isThumbOnDragging = isThumbTouched(event);
                if (isThumbOnDragging) {
                    if (isAlwaysShowBubble && !triggerBubbleShowing) {
                        triggerBubbleShowing = true;
                    }
                    showBubble();
                    invalidate();
                    System.out.println("++++++++down 1");
                } else if (isTouchToSeek && isTrackTouched(event)) {
                    if (isAlwaysShowBubble) {
                        hideBubble();
                        triggerBubbleShowing = true;
                    }

                    mThumbCenterX = event.getX();
                    if (mThumbCenterX < mLeft) {
                        mThumbCenterX = mLeft;
                    }
                    if (mThumbCenterX > mRight) {
                        mThumbCenterX = mRight;
                    }
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                    mToastCenterRawX = mToastCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;

                    showBubble();
                    invalidate();
                    System.out.println("++++++++down 2");
                }

                dx = mThumbCenterX - event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                if (isThumbOnDragging) {
                    mThumbCenterX = event.getX() + dx;
                    if (mThumbCenterX < mLeft) {
                        mThumbCenterX = mLeft;
                    }
                    if (mThumbCenterX > mRight) {
                        mThumbCenterX = mRight;
                    }
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;

                    mToastCenterRawX = mToastCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;
                    mLayoutParams.x = (int) (mToastCenterRawX + 0.5f);
                    mWindowManager.updateViewLayout(mToastView, mLayoutParams);
                    mToastView.setProgressText(String.valueOf(getProgress()));

                    invalidate();

                    if (mProgressListener != null) {
                        mProgressListener.onProgressChanged(getProgress(), getProgressFloat());
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isThumbOnDragging || isTouchToSeek) {
                    System.out.println("++++++++up 1");
                    mToastView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mToastView.animate()
                                    .alpha(isAlwaysShowBubble ? 1f : 0f)
                                    .setDuration(mAnimDuration)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            if (!isAlwaysShowBubble) {
                                                hideBubble();
                                            }

                                            isThumbOnDragging = false;
                                            invalidate();

                                            if (mProgressListener != null) {
                                                mProgressListener.onProgressChanged(getProgress(),
                                                        getProgressFloat());
                                            }
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {
                                            if (!isAlwaysShowBubble) {
                                                hideBubble();
                                            }

                                            isThumbOnDragging = false;
                                            invalidate();
                                        }
                                    })
                                    .start();

                        }
                    }, !isThumbOnDragging ? 300 : 0);
                }

                if (mProgressListener != null) {
                    mProgressListener.getProgressOnActionUp(getProgress(), getProgressFloat());
                }

                break;
        }

        return isThumbOnDragging || isTouchToSeek || super.onTouchEvent(event);
    }

    /**
     * Detect effective touch of thumb
     */
    private boolean isThumbTouched(MotionEvent event) {
        if (!isEnabled())
            return false;

        float x = mTrackLength / mDelta * (mProgress - mMin) + mLeft;
        float y = getMeasuredHeight() / 2f;
        return (event.getX() - x) * (event.getX() - x) + (event.getY() - y) * (event.getY() - y)
                <= (mLeft + dp2px(8)) * (mLeft + dp2px(8));
    }

    /**
     * Detect effective touch of track
     */
    private boolean isTrackTouched(MotionEvent event) {
        if (!isEnabled())
            return false;

        return event.getX() >= getPaddingLeft() && event.getX() <= getMeasuredWidth() - getPaddingRight()
                && event.getY() >= getPaddingTop() && event.getY() <= getPaddingTop() + mThumbRadius * 2;
    }

    /**
     * Showing the Bubble depends the way that the WindowManager adds a Toast type view to the Window.
     * <p>
     * 显示气泡
     * 原理是利用WindowManager动态添加一个与Toast相同类型的BubbleView，消失时再移除
     */
    private void showBubble() {
        if (mToastView.getParent() != null) {
            return;
        }

        if (mLayoutParams == null) {
            mLayoutParams = new WindowManager.LayoutParams();
            mLayoutParams.gravity = Gravity.START | Gravity.TOP;
            mLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mLayoutParams.format = PixelFormat.TRANSLUCENT;
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            // MIUI禁止了开发者使用TYPE_TOAST，Android 7.1.1 对TYPE_TOAST的使用更严格
            if (BubbleUtils.isMIUI() || Build.VERSION.SDK_INT >= 25) {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            } else {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }
        mLayoutParams.x = (int) (mToastCenterRawX + 0.5f);
        mLayoutParams.y = (int) (mToastCenterRawSolidY + 0.5f);

        mToastView.setAlpha(0);
        mToastView.setVisibility(VISIBLE);
        mToastView.animate().alpha(1f).setDuration(mAnimDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mWindowManager.addView(mToastView, mLayoutParams);
                    }
                }).start();
        mToastView.setProgressText(String.valueOf(getProgress()));
    }

    /**
     * The WindowManager removes the BubbleView from the Window.
     */
    private void hideBubble() {
        mToastView.setVisibility(GONE); // 防闪烁
        if (mToastView.getParent() != null) {
            mWindowManager.removeViewImmediate(mToastView);
        }
    }

    /**
     * When ToastSeekBar's parent view is scrollable, must listener to it's scrolling and call this
     * method to correct the offsets.
     */
    public void correctOffsetWhenContainerOnScrolling() {
        locatePositionOnScreen();

        if (mToastView.getParent() != null) {
            postInvalidate();
        }
    }

    private String getMinText() {
        return String.valueOf((int) mMin);
    }

    private String getMaxText() {
        return String.valueOf((int) mMax);
    }

    public float getMin() {
        return mMin;
    }

    public float getMax() {
        return mMax;
    }

    public void setProgress(float progress) {
        mProgress = progress;

        mToastCenterRawX = mToastCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;

        if (mProgressListener != null) {
            mProgressListener.onProgressChanged(getProgress(), getProgressFloat());
            mProgressListener.getProgressOnFinally(getProgress(), getProgressFloat());
        }
        if (isAlwaysShowBubble) {
            hideBubble();

            int[] location = new int[2];
            getLocationOnScreen(location);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    showBubble();
                    triggerBubbleShowing = true;
                }
            }, location[0] == 0 && location[1] == 0 ? 200 : 0);
        }

        postInvalidate();
    }

    public int getProgress() {
        return Math.round(mProgress);
    }

    public float getProgressFloat() {
        return formatFloat(mProgress);
    }

    public OnProgressChangedListener getOnProgressChangedListener() {
        return mProgressListener;
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        mProgressListener = onProgressChangedListener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("save_instance", super.onSaveInstanceState());
        bundle.putFloat("progress", mProgress);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getFloat("progress");
            super.onRestoreInstanceState(bundle.getParcelable("save_instance"));
            mToastView.setProgressText(String.valueOf(getProgress()));
            if (isAlwaysShowBubble) {
                setProgress(mProgress);
            }

            return;
        }

        super.onRestoreInstanceState(state);
    }

    private float formatFloat(float value) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * Listen to progress onChanged, onActionUp, onFinally
     */
    public interface OnProgressChangedListener {

        void onProgressChanged(int progress, float progressFloat);

        void getProgressOnActionUp(int progress, float progressFloat);

        void getProgressOnFinally(int progress, float progressFloat);
    }

    /**
     * Listener adapter
     * <br/>
     * usage like {@link AnimatorListenerAdapter}
     */
    public static abstract class OnProgressChangedListenerAdapter implements OnProgressChangedListener {

        @Override
        public void onProgressChanged(int progress, float progressFloat) {
        }

        @Override
        public void getProgressOnActionUp(int progress, float progressFloat) {
        }

        @Override
        public void getProgressOnFinally(int progress, float progressFloat) {
        }
    }

    /*******************************************************************************************
     **************************************  custom toast view  ********************************
     *******************************************************************************************/
    private class ToastView extends View {

        private Paint mToastPaint;
        private Rect mRect;
        private String mProgressText = "";

        ToastView(Context context) {
            this(context, null);
        }

        ToastView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        ToastView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            mToastPaint = new Paint();
            mToastPaint.setAntiAlias(true);
            mToastPaint.setTextAlign(Paint.Align.CENTER);

            mRect = new Rect();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            setMeasuredDimension(2 * mToastRadius, 2 * mToastRadius);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            mToastPaint.setTextSize(mToastTextSize);
            mToastPaint.setColor(mToastTextColor);
            String text = mProgressText + "%";
            mToastPaint.getTextBounds(text, 0, text.length(), mRect);
            Paint.FontMetrics fm = mToastPaint.getFontMetrics();
            float baseline = mToastRadius + (fm.descent - fm.ascent) / 2f - fm.descent;
            canvas.drawText(text, getMeasuredWidth() / 2f, baseline, mToastPaint);
        }

        void setProgressText(String progressText) {
            if (progressText != null && !mProgressText.equals(progressText)) {
                mProgressText = progressText;
                invalidate();
            }
        }
    }

}