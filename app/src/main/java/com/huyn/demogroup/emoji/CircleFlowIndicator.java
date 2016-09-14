package com.huyn.demogroup.emoji;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 浮动圆点视图
 * @author junjie.Li
 *
 */
public class CircleFlowIndicator extends View implements FlowIndicator{
	private static final int STYLE_STROKE = 0;
	private static final int STYLE_FILL = 1;
	private float radius = 4;
	private int currentIndex = 0;
	private float centeringOffset = 0;
	private final Paint mPaintInactive = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mPaintActive = new Paint(Paint.ANTI_ALIAS_FLAG);
	private ViewGroup viewGroup = null;
	private int count = 0;
	
	public CircleFlowIndicator(Context context) {
		this(context,null);
	}
	
	public CircleFlowIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);

		int activeType = STYLE_FILL;
		
		int activeDefaultColor = 0xFFFFFFFF;

		int inactiveType = STYLE_STROKE;

		int inactiveDefaultColor = 0x44FFFFFF;

		radius = 6;
		initColors(activeDefaultColor, inactiveDefaultColor, activeType, inactiveType);
		
	}
	
	private void initColors(int activeColor, int inactiveColor, int activeType,
			int inactiveType) {
		switch (inactiveType) {
		case STYLE_FILL:
			mPaintInactive.setStyle(Style.FILL);
			break;
		default:
			mPaintInactive.setStyle(Style.STROKE);
		}
		mPaintInactive.setColor(inactiveColor);

		switch (activeType) {
		case STYLE_STROKE:
			mPaintActive.setStyle(Style.STROKE);
			break;
		default:
			mPaintActive.setStyle(Style.FILL);
		}
		mPaintActive.setColor(activeColor);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(viewGroup != null){
			count = viewGroup.getChildCount();
		}
		
		float circleSeparation = 2*radius+radius;
		int leftPadding = getPaddingLeft();
		
		for (int iLoop = 0; iLoop < count; iLoop++) {
			canvas.drawCircle(leftPadding + radius
					+ (iLoop * circleSeparation) + centeringOffset,
					getPaddingTop() + radius, radius, mPaintInactive);
		}
		canvas.drawCircle(leftPadding + radius + (currentIndex * circleSeparation)+centeringOffset, getPaddingTop()
				+ radius, radius, mPaintActive);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}else {
			if (viewGroup != null) {
				count = viewGroup.getChildCount();
			}
			result = (int) (getPaddingLeft() + getPaddingRight()
					+ (count * 2 * radius) + (count - 1) * radius + 1);
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}
		else {
			result = (int) (2 * radius + getPaddingTop() + getPaddingBottom() + 1);
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	public void onSwitched(View view, int position) {
		currentIndex = position;
		invalidate();
	}

	@Override
	public void setViewFlow(ViewGroup viewGroup, int count) {
		if(!(viewGroup instanceof ViewPager)){
			this.viewGroup = viewGroup; 
		}
		this.count = count;
		invalidate();
	}

	public int getCount() {
		return count;
	}
	
}