package com.huyn.demogroup.paint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huyn.demogroup.R;
import com.huyn.demogroup.anim.roundedimageview.RoundedImageView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by huyaonan on 16/11/10.
 */
public class DetailLayout extends FrameLayout {

    private Context mContext;

    private RoundedImageView mImageView;

    private View mDetailLayout;
    private TextView mDetailTitle;
    private TextView mDetailDes;
    private TextView mDetailPrice;
    private TextView mDetailBuy;

    private int SMALL_SIZE_WIDTH = 200;
    private int SMALL_SIZE_HEIGHT = 200;
    private int BIG_SIZE_WIDTH = 0;
    private int BIG_SIZE_HEIGHT = 0;
    private int TARGET_X = 0;
    private int TARGET_Y = 0;

    private IAnimEndListener mAnimEndListener;
    private boolean mIsRound = true;

    private int startX=0;
    private int startY=0;

    private BorderedStripeView mStripeBg;
    private int stripeX=0;
    private int stripeY=0;
    private int STRIPE_HEIGHT=0;
    private int STRIPE_WIDTH_MIN=0;
    private int STRIPE_WIDTH_MAX=0;
    private AtomicBoolean mBgMeasured = new AtomicBoolean(false);

    public DetailLayout(Context context) {
        this(context, null);
    }

    public DetailLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;

        mStripeBg = new BorderedStripeView(context);
        addView(mStripeBg);
        mStripeBg.setVisibility(GONE);
        mStripeBg.setAlpha(0f);

        mDetailLayout = LayoutInflater.from(context).inflate(R.layout.aube_intera_layout_goods_detail, null, false);
        addView(mDetailLayout);
        mDetailTitle = (TextView) findViewById(R.id.card_goods_title);
        mDetailDes = (TextView) findViewById(R.id.card_goods_content);
        mDetailPrice = (TextView) findViewById(R.id.card_goods_price);
        mDetailBuy = (TextView) findViewById(R.id.card_goods_favor);
        mDetailLayout.setAlpha(0f);

        mImageView = new RoundedImageView(mContext);
        addView(mImageView, new LayoutParams(SMALL_SIZE_WIDTH, SMALL_SIZE_HEIGHT));
        mImageView.setAlpha(0f);
        mImageView.setCornerRadius(SMALL_SIZE_WIDTH/2);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageResource(R.drawable.cover);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getWidth();
        int height= getHeight();

        //大概比例为实际屏幕的3/4
        BIG_SIZE_HEIGHT = height*3/4;
        BIG_SIZE_WIDTH = height*9/16;

        TARGET_X = width/8;
        TARGET_Y = height/8;

        STRIPE_HEIGHT = BIG_SIZE_HEIGHT + 24;
        STRIPE_WIDTH_MIN = BIG_SIZE_WIDTH + 24;
        STRIPE_WIDTH_MAX = width*3/4 + 24;
        stripeX = TARGET_X-12;
        stripeY = TARGET_Y-12;

        mDetailLayout.measure(MeasureSpec.makeMeasureSpec(width*3/4-BIG_SIZE_WIDTH, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(BIG_SIZE_HEIGHT, MeasureSpec.EXACTLY));

        if(!mBgMeasured.get() && STRIPE_HEIGHT > 0) {
            mStripeBg.measure(MeasureSpec.makeMeasureSpec(STRIPE_WIDTH_MIN, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(STRIPE_HEIGHT, MeasureSpec.EXACTLY));
            mBgMeasured.set(true);
        }
    }

    /**
     *  设置转换动画前每个view的位置, layout
     * @param size
     * @param x
     * @param y
     * @param listener
     */
    public void setupPreAnimLayout(int size, int x, int y, final IAnimEndListener listener) {
        LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
        params.width = size;
        params.height = size;
        mImageView.setLayoutParams(params);
        SMALL_SIZE_WIDTH = size;
        SMALL_SIZE_HEIGHT = size;
        mIsRound = true;

        mAnimEndListener = listener;

        startX = x;
        startY = y;
        mImageView.setTranslationX(x);
        mImageView.setTranslationY(y);
        mImageView.setAlpha(1f);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        setVisibility(View.INVISIBLE);
    }

    public void startAnim(int[] values, final IAnimEndListener listener) {
        LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
        params.width = values[2];
        params.height = values[3];
        mImageView.setLayoutParams(params);
        SMALL_SIZE_WIDTH = values[2];
        SMALL_SIZE_HEIGHT = values[3];
        mIsRound = false;

        mAnimEndListener = listener;

        startX = values[0];
        startY = values[1];
        mImageView.setTranslationX(startX);
        mImageView.setTranslationY(startY);
        mImageView.setAlpha(1f);
        mImageView.setCornerRadius(0);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        setVisibility(View.VISIBLE);

        startAnim();
    }

    public void startAnim() {
        final float startX = mImageView.getTranslationX();
        final float startY = mImageView.getTranslationY();
        final int startWidth = SMALL_SIZE_WIDTH;
        final int startHeight = SMALL_SIZE_HEIGHT;
        final int startRadius = startWidth/2;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float targetX = startX + (TARGET_X - startX) * value;
                float targetY = startY + (TARGET_Y - startY) * value;
                int targetWidth = (int) (startWidth + (BIG_SIZE_WIDTH -startWidth) * value);
                int targetHeight = (int) (startHeight + (BIG_SIZE_HEIGHT -startHeight) * value);

                LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
                params.width = targetWidth;
                params.height = targetHeight;
                mImageView.setTranslationX(targetX);
                mImageView.setTranslationY(targetY);
                mImageView.setLayoutParams(params);

                mImageView.setRotationY(360*value);

                if(mIsRound) {
                    float radius = startRadius*(1-value);
                    mImageView.setCornerRadius(radius);
                }

//                mStripeBg.setAlpha(value);
//                LayoutParams paramsBg = (LayoutParams) mStripeBg.getLayoutParams();
//                paramsBg.width = (int) (STRIPE_WIDTH_MIN + (STRIPE_WIDTH_MAX-STRIPE_WIDTH_MIN)*value);
//                paramsBg.height = STRIPE_HEIGHT;
//                mStripeBg.setLayoutParams(paramsBg);

                mStripeBg.setTranslationX(stripeX);
                mStripeBg.setTranslationY(stripeY);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showDes();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(View.VISIBLE);

                mStripeBg.setVisibility(VISIBLE);
            }
        });
        animator.start();
    }

    private void showDes() {
        mDetailLayout.setTranslationY(TARGET_Y);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float x = TARGET_X + BIG_SIZE_WIDTH*value;

                mDetailLayout.setAlpha(value);
                mDetailLayout.setTranslationX(x);

                mStripeBg.setAlpha(value);
                int width = (int) (STRIPE_WIDTH_MIN + (STRIPE_WIDTH_MAX-STRIPE_WIDTH_MIN)*value);
                LayoutParams paramsBg = (LayoutParams) mStripeBg.getLayoutParams();
                paramsBg.width = width;
                paramsBg.height = STRIPE_HEIGHT;
                mStripeBg.setLayoutParams(paramsBg);
            }
        });
        animator.start();
    }

    public void close() {
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float x = TARGET_X + BIG_SIZE_WIDTH*value;

                mDetailLayout.setAlpha(value);
                mDetailLayout.setTranslationX(x);

                mStripeBg.setAlpha(value);
                int width = (int) (STRIPE_WIDTH_MIN + (STRIPE_WIDTH_MAX-STRIPE_WIDTH_MIN)*value);
                LayoutParams paramsBg = (LayoutParams) mStripeBg.getLayoutParams();
                paramsBg.width = width;
                paramsBg.height = STRIPE_HEIGHT;
                mStripeBg.setLayoutParams(paramsBg);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                reverse(SMALL_SIZE_WIDTH, SMALL_SIZE_WIDTH, startX, startY, mAnimEndListener);
            }
        });
        animator.start();
    }

    public void reverse(final int width, final int height, final int x, final int y, final IAnimEndListener listener) {
        final float startX = mImageView.getTranslationX();
        final float startY = mImageView.getTranslationY();
        final int startWidth = mImageView.getWidth();
        final int startHeight = mImageView.getHeight();
        final int targetRadius = width/2;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                float targetX = startX + (x - startX) * value;
                float targetY = startY + (y - startY) * value;
                int targetWidth = (int) (startWidth + (width -startWidth) * value);
                int targetHeight = (int) (startHeight + (height -startHeight) * value);

                LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
                params.width = targetWidth;
                params.height = targetHeight;
                mImageView.setTranslationX(targetX);
                mImageView.setTranslationY(targetY);
                mImageView.setLayoutParams(params);

                mImageView.setRotationY(-360*(1-value));

                if(mIsRound) {
                    float radius = targetRadius * value;
                    mImageView.setCornerRadius(radius);
                }

//                mStripeBg.setAlpha(1-value);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);

                mStripeBg.setVisibility(GONE);
                mStripeBg.setAlpha(0f);

                if(listener != null)
                    listener.onAnimEnd();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if(listener != null)
                    listener.onStartFinishAnim();
            }
        });

        animator.start();
    }

    public interface IAnimEndListener {
        public void onAnimEnd();
        public void onStartFinishAnim();
    }

}