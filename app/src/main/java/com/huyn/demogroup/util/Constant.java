package com.huyn.demogroup.util;

import android.graphics.Bitmap.Config;

/**
 * @author huyn
 * @version 2013-12-9 下午4:43:26
 */
public class Constant {

	public static final boolean DEBUG=true;
	public static final boolean IMG_DEBUG=false;

	public static final boolean ALWAYS_LOAD=false;

	public static final Config BITMAP_CONFIG = Config.RGB_565;

	public static final String SD_CARK_ROOT = "/sdcard";

	public static final String XIAOMI_PUSH_APPID = "2882303761517150002";
	public static final String XIAOMI_PUSH_APPKEY = "5661715073002";

    public static final String UBER_CLIENT_ID = "3_YRmZsY0-kinb1B4EwJjrxRW7w7DGSu";
    //滴滴打车appid
    public static final String DIDI_APPID = "didi6443574A5058682B31325773543851";
    //滴滴打车secret
    public static final String DIDI_SECRET = "4bd825da9c7857db72ca398d96f1059c";
    public static final String ORDER_STATUS_SUCCESS = "success";
    public static final String ORDER_STATUS_PAID = "paid";
    public static final String ORDER_STATUS_NEW = "new";
    public static final String ORDER_STATUS_REFUND = "refund";

    public static final String APP_VERSIONCODE = "APP_VERSIONCODE";

	public static boolean SELECTED_CHANGE_CITY = false;//切换城市dialog是否已显示

	/*
	 * 提供需要解析的超链接，如果是自定义的超链接，则跳转至APP指定页面。否则不予处理
	 * SUPERLINK_1.某家影院 http://www.gewara.com/cinema/47149892 → app影院详情页
	 * SUPERLINK_2.某家影院排片购票页 http://www.gewara.com/cinema/47149892/playtable → app选择影片页
	 * SUPERLINK_3.某部影片 http://www.gewara.com/movie/37090161 → app影片详情页
	 * SUPERLINK_4.某部影片排片购票页 http://www.gewara.com/movie/37090161/playtable → app选择影院页
	 * SUPERLINK_5.某条新闻 http://www.gewara.com/news/92569203 → app新闻详情页
	 * SUPERLINK_6.某个活动 http://www.gewara.com/activity/6157759 → app活动详情页
	 * SUPERLINK_7.具体场次 http://www.gewara.com/cinema/order/step1.shtml?mpid=12680854 → app某场次的选座页
	 */

	public static final String SUPER_LINK_1 = "gewara.com/cinema/";
	public static final String SUPER_LINK_3 = "gewara.com/movie/";
	public static final String SUPER_LINK_6 = "gewara.com/activity/";
	public static final String SUPER_LINK_7 = "gewara.com/cinema/order/";
	public static final String SUPER_LINK_WALA = "gewara.com/wala/";

	public static final String SUPER_LINK_1_TOUCH = "gewara.com/touch/cinema/";
	public static final String SUPER_LINK_3_TOUCH = "gewara.com/touch/movie/";
	public static final String SUPER_LINK_6_TOUCH = "gewara.com/touch/activity/";
	public static final String SUPER_LINK_WALA_TOUCH = "gewara.com/touch/wala/";
	public static final String SUPER_LINK_STAR_TOUCH = "m.gewara.com/touch/movie/personinfo.xhtml?pid=";
	public static final String SUPER_LINK_FOOTMARK_TOUCH = "m.gewara.com/touch/member/getFootMark.xhtml?memberid=";
	public static final String SUPER_LINK_LABEL_TOUCH = "";

	public static final String SUPER_LINK_7_TOUCH = "gewara.com/touch/cinema/order/";

	public static final String SUPER_LINK_WANDA = "wanda/order/";
	public static final String SUPER_LINK_WANDA_EXTRA = "ticketOrder.xhtml?tradeNo=";
	//http://m137.gewara.com:81/touch/home/ticketOrder.xhtml?tradeNo=1150714141645002

	public static final String SUPER_LINK_SELECT_SEAT = "touch/cacheSeat.xhtml?mpid=";

	public static final String SUPER_SHARE_SINA = "gewara://share.sina";
	public static final String SUPER_SHARE_WX = "gewara://share.weixin";
	public static final String SUPER_SHARE_TIMELINE = "gewara://share.timeline";
	public static final String SUPER_SHARE_QQ = "gewara://share.qq";
	public static final String SUPER_SHARE_QZONE = "gewara://share.qzone";
	public static final String SUPER_SHARE_ALL = "gewara://share.all";

	/**
	 * 登录发送的广播
	 */
	public static final String LOGIN_SUCCESS = "ACTION_REFRESH_INFO";

	public static final String AESKEY = "0a2455c";

	public static final String GEWARA_ORDER = "GEWARA_ORDER";

	public static final int DATABASE_VERSION=7;//update on version 6.3

	public static final String SHARED="Gewara";
	public static final String TAG="Gewara2_";
	public static final String MAIN_ACTION="";
	public static final String USER_AGENT="android";
	public static final String SENDER_ID="app@gewara.com";
	public static final String YOUKU_PID="d2311fb79f91b038";

	//推送服务的版本比较后需要停止其他版本较低的推送服务
	public static final String STOP_PUSH_SERVICE = "com.gewara.StopPushService";

	//从哪个APP来的广播
	public static final String FROM_APP = "fromApp";

	//登录失效
	public static final String MEMBERENCODE_ILLEGAL = "MEMBERENCODE_ILLEGAL";

	public static final String NOTICE_CHANGE_PLACE = "notice_change";
	public static final String NOTICE_CHANGE_PLACE_ALERT = "notice_change_place_alert";

	public static final String NOTICE_XIAOMI_PUSH_REGISTER = "notice_xiaomi_push_register";
	public static final String NOTICE_XIAOMI_PUSH_STOP = "notice_xiaomi_push_stop";
	public static final String NOTICE_XIAOMI_PUSH_REGISTER_SUCCESS = "notice_xiaomi_push_register_success";
	public static final String XIAOMI_PUSH_REGIST_ID = "xiaomi_push_register_id";//小米push注册ID

	public static final String AUTH_SUCCESS_FROM_WX = "AUTH_SUCCESS_FROM_WX";
	public static final String CARD_ADD_WX = "CARD_ADD_WX";
	public static final String AUTH_CODE = "AUTH_CODE";
	public static final String AUTH_URL = "AUTH_URL";
	public static final String AUTH_STATE = "AUTH_STATE";
	public static final String NOTICE_USERCARD_REDPACK_WX = "NOTICE_USERCARD_REDPACK_WX";

	//更新所有城市后，发送通知，更新城市列表
	public static final String NOTICE_UPDATE_ALL_CITYS = "notice_updated_all_citys";

	//在城市列表act中点击任意一个城市，进行切换后
	public static final String CITY_SETTING_CHANGE = "change_setting_change";

	public static final String INTENT_CLEAR_CACHE = "com.android.packageinstaller.CLEAR_CACHE";

	//绑定手机号后状态更新
	public static final String ACCOUNT_INFO_REFRESH = "account_info_refresh";

	public static String APP_MOBILETYPE = "";
	public static String APP_IMEI = "";
	public static String APP_DEVICEID = "";
	public static String APP_NET_STATE = "";
	public static String APP_NET_PROVIDER = "";
	public static String APP_CITYCODE = "";
	public static String APP_CITYNAME = "";
	public static String APP_POINTX = "";
	public static String APP_POINTY = "";

	public static String APP_KEY_NET_STATE = "mnet";
	public static String APP_KEY_NET_PROVIDER = "mprovider";
	public static String APP_KEY_MOBILETYPE = "mobileType";
	public static String APP_KEY_DEVICEID = "deviceId";
	public static String APP_KEY_IMEI = "imei";
	public static String APP_KEY_CITYCODE = "citycode";
	public static String APP_KEY_CITYNAME = "cityname";
	public static String APP_KEY_POINTX = "pointx";
	public static String APP_KEY_POINTY = "pointy";

	public static boolean isShoufa() {
//		return Constant.APP_BAIDU.equalsIgnoreCase(Constant.APP_SOURCE);
		return false;
	}

	public static boolean enableWelcomScreen() {
		return false;
//		return "AS128".equalsIgnoreCase(Constant.APP_SOURCE);
	}

	public static final String BAIDU_SOURCE = "AS200";
	public static final String APP_BAIDU = "AS134";//百度手机助手领券
	public static final String APP_UNICOM = "AS132";//联通沃商城
	public static final String APP_360 = "AS23";
	public static final String APP_91 = "AS120";

	//应用来源
	public static String APP_SOURCE = "AS01";

	//os类型
	public static final String OS_TYPE = "ANDROID";
	public static final String APP_TAG = "cinema";
	public static final String APP_TYPE = "cinema";
	public static final String SPECIAL_IMEI="000000000000000";
	public static final String APP_DRAMA_TYPE = "drama";
	/**
	 * 自动登录类型
	 **/
	public static final String AUTO_LOGIN_TYPE="AUTOLOGINTYPE";
	/**
	 * 商户相关配置
	 */
	public static final String KEY = "android2009";
	public static final String PRIVATE_KEY = "prikey-android";
	/**
	 * 演出appkey
	 */
	public static final String DRAMA_KEY = "androiddramainmovie";
	/**
	 * 设置自动登录类型
	 **/
	public static final String LOGIN_THRID="1"; //第三方登录
	public static final String LOGIN_ALI="2"; //支付宝钱包登录
	public static final String LOGIN_COMMON="3"; //普通登录

	public static final String API_VERSION = "1.0";

	public static final String CITY_CODE_DEFAULT_SHANGHAI = "310000";
	public static final String CITY_NAME_DEFAULT_SHANGHAI = "上海";
	public static final double POINTX_DEFAULT_SHANGHAI=121.47494494915009;
	public static final double POINTY_DEFAULT_SHANGHAI=31.234914894041356;

	/**application session key**/
	public static final String VERSION = "version";
	public static final String APP_VERSION = "appVersion";
	public static final long DKEY_TIME = 60 * 1000;//获取动态验证码的时间间隔 60 秒

	public static final String USER_LOGOUT_STATE = "logoutState";

	public static final String USER_PASSWORD = "user_password";
	public static final String CITY_CODE = "city_code";
	public static final String CITY_NAME = "city_name";
	public static final String GPS_CITYCODE = "gps_citycode";
	public static final String GPS_CITYNAME = "gps_cityname";
	public static final String GPS_ADDRESS = "gps_address";
	public static final String USER_NAME="user_name";
	public static final String USER_HEAD="user_head";
	public static final String MEMBER_ENCODE="memberEncode";
	public static final String MEMBER_EMAIL = "email";
	public static final String FORM_WHERE = "fromWhere";
	public static final String USER_TRACENUM="trace_num";
	public static final String USER_BETOTAL="betotal";
	public static final String LOAD_USER_OTHER_INFO = "LoadUserOtherInfo";
	public static final String SECURITY_CODE = "securityCode";

	/** CC视频 **/
	public static final String CC_API_KEY = "zKds7umiZKu4rn7SqggY0VYxgaZw7d0Z";
	public static final String CC_USERID = "E4AE228E2DC39B85";

	/** Gewara存储目录/文件 **/
	public static final String GEWARA_DIR = "/gewara";
	public static final String GEWARAPUSH_DIR = "/gewarapush";
	public static final String GEWARA_SERVICE = "/gewaraservice";
	public static final String CACHE_CACHE = GEWARA_DIR + "/cache";
	public static final String GEWARA_TEMP = GEWARA_DIR + "/temp";
	public static final String GEWARA_LOG = GEWARA_DIR + "/logs/";
	public static final String GEWARA_HTML = GEWARA_DIR + "/html/";
	public static final String GEWARA_AUTHENTICATE = GEWARA_DIR + "/auth/";
	public static final String CACHE_DIR = GEWARA_DIR + "/images";
	public static final String CAMERA_TEMP = GEWARA_DIR + "/images/camera_temp.jpg";
	public static final String IMAGE_TEMP = GEWARA_DIR + "/images/temp.jpg";
	public static final String DATABASE_FILE = GEWARA_DIR + "/gewara.db";
	public static final String UPDATE_APK_DIR = GEWARA_DIR + "/download";
	public static final String GEWARA_FILES_SECURITY = GEWARA_DIR + "/files/gewara.info";

	public static final String UPDATE_APK_NAME_PREFIX = "GewaraMovieV";
	public static final String UPDATE_APK_NAME_SUFIX = ".apk";

	/**支付方式选项常量**/
	/** 格瓦拉帐户支付*/
	public static final String GEWARA_PAY = "gewaPay";

	/** 银联支付*/
	public static final String UNION_PAY = "chinaSmartMobilePay";

	/** 6.3.1开始，新版银联支付*/
	public static final String UNION_PAY_NEW = "unionAppPay";

	/** 支付宝移动快捷支付*/
	public static final String ALI_KUAI_PAY = "aliSmartMobilePay";

	/** 支付宝移动快捷支付*/
	public static final String ALI_APP_PAY = "aliAppPay";

	/** 支付宝WAP支付*/
	public static final String ALI_WAP_PAY = "aliwapPay";

	/** 移动手机安全支付*/
	public static final String CM_PAY = "cmSmartPay";

	/** 微信app支付*/
	public static final String WX_APP_PAY = "wxAppTenPay";

	/** 微信支付*/
	public static final String WX_WC_PAY = "wxWCPay";

	/** 建行wap支付*/
	public static final String CCB_PAY = "ccbWapPay";

	/** 建行wap支付*/
	public static final String CCB_PAY_SND = "ccbWapSecdPay";

	/**财付通一键支付*/
	public static final String TENCENT_PAY = "oneClickTenPay";

	/**浦发银行手机支付*/
	public static final String PUFA_PAY = "spdWapPay";

	/**招商银行支付*/
	public static final String ZS_PAY = "cmbwapPay";

	/**中国银行支付*/
	public static final String ZH_PAY = "bocWapPay";

	/**百度钱包*/
	public static final String BAIDU_WALLET = "bfbWapPay";

	/**银联wap*/
	public static final String UNION_WAP_PAY = "unionWapPay";

	/**qq钱包*/
	public static final String QQ_WALLET = "qqAppPay";

	/**工行wap*/
	public static final String ICBC_WAP = "icbcWapPay";

	/**京东支付*/
	public static final String JD_PAY = "jdWapPay";

	/**电信翼支付 from 6.5*/
	public static final String CHINA_TELE_PAY = "chinaTeleAppPay";

	/**
	 * 友盟统计
	 */
	public static final String UMENG_EVENT_MOVIE_MORE = "MOVIE_MORE";
	public static final String UMENG_EVENT_MOVIE_MORE_ACT = "MOVIE_MORE_ACT";
	public static final String UMENG_EVENT_MOVIE_PAY = "MOVIE_PAY";

	public static final String UMENG_EVENT_MOVIE_VIDEO = "MOVIE_VIDEO";

	public static final String UMENG_EVENT_MOVIE_AD_1 = "MOVIE_HOME_AD_TOP";
	public static final String UMENG_EVENT_MOVIE_AD_2 = "MOVIE_HOME_AD_MIDDLE";
	public static final String UMENG_EVENT_MOVIE_AD_3 = "MOVIE_HOME_AD_BOTTOM";
	public static final String UMENG_EVENT_MOVIE_AD_4 = "MOVIE_ACT_AD";

	public static final String UMENG_EVENT_INVOKE_BY_ALIWALLET = "MOVIE_FROM_ALIWALLET";
	public static final String UMENG_EVENT_INVOKE_VIAALICHANNEL = "MOVIE_FROM_ALIWALLETCHANNEL";
	public static final String UMENG_EVENT_INVOKE_NOTBY_ALIWALLET_VIAALICHANNEL = "MOVIE_FROM_ALIWALLETCHANNEL_BESIDES_WALLET";

	public static final String UMENG_LABLE_BY_ALIWALLET = "支付宝钱包启动";
	public static final String UMENG_LABLE_VIAALICHANNEL = "来自支付宝钱包渠道";
	public static final String UMENG_LABLE_NOTBY_ALIWALLET_VIAALICHANNEL = "来自支付宝钱包渠道不通过钱包启动";

	public static final String ALIWALLET_SOURCE = "AS124";
	public static final String SAMSUNG_SOURCE = "AS128";

	//座位大小和缩放设置参数
	/**座位缩略图的尺寸大小*/
	public static int SEAT_THUMB_NUM = 20;
	/**座位正常布局的宽高*/
	public static final int SEAT_WIDTH_HEIGHT = 40;
	/**放大最大值*/
	public static int ZOOM_MAX_NUM = 60;
	/**每次放大缩小的数量*/
	public static final int ZOOM_NUM = 5;

	/**支付宝安全支付相关参数 **/
	public static final String ALIPAY_RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCENYmUCi3/A80RzeOMZuTUs7PbvrOthMlSwBAv5QgukiOs27r/6Dh1GPmJroGjYP/iGRz5ivxuWXjPRqVpX1ZCtinZULLgy5H8NJuQULAl2mQUrp/5JYqxW4/t7EwsyRfmUCGaiEy+Mh4FS8B5wZCHM4rfh5tydko4rudPHHGl1QIDAQAB";
	public static final String ALIPAY_PLUGIN_NAME = "alipay_plugin.apk";
	public static final String ALIPAY_SERVER_URL = "https://msp.alipay.com/x.htm";
	public static final String ALIPAY_SIGN_TYPE = "RSA";

	public static final String SCREENWIDTH = "screenWidth";
	public static final String SCREENHEIGHT = "screenHeight";
	public static final String STATUSHEIGHT = "statusHeight";
	public static final String ISLARGESCREEN = "largeScreen";
	public static final String ISXLARGE = "isXLarge";
	public static final String ISXXLARGE = "isXXLarge";
	public static final String SDK_VERSION = "SDK_VERSION";

	public static final String QRCODE_GUIDE = "QRCODE_GUIDE";

	public static final int COVER_AD_TIMER = 2000; //cover 页面大广告停留时间
	public static final String CHARSET_UTF8 = "UTF-8";

	public static String[][] LOCALCITY = new String[][]{

		{"310000","上海","直辖市","shanghai"},
		{"110000","北京","直辖市","beijing"},
		{"500000","重庆","直辖市","chongqing"},

		{"330100","杭州","浙江省","hangzhou"},
		{"330200","宁波","浙江省","ningbo"},
		{"330400","嘉兴","浙江省","jiaxing"},
		{"330600","绍兴","浙江省","shaoxing"},
		{"330500","湖州","浙江省","huzhou"},
		{"331000","台州","浙江省","taizhou"},

		{"320100","南京","江苏省","nanjing"},
		{"320200","无锡","江苏省","wuxi"},
		{"320400","常州","江苏省","changzhou"},
		{"320500","苏州","江苏省","suzhou"},
		{"320600","南通","江苏省","nantong"},

		{"440100","广州","广东省","guangzhou"},
		{"440300","深圳","广东省","shenzhen"},

		{"510100","成都","四川省","chengdu"},

		{"420100","武汉","湖北省","wuhan"},

	};

	/**
	 * 第三方登录信息
	 */
	public static String TD_USERID= "TD_USERID";
	public static String TD_TOKEN = "TD_TOKEN";
	public static String TD_NICKNAME = "TD_NICKNAME";
	public static String TD_SOURCE = "TD_SOURCE";

	public static String TD_LOGIN_SINA = "sina";
	public static String TD_LOGIN_TENCENT = "tencent";
	public static String TD_LOGIN_TAOBAO = "taobao";
	public static String TD_LOGIN_ALIPAY = "alipay";
	public static String TD_LOGIN_ALIPAY_WALLET = "alipay_wallet";
	public static String TD_LOGIN_WECHAT = "wechat";


	public static String ALIPAY_WALLET_USERID = "alipay_user_id";
	public static String ALIPAY_WALLET_APPID = "app_id";
	public static String ALIPAY_WALLET_AUTHCODE = "auth_code";
	public static String ALIPAY_WALLET_APPVERSION = "alipay_gewara_version";
	public static String ALIPAY_WALLET_WALLETVERSION = "alipay_client_version";


	/** 不需要更新*/
	public static final int UPDATE_NOTING = 0;
	/** 用户自己选择是否更新*/
	public static final int UPDATE_SPECIFIC = 1;
	/** 强制更新*/
	public static final int UPDATE_FORC = 2;

	public static final String WALA_SPACE_NORMAL = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	public static final String WALA_SPACE_SAMALL = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

	public static final int WALA_PUSH_TIME_HOUR=12;
	public static final int WALA_PUSH_TIME_MINUTE=0;

	public static final String USER_PARTNER_UPDATE = "user_partner_update";
	public static final String BUSINESS_TYPE = "business_type";

	public static final boolean LABEL_ENABLE = true;
	public static final boolean POLL_SHARE_ENABLE = true;
	public static final int ILLEGAL_ID = -1;

	public static final String TASK_REWARD_PUSH_ID = "task_reward_push_id"; // 电影圈补全信息的pushid
}
