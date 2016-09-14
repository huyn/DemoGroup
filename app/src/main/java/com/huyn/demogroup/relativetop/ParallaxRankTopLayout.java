package com.huyn.demogroup.relativetop;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huyn.demogroup.R;
import com.huyn.demogroup.util.SysUtil;

/**
 * Created by huyaonan on 16/8/12.
 */
public class ParallaxRankTopLayout extends FrameLayout {

    private ImageView mBack;
    private TextView mTitle;
    private ImageView mShare;

    private View mRuleLayout;
    private View mIndicatorLayout;

    private ImageView mRuleImg;
    private PagerSlidingTabStrip mIndicator;
    private TextView mDes;

    private int space = 40;
    private int mTitleOffsetX = 0;
    private int mTitleOffsetY = 0;
    private int MAX_OFFSET_X = 0;
    private int mTopbarHeight = 0;
    private int statusBarHeight = 0;
    private int mIndicatorHeight=0;
    private int mTargetHeight = 0;

    private Context context;

    public ParallaxRankTopLayout(Context context) {
        this(context, null);
    }

    public ParallaxRankTopLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxRankTopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initViews();
        init();
    }

    public void initViews() {
        space = getResources().getDimensionPixelOffset(R.dimen.parallax_top_margin);
        mTopbarHeight = getResources().getDimensionPixelOffset(R.dimen.actionbar_height);
        mTitleOffsetX = getResources().getDimensionPixelOffset(R.dimen.parallax_top_margin);

        statusBarHeight = SysUtil.getStatusHeight(context);

        setupData();
    }

    private void init() {
        space = getResources().getDimensionPixelOffset(R.dimen.parallax_top_margin);
        mTopbarHeight = getResources().getDimensionPixelOffset(R.dimen.actionbar_height);
        mTitleOffsetX = getResources().getDimensionPixelOffset(R.dimen.parallax_top_margin);

        statusBarHeight = SysUtil.getStatusHeight(context);

        mTargetHeight = getResources().getDimensionPixelOffset(R.dimen.category_list_head_height);

        setupData();
    }

    private void setupData() {
        mBack = new ImageView(context);
        mBack.setScaleType(ImageView.ScaleType.CENTER);
        mBack.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_new_back));
        addView(mBack);

        mShare = new ImageView(context);
        mShare.setScaleType(ImageView.ScaleType.CENTER);
        mShare.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_share));
        addView(mShare);

        mTitle = new TextView(context);
        mTitle.setTextSize(20);
        mTitle.setTextColor(Color.WHITE);
        mTitle.setSingleLine(true);
        mTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        addView(mTitle);

        mRuleLayout = inflate(LayoutInflater.from(context), R.layout.top_rule_layout);
        mRuleImg = (ImageView) mRuleLayout.findViewById(R.id.rank_rule_img);
        mRuleImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_info));
        addView(mRuleLayout);

        mIndicatorLayout = inflate(LayoutInflater.from(context), R.layout.top_indicator_layout);
        mIndicator = (PagerSlidingTabStrip) mIndicatorLayout.findViewById(R.id.rank_indicator);
        mDes = (TextView) mIndicatorLayout.findViewById(R.id.rank_des);
        addView(mIndicatorLayout);
    }

    public View inflate(LayoutInflater inflater, int id) {
        XmlResourceParser parser = this.getResources().getLayout(id);

        View view;
        try {
            view = inflater.inflate(parser, null, false);
        } finally {
            parser.close();
        }

        return view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(mBack == null)
            return;

        mBack.measure(0, 0);
        mShare.measure(0, 0);
        mRuleLayout.measure(0, View.MeasureSpec.makeMeasureSpec(mTopbarHeight, View.MeasureSpec.EXACTLY));
        int imgWidth = mBack.getMeasuredWidth();
        int ruleWidth = mRuleLayout.getMeasuredWidth();
        int width = getMeasuredWidth();
        MAX_OFFSET_X = imgWidth + 2*space;
        int titleWidth = width - MAX_OFFSET_X - space*2 - ruleWidth;
        mTitle.measure(View.MeasureSpec.makeMeasureSpec(titleWidth, View.MeasureSpec.EXACTLY), 0);

        mIndicatorLayout.measure(widthMeasureSpec, 0);

        int measureWidth = measureWidth(widthMeasureSpec);
        mIndicatorHeight = mIndicatorLayout.getMeasuredHeight();

        setMeasuredDimension(measureWidth, mTargetHeight + mIndicatorLayout.getMeasuredHeight());
    }

    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = View.MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = View.MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

        switch (widthMode) {
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.EXACTLY:
                result = widthSize;
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(mBack == null)
            return;

        int backY = statusBarHeight + (mTopbarHeight-mBack.getMeasuredHeight())/2;
        mBack.layout(l+space, t+backY, l+space+mBack.getMeasuredWidth(), t+mBack.getMeasuredHeight()+backY);
        backY = statusBarHeight + (mTopbarHeight-mShare.getMeasuredHeight())/2;
        mShare.layout(r-space-mShare.getMeasuredWidth(), t+backY, r-space, t+mShare.getMeasuredHeight()+backY);

        mTitleOffsetY = getHeight()-mTopbarHeight+(mTopbarHeight-mTitle.getHeight())/2 - mIndicatorHeight;
        mTitle.layout(l+mTitleOffsetX, t+mTitleOffsetY, l+mTitleOffsetX+mTitle.getMeasuredWidth(), t+mTitleOffsetY+mTitle.getMeasuredHeight());

        int offsetFavorY = getHeight()-mTopbarHeight+(mTopbarHeight-mRuleLayout.getHeight())/2 - mIndicatorHeight;
        mRuleLayout.layout(r - mRuleLayout.getMeasuredWidth() - space, t + offsetFavorY, r - space, t + offsetFavorY + mRuleLayout.getMeasuredHeight());

        mIndicatorLayout.layout(l, getHeight() - mIndicatorHeight, r, getHeight());
    }

    public void setOnParallaxTopEventListener(final OnParallaxTopEventListener listener) {
        if(listener == null)
            return;
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBackPressed();
            }
        });
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShare();
            }
        });
    }

    public void endLoading() {
    }

    public void setRuleClickListener(final View.OnClickListener listener) {
        if(listener == null)
            return;
        mRuleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });
    }

    public void updateDes(String str) {
        mDes.setText(str);
    }

    public void updateTitleOffset(int offset) {
        updateTitleTranslation(offset);
    }

    public void updateTitleTranslation(int offset) {
        if(offset >= getHeight()-2*mTopbarHeight -statusBarHeight - mIndicatorHeight) {
            int offsetX = (int) ((offset-getHeight()+2*mTopbarHeight+statusBarHeight+mIndicatorHeight) * 1f * (MAX_OFFSET_X - mTitleOffsetX) / (mTopbarHeight));
            mTitle.setTranslationX(offsetX);
        } else {
            mTitle.setTranslationX(0);
        }
        mTitle.setTranslationY(-offset);
        mRuleLayout.setTranslationY(-offset);
        mIndicatorLayout.setTranslationY(-offset);
    }

    public void updateTitleAlpha(float alpha) {
        mRuleLayout.setAlpha(alpha);
    }

    public int getIndicatorHeight() {
        return mIndicatorHeight;
    }

    public void setViewPager(ViewPager pager, PagerSlidingTabStrip.IconAndTextTabProvider iProvider) {
        mIndicator.setViewPager(pager, iProvider);
    }

    public void setIndicatorColor(int color) {
        mIndicator.setIndicatorColor(color);
    }

    public void setText(String text) {
        mTitle.setText(text);
    }

}
