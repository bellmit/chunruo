package com.chunruo.core.util;

import java.util.HashMap;
import java.util.Map;

import com.chunruo.core.Constants;

public class PushConstants {
	public static Map<String, String> HEADER_MAP = new HashMap<String, String>();
	public static final Integer PUSH_TYPE_ALIAS = 1;			//别名
	public static final Integer PUSH_TYPE_TAG = 2;				//标签
	public static final Integer PUSH_TYPE_ALL = 3;				//所有
	
	/** jpush api url */
	public static final String JPUSH_API_URL = "https://api.jpush.cn/v3/push";
	/** jpush device api delete alias url*/
	public static final String JPUSH_DELETE_ALIAS_URL = "https://device.jpush.cn/v3/aliases/%s";
	
	// 自定义消息类型
	public static final String PARAM_CUSTOM_ID = "id";
	public static final String PARAM_CUSTOM_TITLE = "title";
	public static final String PARAM_CUSTOM_MSGTYPE = "msgType";
	public static final String PARAM_CUSTOM_CHILDMSGTYPE = "childMsgType";
	public static final String PARAM_CUSTOM_WEB_URL = "webUrl";
	public static final String PARAM_CUSTOM_MESSAGE_ID = "messageId";
	public static final String PARAM_CUSTOM_IMAGE_URL = "imageUrl";
	public static final String PARAM_CUSTOM_CONETET = "content";
	public static final String PARAM_CUSTOM_CREATE_TIME = "createTime";
	public static final String MUTABLE_CONTENT= "mutable-content";
	
	public static final String PARAM_ALIAS = "alias";
	public static final String PARAM_TITLE = "title";
	public static final String PARAM_ALERT = "alert";
	public static final String PARAM_STYLE = "style";
	public static final Integer PARAM_STYLE_VALUE = 3;
	public static final String PARAM_BIG_PIC_PATH = "big_pic_path";
	public static final String PARAM_BUILDER_ID = "builder_id";
	public static final String PARAM_BADGE = "badge";
	public static final String PARAM_BADGE_ADD = "1";
	public static final String PARAM_SOUND = "sound";
	public static final String PARAM_EXTRAS = "extras";
	public static final String PARAM_ANDROID = "android";
	public static final String PARAM_IOS = "ios";
	public static final String PARAM_PLATFORM = "platform";
	public static final String PARSE_PARAM_ALL = "all";
	public static final String PARSE_PARAM_TAG = "tag";
	public static final String PARSE_PARAM_AND_TAG = "tag_and";
	public static final String PARAM_AUDIENCE = "audience";
	public static final String PARAM_NOTIFI = "notification";
	public static final String APNS_PRODUCTION = "apns_production";
	public static final String OPTIONS = "options";
	
	static {
		// 极光推送
		PushConstants.HEADER_MAP.put("Content-Type", "application/json");
		PushConstants.HEADER_MAP.put("Authorization", Constants.conf.getProperty("jpush.authorization"));
	}
}
