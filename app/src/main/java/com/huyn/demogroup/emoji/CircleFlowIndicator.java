package com.huyn.demogroup.emoji;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

/**
 * 浮动圆点视图
 * @author junjie.Li
 *
 */
public class CircleFlowIndicator extends View implements FlowIndicator{
	private static final int STYLE_STROKE = 0;
	private static final int STYLE_FILL = 1;
	private float radius = 10;
	private int currentIndex = 0;
	private float centeringOffset = 0;
	private final Paint mPaintInactive = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mPaintActive = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int count = 0;
	
	public CircleFlowIndicator(Context context) {
		this(context,null);
	}
	
	public CircleFlowIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);

		int activeType = STYLE_FILL;
		
		int activeDefaultColor = 0xFFFF0000;

		int inactiveType = STYLE_STROKE;

		int inactiveDefaultColor = 0xFFFF0000;

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
	public void setViewFlow(int count) {
		this.count = count;
		invalidate();
	}

	public int getCount() {
		return count;
	}
	
}