package com.huyn.demogroup.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefUtil {
	
	private static SharedPrefUtil instance;
	private static SharedPreferences sharedPrefs;
	
	public static final String SHOW_HELP = "SHOW_HELP";

	public static final String SHOW_GUIDE_COVER = "SHOW_GUIDE_COVER";

	public static final String SHOW_GUIDE_HOME = "SHOW_GUIDE_HOME";
	
	public static final String SHOW_GUIDE_ROOT = "SHOW_GUIDE_ROOT";
	
	public static final String REMEMBER_MOBILE = "REMEMBER_MOBILE";
	
	public static final String APP_VERSION_NAME = "APP_VERSION_NAME";
	
	public static final String SHOW_DETAIL_HELP = "SHOW_DETAIL_HELP";
	
	public static final String SHOW_DETAIL_HELP_UP = "SHOW_DETAIL_HELP_UP";
	
	public static final String SHOW_DETAIL_HELP_ARROW = "SHOW_DETAIL_HELP_ARROW";

	public static final String MOVIE_DETAIL_TAB_DOT_SHOW = "MOVIE_DETAIL_TAB_DOT_SHOW";
	public static final String DOMAIN_DATA_KEY = "DOMAIN_DATA"; //基础数据信息
	public static final String DOMAIN_METHOD_KEY = "DOMAIN_METHOD"; //基础数据方法
	public static final String DOMAIN_METHOD_TIME_KEY = "DOMAIN_METHOD_TIME"; //基础数据方法更新标志
	public static final String DOMAIN_VALID_TIME_KEY = "DOMAIN_VALID_TIME"; //基础数据有效时间
	//for test
	public static final String API_PATH = "API_PATH";
	public static final String API_FOR_UPLOAD_PATH = "API_FOR_UPLOAD_IMAGE_PATH";

	public static final String LABEL_GUIDE_SHOWED = "LABEL_GUIDE_SHOWED";//第二次启动的时候展示标签引导

	public static final String CINEMA_LIST_WHOLETHEATER = "cinema_list_wholetheater";//影院列表包场引导

	public static final String CINEMA_PLAY_WHOLETHEATER = "cinema_play_wholetheater";//场次详情页包场引导
	
	public static String version="";
	
	private SharedPrefUtil(Context context) {
		sharedPrefs = context.getSharedPreferences(Constant.SHARED, Context.MODE_PRIVATE);
	}
	
	public static SharedPrefUtil getInstance(Context context) {
		if(sharedPrefs == null)
			instance = new SharedPrefUtil(context.getApplicationContext());
		return instance;
	}
	
	public String getString(String key, String defaultValue) {
		return sharedPrefs.getString(key, defaultValue);
	}
	
	public String getString(String key) {
		return sharedPrefs.getString(key, "");
	}
	
	public boolean getBool(String key) {
		return sharedPrefs.getBoolean(key, false);
	}
	
	public boolean getBool(String key, boolean defaultValue) {
		return sharedPrefs.getBoolean(key, defaultValue);
	}
	
	public int getInt(String key, int defaultValue) {
		return sharedPrefs.getInt(key, defaultValue);
	}
	
	public void putString(String key, String value) {
		Editor edit = sharedPrefs.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
	public void putBool(String key, boolean value) {
		Editor edit = sharedPrefs.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}
	
	public void putInt(String key, int value) {
		Editor edit = sharedPrefs.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	/**
	 * 是否第一次安装显示新手帮助
	 *
	 * @return
	 */
	public static boolean shouldShowHelp(Context context) {
		boolean show = SharedPrefUtil.getInstance(context).getBool(SharedPrefUtil.SHOW_HELP, true);
		if (show)
			return true;
		else
			return false;
	}

	/**
	 * 改变显示新手帮助状态
	 *
	 */
	public static void changeShowHelp(Context context) {
		SharedPrefUtil.getInstance(context).putBool(SharedPrefUtil.SHOW_HELP, false);
	}

	//判断是否第一次登陆
	public static boolean isFirstLanch(Context context) {
		String isFirst = SharedPrefUtil.getInstance(context).getString("isFirst", "YES");
		if (isFirst.endsWith("YES")) {
			SharedPrefUtil.getInstance(context).putString("isFirst","NO");
			return true;
		} else {
			return false;
		}
	}

	public static String firstSetup(Context context) {
		String setup = SharedPrefUtil.getInstance(context).getString(SharedPrefrence.SETUP_STATE, null);
		if (setup == null) {
			SharedPrefUtil.getInstance(context).putString(SharedPrefrence.SETUP_STATE, "true");
			return "0";
		}
		return "1";
	}

}
