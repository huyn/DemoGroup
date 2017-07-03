package com.huyn.demogroup.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.huyn.demogroup.R;
import com.huyn.demogroup.crop.motion.MotionGestureDetector;
import com.huyn.demogroup.crop.motion.OnMotionGestureListener;
import com.huyn.demogroup.crop.motion.OnMotionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 * <p/>
 * This view is used for drawing the overlay on top of the image. It may have frame, crop guidelines and dimmed area.
 * This must have LAYER_TYPE_SOFTWARE to draw itself properly.
 */
public class CropRectView extends View implements OnMotionGestureListener {

    public static final boolean DEFAULT_SHOW_CROP_FRAME = true;
    public static final boolean DEFAULT_SHOW_CROP_GRID = true;
    public static final int DEFAULT_CROP_GRID_ROW_COUNT = 0;
    public static final int DEFAULT_CROP_GRID_COLUMN_COUNT = 0;

    private final RectF mCropViewRect = new RectF();
    private final RectF mTempRect = new RectF();

    private static final int EDGE_NONE = -1;
    private static final int EDGE_LEFT = 0;
    private static final int EDGE_RIGHT = 1;
    private static final int EDGE_BOTH = 2;
    private static int SINGLE_TOUCH = 1;

    private int mScrollEdge = EDGE_BOTH;

    protected int mThisWidth, mThisHeight;
    protected float[] mCropGridCorners;
    protected float[] mCropGridCenter;

    private int mCropGridRowCount, mCropGridColumnCount;
    private float[] mGridPoints = null;
    private boolean mShowCropFrame, mShowCropGrid;
    private Path mCircularPath = new Path();
    private Paint mCropGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropFrameCornersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropSquarePointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropFrameDottedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mPreviousTouchX = -1, mPreviousTouchY = -1;
    private int mCurrentTouchCornerIndex = -1;
    private int mTouchPointThreshold;
    private int mCropRectMinSize;
    private int mCropRectCornerTouchAreaLineLength;
    private int mCropRectCornerPadding;
    private int mCropRectPointSize;
    private int mCropFrameStrokeSize;
    private Bitmap mOk;
    private int mBitmapW, mBitmapH;
    private float mOkX, mOkY;
    private long mDownEventTimeMills;
    private List<RectF> mRecommendRect = new ArrayList<>();

    private OverlayViewChangeListener mCallback;

    private boolean mDraggingRect = false;

    private MotionGestureDetector mScaleDragDetector;
    private boolean mAllowParentInterceptOnEdge = true;
    private boolean mBlockParentIntercept = false;

    private OnMotionListener mScaleListener;

    private boolean mShouldSetupCropBounds;

    {
        mTouchPointThreshold = getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_rect_corner_touch_threshold);
        mCropRectMinSize = getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_rect_min_size);
        mCropRectCornerTouchAreaLineLength = getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_rect_corner_touch_area_line_length);
        mCropRectCornerPadding = getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_rect_corner_padding);
        mCropRectPointSize = getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_point_size);
        mCropFrameStrokeSize = getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width);
        mOk = BitmapFactory.decodeResource(getResources(), R.drawable.icon_ok);
        mBitmapW = mOk.getWidth();
        mBitmapH = mOk.getHeight();
        mRecommendRect.add(new RectF(100, 100, 400, 400));
    }

    public void setOnScaleListener(OnMotionListener listener) {
        mScaleListener = listener;
    }

    public CropRectView(Context context) {
        this(context, null);
    }

    public CropRectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropRectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processStyledAttributes();
        init();
        // Create Gesture Detectors...
        mScaleDragDetector = new MotionGestureDetector(context, this);
    }

    public OverlayViewChangeListener getOverlayViewChangeListener() {
        return mCallback;
    }

    public void setOverlayViewChangeListener(OverlayViewChangeListener callback) {
        mCallback = callback;
    }

    @NonNull
    public RectF getCropViewRect() {
        return mCropViewRect;
    }

    /**
     * This method sets aspect ratio for crop bounds.
     */
    private void showRect(float x, float y) {
        if (mThisWidth > 0) {
            setupCropBounds(x, y);
            postInvalidate();
        } else {
            mShouldSetupCropBounds = true;
        }
    }

    private void showRect(RectF rectF) {
        if (mThisWidth > 0) {
            setupCropBounds(rectF);
            postInvalidate();
        } else {
            mShouldSetupCropBounds = true;
        }
    }

    private RectF getCurrentCropRect(float x, float y) {
        if(mRecommendRect.size() == 0)
            return null;
        for(RectF rect : mRecommendRect) {
            if(rect.contains(x, y)) {
                mRecommendRect.remove(rect);
                return rect;
            }
        }
        return null;
    }

    public void clear() {
        mCropViewRect.setEmpty();
        postInvalidate();
    }

    public void recycle() {
        mRecommendRect.clear();
    }

    public void addRecommendRect(RectF rectF) {
        mRecommendRect.add(rectF);
        postInvalidate();
    }

    /**
     * This method setups crop bounds rectangles for given aspect ratio and view size.
     * {@link #mCropViewRect} is used to draw crop bounds - uses padding.
     */
    public void setupCropBounds(float x, float y) {
        if(x == -1 || y == -1) {
            mCropViewRect.set(getPaddingLeft() + (mThisWidth / 4f),
                    getPaddingTop() + mThisHeight / 4f,
                    getPaddingLeft() + mThisWidth * 3f / 4,
                    getPaddingTop() + mThisHeight * 3f / 4);
        } else {
            float left = x - mCropRectMinSize/2;
            float top = y - mCropRectMinSize/2;
            float right = x + mCropRectMinSize/2;
            float bottom = y + mCropRectMinSize/2;
            if(left < 0) {
                left = 0;
                right = mCropRectMinSize;
            } else if(right > mThisWidth) {
                right = mThisWidth;
                left = mThisWidth - mCropRectMinSize;
            }

            if(top < 0) {
                top = 0;
                bottom = mCropRectMinSize;
            } else if(bottom > mThisHeight) {
                bottom = mThisHeight;
                top = mThisHeight - mCropRectMinSize;
            }
            mCropViewRect.set(left, top, right, bottom);
        }

        if (mCallback != null) {
            mCallback.onCropRectUpdated(mCropViewRect);
        }

        updateGridPoints();
    }

    public void setupCropBounds(RectF rectF) {
        mCropViewRect.set(rectF);

        if (mCallback != null) {
            mCallback.onCropRectUpdated(mCropViewRect);
        }

        updateGridPoints();
    }

    private void updateGridPoints() {
        mCropGridCorners = RectUtils.getCornersFromRect(mCropViewRect);
        mCropGridCenter = RectUtils.getCenterFromRect(mCropViewRect);

        mGridPoints = null;
        mCircularPath.reset();
        mCircularPath.addCircle(mCropViewRect.centerX(), mCropViewRect.centerY(),
                Math.min(mCropViewRect.width(), mCropViewRect.height()) / 2.f, Path.Direction.CW);
    }

    protected void init() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 &&
//                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
//        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            mThisWidth = right - left;
            mThisHeight = bottom - top;

            if (mShouldSetupCropBounds) {
                mShouldSetupCropBounds = false;
                showRect(-1, -1);
            }
        }
    }

    /**
     * Along with image there are dimmed layer, crop bounds and crop guidelines that must be drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mMultiTouch)
            return;
        drawCropGrid(canvas);
        drawOk(canvas);
        drawRecommend(canvas);
    }

    /**
     * 绘制推荐区域
     * @param canvas
     */
    private void drawRecommend(Canvas canvas) {
        if(mCropViewRect.isEmpty()) {
            for(RectF rectF : mRecommendRect)
                canvas.drawRect(rectF, mCropFrameDottedPaint);
        }
    }

    /**
     * 绘制ok键
     * @param canvas
     */
    private void drawOk(Canvas canvas) {
        if(mCropViewRect.isEmpty())
            return;
        int minGap = 10;
        mOkX = mCropViewRect.right + minGap;
        mOkY = mCropViewRect.bottom + minGap;
        if(mOkX + mBitmapW > mThisWidth) {
            if(mOkY + mBitmapH > mThisHeight) {
                mOkX = mCropViewRect.left - minGap - mBitmapW;
                mOkY = mThisHeight - mBitmapH - minGap;
            } else {
                mOkX = mThisWidth - mBitmapW - minGap;
            }
        } else if(mOkY + mBitmapH > mThisHeight) {
            mOkY = mThisHeight - mBitmapH - minGap;
        }
        canvas.drawBitmap(mOk, mOkX, mOkY, null);
    }

    private boolean mMultiTouch = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean isCropViewRectBlank = mCropViewRect.isEmpty();
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            mMultiTouch = false;
            mDraggingRect = false;
            if(isCropViewRectBlank) {
                RectF rectF = getCurrentCropRect(x, y);
                if(rectF != null) {
                    showRect(rectF);
                } else {
                    showRect(x, y);
                }

                mPreviousTouchX = x;
                mPreviousTouchY = y;
                mCurrentTouchCornerIndex = 8;
                //return true;
            } else {
                mCurrentTouchCornerIndex = getCurrentTouchIndex(x, y);
                boolean shouldHandle = mCurrentTouchCornerIndex != -1;
                if (!shouldHandle) {
                    mPreviousTouchX = -1;
                    mPreviousTouchY = -1;
                    clear();
                } else if (mPreviousTouchX < 0) {
                    mPreviousTouchX = x;
                    mPreviousTouchY = y;
                }
            }
            //return shouldHandle;
            //return true;
        }

        if(action == MotionEvent.ACTION_POINTER_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(false);
            mMultiTouch = true;

            mPreviousTouchX = -1;
            mPreviousTouchY = -1;
            clear();
        }


        if(!mMultiTouch) {
            System.out.println("++++++action..." + action);
            if (!isCropViewRectBlank) {

                if (action == MotionEvent.ACTION_MOVE) {
                    if (event.getPointerCount() == 1) {
                        if (mCurrentTouchCornerIndex != -1) {
                            x = Math.min(Math.max(x, getPaddingLeft()), getWidth() - getPaddingRight());
                            y = Math.min(Math.max(y, getPaddingTop()), getHeight() - getPaddingBottom());

                            mDraggingRect = true;
                            updateCropViewRect(x, y);

                            mPreviousTouchX = x;
                            mPreviousTouchY = y;
                            //return true;
                        }
                    }
                }

                if (action == MotionEvent.ACTION_UP) {
                    if (mDraggingRect) {
                        mDraggingRect = false;
                        postInvalidate();
                    }
                    mPreviousTouchX = -1;
                    mPreviousTouchY = -1;
                    mCurrentTouchCornerIndex = -1;

                    if (mCallback != null) {
                        mCallback.onCropRectUpdated(mCropViewRect);
                    }
                }
            }
        }

        // Try the Scale/Drag detector
        boolean handled = false;
        if (mScaleDragDetector != null) {
            boolean wasScaling = mScaleDragDetector.isScaling();
            boolean wasDragging = mScaleDragDetector.isDragging();

            handled = mScaleDragDetector.onTouchEvent(event);

            boolean didntScale = !wasScaling && !mScaleDragDetector.isScaling();
            boolean didntDrag = !wasDragging && !mScaleDragDetector.isDragging();

            mBlockParentIntercept = didntScale && didntDrag;
        }

        return handled;
    }

    /**
     * * The order of the corners is:
     * 0---1--->2
     * ^        |
     * |        3
     * 7   8    |
     * |        v
     * 6<--5----4
     */
    private void updateCropViewRect(float touchX, float touchY) {
        mTempRect.set(mCropViewRect);
        float offsetX=0;
        float offsetY=0;
        float left, top, right, bottom;
        switch (mCurrentTouchCornerIndex) {
            // resize rectangle
            case 0:
                offsetX=touchX-mCropViewRect.left;
                offsetY=touchY-mCropViewRect.top;
                right = mCropViewRect.right-offsetX;
                bottom = mCropViewRect.bottom-offsetY;
                if(right > mThisWidth) {
                    right = mThisWidth;
                    touchX = mCropViewRect.left + mCropViewRect.right - mThisWidth;
                }
                if(bottom > mThisHeight) {
                    bottom = mThisHeight;
                    touchY = mCropViewRect.top + mCropViewRect.bottom - mThisHeight;
                }
                mTempRect.set(touchX, touchY, right, bottom);
                break;
            case 1:
                mTempRect.set(mCropViewRect.left, touchY, mCropViewRect.right, mCropViewRect.bottom);
                break;
            case 2:
                offsetX=touchX-mCropViewRect.right;
                offsetY=touchY-mCropViewRect.top;
                left = mCropViewRect.left-offsetX;
                bottom = mCropViewRect.bottom-offsetY;
                if(left < 0) {
                    left = 0;
                    touchX = mCropViewRect.left + mCropViewRect.right;
                }
                if(bottom > mThisHeight) {
                    bottom = mThisHeight;
                    touchY = mCropViewRect.top + mCropViewRect.bottom - mThisHeight;
                }
                mTempRect.set(left, touchY, touchX, bottom);
                break;
            case 3:
                mTempRect.set(mCropViewRect.left, mCropViewRect.top, touchX, mCropViewRect.bottom);
                break;
            case 4:
                offsetX=touchX-mCropViewRect.right;
                offsetY=touchY-mCropViewRect.bottom;
                left = mCropViewRect.left-offsetX;
                top = mCropViewRect.top-offsetY;
                if(left < 0) {
                    left = 0;
                    touchX = mCropViewRect.left + mCropViewRect.right;
                }
                if(top < 0) {
                    top = 0;
                    touchY = mCropViewRect.top + mCropViewRect.bottom;
                }
                mTempRect.set(left, top, touchX, touchY);
                break;
            case 5:
                mTempRect.set(mCropViewRect.left, mCropViewRect.top, mCropViewRect.right, touchY);
                break;
            case 6:
                offsetX=touchX-mCropViewRect.left;
                offsetY=touchY-mCropViewRect.bottom;
                right = mCropViewRect.left-offsetX;
                top = mCropViewRect.top-offsetY;
                if(right > mThisWidth) {
                    right = mThisWidth;
                    touchX = mCropViewRect.left + mCropViewRect.right - mThisWidth;
                }
                if(top < 0) {
                    top = 0;
                    touchY = mCropViewRect.top + mCropViewRect.bottom;
                }
                mTempRect.set(touchX, top, right, touchY);
                break;
            case 7:
                mTempRect.set(touchX, mCropViewRect.top, mCropViewRect.right, mCropViewRect.bottom);
                break;
            // move rectangle
            case 8:
                mTempRect.offset(touchX - mPreviousTouchX, touchY - mPreviousTouchY);
                if (mTempRect.left > getLeft() && mTempRect.top > getTop()
                        && mTempRect.right < getRight() && mTempRect.bottom < getBottom()) {
                    mCropViewRect.set(mTempRect);
                    updateGridPoints();
                    postInvalidate();
                }
                return;
        }

        boolean changeHeight = mTempRect.height() >= mCropRectMinSize;
        boolean changeWidth = mTempRect.width() >= mCropRectMinSize;
        mCropViewRect.set(
                changeWidth ? mTempRect.left : mCropViewRect.left,
                changeHeight ? mTempRect.top : mCropViewRect.top,
                changeWidth ? mTempRect.right : mCropViewRect.right,
                changeHeight ? mTempRect.bottom : mCropViewRect.bottom);

        if (changeHeight || changeWidth) {
            updateGridPoints();
            postInvalidate();
        }
    }

    /**
     * * The order of the corners in the float array is:
     * 0---1--->2
     * ^        |
     * |        3
     * 7   8    |
     * |        v
     * 6<--5----4
     *
     * @return - index of corner that is being dragged
     */
    private int getCurrentTouchIndex(float touchX, float touchY) {
        int closestPointIndex = -1;
        double closestPointDistance = mTouchPointThreshold;
        for (int i = 0; i < 16; i += 2) {
            double distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCorners[i], 2)
                    + Math.pow(touchY - mCropGridCorners[i + 1], 2));
            if (distanceToCorner < closestPointDistance) {
                closestPointDistance = distanceToCorner;
                closestPointIndex = i / 2;
            }
        }

        if (closestPointIndex < 0 && mCropViewRect.contains(touchX, touchY)) {
            return 8;
        }

        return closestPointIndex;
    }

    /**
     * This method draws crop bounds (empty rectangle)
     * and crop guidelines (vertical and horizontal lines inside the crop bounds) if needed.
     *
     * @param canvas - valid canvas object
     */
    protected void drawCropGrid(@NonNull Canvas canvas) {
        if (mShowCropGrid) {
            if (mGridPoints == null && !mCropViewRect.isEmpty()) {

                mGridPoints = new float[(mCropGridRowCount) * 4 + (mCropGridColumnCount) * 4];

                int index = 0;
                for (int i = 0; i < mCropGridRowCount; i++) {
                    mGridPoints[index++] = mCropViewRect.left;
                    mGridPoints[index++] = (mCropViewRect.height() * (((float) i + 1.0f) / (float) (mCropGridRowCount + 1))) + mCropViewRect.top;
                    mGridPoints[index++] = mCropViewRect.right;
                    mGridPoints[index++] = (mCropViewRect.height() * (((float) i + 1.0f) / (float) (mCropGridRowCount + 1))) + mCropViewRect.top;
                }

                for (int i = 0; i < mCropGridColumnCount; i++) {
                    mGridPoints[index++] = (mCropViewRect.width() * (((float) i + 1.0f) / (float) (mCropGridColumnCount + 1))) + mCropViewRect.left;
                    mGridPoints[index++] = mCropViewRect.top;
                    mGridPoints[index++] = (mCropViewRect.width() * (((float) i + 1.0f) / (float) (mCropGridColumnCount + 1))) + mCropViewRect.left;
                    mGridPoints[index++] = mCropViewRect.bottom;
                }
            }

            if (mGridPoints != null) {
                canvas.drawLines(mGridPoints, mCropGridPaint);
            }
        }

        if (mShowCropFrame) {
            canvas.drawRect(mCropViewRect, mCropFramePaint);
        }

        canvas.save();

        /**
         * draw 8 square points
         */
        float offset = (mCropRectPointSize - mCropFrameStrokeSize*2)/2f;
        float left = mCropViewRect.left-offset;
        float top = mCropViewRect.top-offset;
        //topleft
        canvas.drawRect(left, top, left + mCropRectPointSize, top + mCropRectPointSize, mCropSquarePointPaint);
        //topcenter
        left = (mCropViewRect.right-mCropViewRect.left)/2f+mCropViewRect.left-offset;
        canvas.drawRect(left, top, left + mCropRectPointSize, top + mCropRectPointSize, mCropSquarePointPaint);
        //topright
        left = mCropViewRect.right-offset;
        canvas.drawRect(left, top, left + mCropRectPointSize, top + mCropRectPointSize, mCropSquarePointPaint);
        //leftcenter
        left = mCropViewRect.left-offset;
        top = mCropViewRect.top + (mCropViewRect.bottom-mCropViewRect.top)/2-offset;
        canvas.drawRect(left, top, left + mCropRectPointSize, top + mCropRectPointSize, mCropSquarePointPaint);
        //rightcenter
        left = mCropViewRect.right-offset;
        canvas.drawRect(left, top, left + mCropRectPointSize, top + mCropRectPointSize, mCropSquarePointPaint);
        //bottomleft
        left = mCropViewRect.left-offset;
        top = mCropViewRect.bottom-offset;
        canvas.drawRect(left, top, left + mCropRectPointSize, top + mCropRectPointSize, mCropSquarePointPaint);
        //bottomcenter
        left = (mCropViewRect.right-mCropViewRect.left)/2f+mCropViewRect.left-offset;
        canvas.drawRect(left, top, left + mCropRectPointSize, top + mCropRectPointSize, mCropSquarePointPaint);
        //bottomright
        left = mCropViewRect.right-offset;
        canvas.drawRect(left, top, left + mCropRectPointSize, top + mCropRectPointSize, mCropSquarePointPaint);

        mTempRect.set(mCropViewRect);
        mTempRect.inset(mCropRectCornerTouchAreaLineLength, -mCropRectCornerTouchAreaLineLength);
        canvas.clipRect(mTempRect, Region.Op.DIFFERENCE);

        mTempRect.set(mCropViewRect);
        mTempRect.inset(-mCropRectCornerTouchAreaLineLength, mCropRectCornerTouchAreaLineLength);
        canvas.clipRect(mTempRect, Region.Op.DIFFERENCE);

        //draw inner triangle
        RectF cornerRect = new RectF();
        cornerRect.left = mCropViewRect.left + mCropRectCornerPadding;
        cornerRect.top = mCropViewRect.top + mCropRectCornerPadding;
        cornerRect.right = mCropViewRect.right - mCropRectCornerPadding;
        cornerRect.bottom = mCropViewRect.bottom - mCropRectCornerPadding;
        canvas.drawRect(cornerRect, mCropFrameCornersPaint);

        canvas.restore();
    }

    /**
     * This method extracts all needed values from the styled attributes.
     * Those are used to configure the view.
     */
    protected void processStyledAttributes() {
        initCropFrameStyle();
        mShowCropFrame = DEFAULT_SHOW_CROP_FRAME;

        initCropGridStyle();
        mShowCropGrid = DEFAULT_SHOW_CROP_GRID;
    }

    /**
     * This method setups Paint object for the crop bounds.
     */
    private void initCropFrameStyle() {
        int radius = 4;
        int dy = 2;
        int dx = 0;
        int color = 0x44000000;

        int cropFrameColor =  getResources().getColor(R.color.ucrop_color_default_crop_frame);
        mCropFramePaint.setStrokeWidth(mCropFrameStrokeSize * 2);
        mCropFramePaint.setColor(cropFrameColor);
        mCropFramePaint.setStyle(Paint.Style.STROKE);
        mCropFramePaint.setShadowLayer(radius, dx, dy, color);

        mCropFrameCornersPaint.setStrokeWidth(mCropFrameStrokeSize);
        mCropFrameCornersPaint.setColor(cropFrameColor);
        mCropFrameCornersPaint.setStyle(Paint.Style.STROKE);
        mCropFrameCornersPaint.setShadowLayer(radius, dx, dy, color);

        mCropFrameDottedPaint.setStrokeWidth(mCropFrameStrokeSize);
        mCropFrameDottedPaint.setColor(cropFrameColor);
        mCropFrameDottedPaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[] {10, 10}, 0);
        mCropFrameDottedPaint.setPathEffect(effects);
        mCropFrameDottedPaint.setShadowLayer(radius, dx, dy, color);

        mCropSquarePointPaint.setColor(cropFrameColor);
        mCropSquarePointPaint.setShadowLayer(radius, dx, dy, color);
    }

    /**
     * This method setups Paint object for the crop guidelines.
     */
    private void initCropGridStyle() {
        int cropGridStrokeSize = getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width);
        int cropGridColor = getResources().getColor(R.color.ucrop_color_default_crop_grid);
        mCropGridPaint.setStrokeWidth(cropGridStrokeSize);
        mCropGridPaint.setColor(cropGridColor);

        mCropGridRowCount = DEFAULT_CROP_GRID_ROW_COUNT;
        mCropGridColumnCount = DEFAULT_CROP_GRID_COLUMN_COUNT;
    }

    @Override
    public void onDragStart() {
        if(mScaleListener != null)
            mScaleListener.onDragStart();
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (mScaleDragDetector.isScaling()) {
            return; // Do not drag if we are already scaling
        }

        //do drag
        if(mScaleListener != null)
            mScaleListener.onDrag(dx, dy);

        /*
         * Here we decide whether to let the parent to start taking
         * over the touch event.
         *
         * First we check whether this function is enabled. We never want the
         * parent to take over if we're scaling. We then check the edge we're
         * on, and the direction of the scroll (i.e. if we're pulling against
         * the edge, aka 'overscrolling', let the parent take over).
         */
        ViewParent parent = getParent();
        if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling() && !mBlockParentIntercept) {
            if (mScrollEdge == EDGE_BOTH
                    || (mScrollEdge == EDGE_LEFT && dx >= 1f)
                    || (mScrollEdge == EDGE_RIGHT && dx <= -1f)) {
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            }
        } else {
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    @Override
    public void onDragEnd() {
        if(mScaleListener != null)
            mScaleListener.onRelease();
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {

    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        if(mScaleListener != null)
            mScaleListener.onScale(scaleFactor, focusX, focusY);
    }
}
