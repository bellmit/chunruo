package com.chunruo.portal;

public class PortalConstants {
	public static final Integer PAGE_LIST_SIZE = 30;
	public static final Integer NEW_PAGE_LIST_SIZE = 10;
	public static final String DECRYPT_KEY = "2017-cr@chunruo.com!";
	public static final String CODE_UPLOAD_LEVEL = "-11";
	public static final String CODE_SECKILL_OVER = "-12";
	public static final String CODE_NO_AUTH = "-15";
	public static final String CODE_REFUND = "-16";
	public static final String CODE_SUCCESS = "1";
	public static final String CODE_ERROR = "0";
	public static final String MOBILE_ERROR = "-1";
	public static final String CODE_ERROR_NOTAGENT = "2";
	public static final String CODE_NOLOGIN = "-1";			//用户未登陆
	public static final String CODE_NOSTORE = "-2";			//店铺不存在
	public static final String CODE_NOAUTH_PAYMENT = "-3";	//余额是否未认证
	public static final String CODE = "code";
	public static final String MOBILE_CODE = "mobileCode";
	public static final String MSG = "msg";
	public static final String SYSTEMTIME = "systemTime";
	public static final String ORDER_REDIRECT_URI = "order_redirect_uri";
	public static final String USER_CODE="userCode";
	public static final String NORMAL_USER_CODE="-10";
	public static final String CODE_NO_ACTIVITY="-20";
	public static final String CODE_HASH_ACTIVITY="-19";
	public static final String CODE_NOT_MY_ACTIVITY="-21";
	public static final String CODE_NO_VOTER="-22";

	// 短信类型
	public static int CODE_TYPE_REGISTER = 1; 		// 注册用户短信
	public static int CODE_TYPE_LOGIN = 2; 			// 短信登陆
	public static int CODE_TYPE_FORGET = 3;	 		// 修改安全密碼
	public static int CODE_TYPE_MODIFY_PASSWD = 4;	// 修改密码短信
	public static int CODE_TYPE_BIND_MOBILE = 5;	// 绑定手机号码
	public static int CODE_TYPE_REGISTER_SUCC = 6;	// 用户注册成功
	public static int CODE_TYPE_WECHAT_MODIFY = 7;	// 更换微信短信
	public static int CODE_TYPE_ORDER_PAY = 8;      // 下单短信
	
	//请求头信息参数Key
	public static final String X_APP_VERSION = "X-APP-VERSION";
	public static final String X_CLIENT_TYPE = "X-CLIENT-TYPE";
	public static final String X_USER_AGENT = "X-USER-AGENT";
	public static final String X_RESOLUTION = "X-RESOLUTION";
	public static final String X_CHANNEL = "X-CHANNEL";
	public static final String X_TOKEN = "X-TOKEN";
	public static final String X_LEVEL = "X-LEVEL";
	public static final String X_STORE_ID = "X-STORE-ID";
	public static final String X_UUID = "X-UUID";
	public static final String X_UA = "X-UA";
	public static final String X_PLATFORM = "platform";
	public static final String X_USER_ID = "X-USER-ID";
	public static final String X_SHARE_USER = "X-SHARE-USER";
	public static final String X_REQUEST_TYPE = "X-REQUEST-TYPE";

	//session信息
	public final static String SESSION_ALIPAY_WEB_SUCCESS = "login_alipay_web_success";
	public final static String SESSION_LOGIN_SUCC_REDIRECT_URI = "login_succ_redirect_uri";
	public final static String SESSION_WECHAT_CONFIG_ID = "current_wechat_config_id";
	public final static String SESSION_CURRENT_USER = "current_user";
	public final static String SESSION_CURRENT_STORE = "current_store"; 
	public final static String SESSION_CURRENT_OPEN_ID = "current_open_id";
	public final static String SESSION_CURRENT_WECHAT = "current_open_wechat";
	public final static String SESSION_SMS_CODE = "sms_code";
	public final static String SESSION_MODFIY_FORGET_SMS = "current_modfiyForget_scm";
	public final static String SESSION_MODFIY_WECHAT_SMS = "current_modfiyWechat_scm";
	public final static String SESSION_MODFIY_ORDER_SMS = "current_orderPay_scm";
	public final static String SESSION_PAY_PASSWORD = "current_pay_password";
	public final static String SESSION_USER_REGISTER = "current_user_register";
	public final static String SESSION_NOSTORE_BUY_MOBILE = "current_nostore_buy_mobile";
	public final static String TOKEN_DEFUALT_OVERDUE = "no";
	public final static String SHARE_TOKEN_DEFAULT_OVERDUE = "no";
	
	
}

	
