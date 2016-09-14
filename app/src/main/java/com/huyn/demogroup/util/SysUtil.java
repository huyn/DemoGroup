/*
 * @SuperTV All Rights Reserved
 */

package com.huyn.demogroup.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * @author yanxin
 * @time 2014-1-9 AM 10:31
 */
public class SysUtil {

	/** 屏幕宽度 */
	private static int screenWidth;
	/** 屏幕高度 */
	private static int screenHeight;
	/** 状态栏高度 */
	private static int statusHeight=0;
	/** 状态栏高度 */
	private static int actionBarHeight;
	private static int sdk_version;
	/** 超大屏手机 */
	private static boolean isXLarge = false;
	/** 1080p */
	private static boolean isXXLarge = false;

	public static void init(Context context) {
		if (null == context || (screenWidth > 0 && screenHeight > 0)) {
			return;
		}

		DisplayMetrics metric = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(metric);

		screenWidth = metric.widthPixels; // 屏幕宽度（像素）
		screenHeight = metric.heightPixels; // 屏幕高度（像素）

		if (screenWidth > 700 && screenHeight > 900) {
			isXLarge = true;
		}

		if (screenWidth > 1000) {
			isXXLarge = true;
		}
	}

	@SuppressLint("NewApi")
	public static int getActionBarHeight(Context context) {
		if (actionBarHeight == 0 && null != context) {
			if (getSdkVersion(context) >= 11) {
				TypedArray actionbarSizeTypedArray = context
						.obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
				actionBarHeight = (int) actionbarSizeTypedArray.getDimension(0,
						0);
				actionbarSizeTypedArray.recycle();
			}
		}
		return actionBarHeight;
	}

	public static int getAbsoluteScreenWidth(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	public static int getScreenWidth(Context context) {
//		init(context);
//		return screenWidth;
		return getAbsoluteScreenWidth(context);
	}

	public static int getAbsoluteScreenHeight(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}

	public static int getScreenHeight(Context context) {
		init(context);
		return screenHeight;
	}
	public static int getStatusHeight(Context context) {
//		init(context);
//		return statusHeight;
		if(statusHeight == 0) {
			Class<?> c = null;
			Object obj = null;
			Field field = null;
			int x = 0;
			try {
			    c = Class.forName("com.android.internal.R$dimen");
			    obj = c.newInstance();
			    field = c.getField("status_bar_height");
			    x = Integer.parseInt(field.get(obj).toString());
			    statusHeight = context.getResources().getDimensionPixelSize(x);
			} catch(Exception e1) {
			    e1.printStackTrace();
			    Rect frame = new Rect();
			    ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

			    statusHeight = frame.top;
			}
		}
		return statusHeight;
	}
	public static int getWindowsHeight(Context context){
		return ((Activity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getBottom(); 
	}

	public static boolean isXLargeScreen(Context context) {
		init(context);
		return isXLarge;
	}

	public static boolean isXXLargeScreen(Context context) {
		init(context);
		return isXXLarge;
	}

	public static int getSdkVersion(Context context) {
		if (sdk_version <= 0) {
			sdk_version = Build.VERSION.SDK_INT;
		}
		return sdk_version;
	}
	
	public static boolean isScreenOn(Context context) {
    	PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);  
    	return pm.isScreenOn();
    }
	public static int getStatusBarHeight(Context context) {
		Resources res = context.getResources();
		if(Build.VERSION.SDK_INT < 19)
			return 0;
        int result = 0;
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
	}
	public static int getActivityMarginTop(Context context){   //基于4.4以下不支持overlay模式，4.4以上activity向上距离0，4.4以下为状态栏高度
		if (Build.VERSION.SDK_INT < 19) {
			return getStatusHeight(context);
		}
		return 0;
	}

	/**
	 * 根据宽高比及视图的高度改变视图的宽
	 * @param view 视图
	 * @param viewHeight 视图的高度
	 * @param srcWidth 原图的width
	 *  @param srcHeight 原图的height
	 */
	private static void changeWidthByHeight(View view, int viewHeight, int srcWidth, int srcHeight) {
		int width = viewHeight * srcWidth / srcHeight;
		ViewGroup.LayoutParams params = view.getLayoutParams();
		params.width = width;
		params.height = viewHeight;
		view.setLayoutParams(params);
	}


}
