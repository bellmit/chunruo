package com.chunruo.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.PushMessage;
import com.chunruo.core.model.SystemSendMsg;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.HttpClientUtil;
import com.chunruo.core.util.JsonUtil;
import com.chunruo.core.util.StringUtil;

public class PushThreadPool {
//	private static final Log log = LogFactory.getLog(PushThreadPool.class);
//	static int corePoolSize = 1;
//	static int maximumPoolSize = 3;
//	static int keepAliveTime = 20;
//	static long waitTime = 10000;
//	
//	static ThreadPoolExecutor workers;
//
//	public static void init() {
//		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
//		RejectedExecutionHandler handler = new RejectedExecutionHandler() {
//			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
//				try{
//					Thread.sleep(PushThreadPool.waitTime);
//				}catch (InterruptedException e) {
//					e.printStackTrace();
//					log.debug(e.getMessage());
//				}
//				executor.execute(r);
//			}
//		};
//
//		if (workers == null) {
//			workers = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue, handler);
//		}
//	}
//	
//	public static ThreadPoolExecutor getThreadPoolExecutor(){
//		if (workers == null){
//			init();
//		}
//		return workers;
//	}
//	
//	/**
//	 * 别名推送消息
//	 * @param msgType
//	 * @param userId
//	 * @param objectId
//	 * @param content
//	 * @param isNeedSave
//	 */
//	public synchronized static void pushAlias(PushMessage pushMessage,Object[] aliasUserIdArray) {
//		try{
//			if (pushMessage == null || StringUtil.isNull(pushMessage.getMsgContent())) {
//				return;
//			}
//			if (workers == null)
//				init();
//			workers.execute(new PushThread(pushMessage,aliasUserIdArray));
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//}
//
//class PushThread implements Runnable {
//	private final static Log log = LogFactory.getLog(PushThread.class);
//	private PushMessage pushMessage = null;
//	private Object[] aliasUserIdArray = null;
//	public PushThread(PushMessage pushMessage,Object[] aliasUserIdArray) {
//		if (Constants.ctx != null) {
//			this.pushMessage = pushMessage;
//			this.aliasUserIdArray = aliasUserIdArray;
//		} else {
//			log.error("ctx is empty !");
//		}
//	}
//
//	@SuppressWarnings("static-access")
//	public void run() {
//		try {
//			if(this.pushMessage == null){
//				return;
//			}
//			
//			if(StringUtil.compareObject(PushMessage.MSG_TYPE_COUPON, this.pushMessage.getMsgType())
//					&& (this.aliasUserIdArray == null || this.aliasUserIdArray.length<= 0)) {
//				//推送优惠券，用户不能未空
//				return ;
//			}
//			
//			//系统消息根据标签或者all 推送
//			Set<Integer> messageTypeSet = new HashSet<Integer>();
//			messageTypeSet.add(PushMessage.MSG_TYPE_SYSTEM_ACTIVITY);
//			messageTypeSet.add(PushMessage.MSG_TYPE_SYSTEM_NOTICE);
//			messageTypeSet.add(PushMessage.MSG_TYPE_GOODS);
//			messageTypeSet.add(PushMessage.MSG_TYPE_SINGLE_PRODUCT);
//			// 检查图片地址补全http
//			if(!StringUtil.isNull(this.pushMessage.getImageUrl()) && (!this.pushMessage.getImageUrl().startsWith("http://")
//					|| !this.pushMessage.getImageUrl().startsWith("https://"))){
//				this.pushMessage.setImageUrl(Constants.conf.getProperty("jkd.domain.name.path") + this.pushMessage.getImageUrl());
//			}
//			
//			String pushRebackMsg = "";
//			if (messageTypeSet.contains(this.pushMessage.getMsgType())){
//				// 消息推送方式
//				if(StringUtil.compareObject(this.pushMessage.getObjectType(), StringUtil.null2Str(SystemSendMsg.OBJECT_TYPE_CUSTOM))){
//					pushRebackMsg = this.pushByAlias(this.pushMessage, PushConstants.PUSH_TYPE_ALIAS,null);
//				}else if(StringUtil.compareObject(this.pushMessage.getObjectType(), SystemSendMsg.OBJECT_TYPE_ALL)){
//					pushRebackMsg = this.pushByAlias(this.pushMessage, PushConstants.PUSH_TYPE_ALL,null);
//				}else {
//					pushRebackMsg = this.pushByAlias(this.pushMessage, PushConstants.PUSH_TYPE_TAG,null);
//				}
////				//统一按标签推送
////				pushRebackMsg = this.pushByAlias(this.pushMessage, PushConstants.PUSH_TYPE_TAG,null);
//			}else{
//				//非系统消息根据别名推送
//				List<Integer> otherProfitTypeList = new ArrayList<Integer> ();
////				otherProfitTypeList.add(PushMessage.MSG_TYPE_INVITER);		//团队邀请
//				otherProfitTypeList.add(PushMessage.MSG_TYPE_WITHDRAWAL);	//提现消息
//				otherProfitTypeList.add(PushMessage.MSG_TYPE_RECHARGE);     //用户充值
//				//转换成互动消息
//				List<Integer>  interactionTypeList = new ArrayList<Integer>();
//				interactionTypeList.add(PushMessage.MSG_TYPE_INVITER);
//				interactionTypeList.add(PushMessage.MSG_TYPE_EXCULLENT_EVALUATE);
//				if(otherProfitTypeList.contains(this.pushMessage.getMsgType())){
//					//转换成收益消息
//					this.pushMessage.setMsgType(PushMessage.MSG_TYPE_PROFIT);
//				}else if(interactionTypeList.contains(this.pushMessage.getMsgType())) {
//					//转换成活动消息
//					this.pushMessage.setMsgType(PushMessage.MSG_TYPE_INTERACTION);
//				}
//				pushRebackMsg = this.pushByAlias(this.pushMessage, PushConstants.PUSH_TYPE_ALIAS,this.aliasUserIdArray);
//			}
//			
//			String strLog = "[msgType=%s, msgContent=%s, pushRebackMsg=%s]";
//			log.debug(String.format(strLog, StringUtil.null2Str(this.pushMessage.getMsgType()), StringUtil.null2Str(this.pushMessage.getMsgContent()), StringUtil.null2Str(pushRebackMsg)));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * 
//	 * @param message
//	 * @param type	1 通过别名推送 2-通过标签推送 3-给所有人推送
//	 * @return
//	 */
//	private static String pushByAlias(PushMessage message , Integer type,Object[] aliasUserIdArray) {
//		if (message == null){
//			return null;
//		}
//		
//		if(StringUtil.compareObject(PushMessage.MSG_TYPE_COUPON, message.getMsgType())
//				&& (aliasUserIdArray == null || aliasUserIdArray.length<= 0)) {
//			//推送优惠券，用户不能未空
//			return null;
//		}
//
//		// 精彩活动消息只推送标题
//		String alert = StringUtil.null2Str(message.getMsgContent());
//		String title = StringUtil.null2Str(message.getTitle());
////		if(StringUtil.compareObject(message.getMsgType(), PushMessage.MSG_TYPE_SYSTEM_ACTIVITY)){
////			alert = message.getTitle();
////		}
//
//		// 对象转换
//		Set<Integer> messageTypeSet = new HashSet<Integer>();
////		messageTypeSet.add(PushMessage.MSG_TYPE_RETURN);
//		messageTypeSet.add(PushMessage.MSG_TYPE_INTERACTION);
////		messageTypeSet.add(PushMessage.MSG_TYPE_SYSTEM_ACTIVITY);
////		messageTypeSet.add(PushMessage.MSG_TYPE_GOODS);
//		if (messageTypeSet.contains(message.getMsgType())
//				|| (StringUtil.compareObject(PushMessage.MSG_TYPE_ORDER, message.getMsgType())
//				&& StringUtil.compareObject(PushMessage.SKIP_PAGE_REFUND, message.getChildMsgType()))){
//			message.setObjectId(message.getRelationId());
//		}
//
//		//自定义消息内容
//		Map<String, Object> extrasMap = new HashMap<String, Object>();
//		extrasMap.put(PushConstants.PARAM_CUSTOM_MSGTYPE, StringUtil.null2Str(message.getMsgType()));
//		extrasMap.put(PushConstants.PARAM_CUSTOM_CHILDMSGTYPE, StringUtil.null2Str(message.getChildMsgType()));
//		extrasMap.put(PushConstants.PARAM_CUSTOM_WEB_URL, StringUtil.null2Str(message.getWebUrl()));
//		extrasMap.put(PushConstants.PARAM_CUSTOM_ID, message.getObjectId());
//		extrasMap.put(PushConstants.PARAM_CUSTOM_MESSAGE_ID, message.getMsgId());
//		extrasMap.put(PushConstants.PARAM_CUSTOM_IMAGE_URL, message.getImageUrl());
//		extrasMap.put(PushConstants.PARAM_CUSTOM_CONETET, message.getMsgContent());
//		extrasMap.put(PushConstants.PARAM_CUSTOM_CREATE_TIME, DateUtil.getSeconds(new Date()));
//		extrasMap.put(PushConstants.PARAM_CUSTOM_TITLE, message.getTitle());
//
//		// 设置终端消息类型受众
//		Map<String, Object> pushMap = new HashMap<String, Object>();
//		Map<String, Object> audienceMap = new HashMap<String, Object>();
//		if (StringUtil.nullToBoolean(Constants.conf.getProperty("jpush.apns_production"))){
//			// 正式环境
//			if (StringUtil.compareObject(type, PushConstants.PUSH_TYPE_ALIAS)){
//				//根据别名推送
//				Object[] aliasArray = null;
//				if(StringUtil.compareObject(PushMessage.MSG_TYPE_COUPON, message.getMsgType())) {
//					//推送优惠券
//					aliasArray = aliasUserIdArray;
//				}else {
//					aliasArray = new Object[]{ message.getUserId() };
//				}
//				audienceMap.put(PushConstants.PARAM_ALIAS, aliasArray);
//			}else if (StringUtil.compareObject(type, PushConstants.PUSH_TYPE_TAG)){
//				//根据标签推送
////				Object[] tagArray = new Object[]{ "specialdealer" };
////				audienceMap.put(PushConstants.PARSE_PARAM_AND_TAG, tagArray);
//				List<Integer> objectTypes = StringUtil.stringToIntegerArray(message.getObjectTypes());
//				if(objectTypes != null && !objectTypes.isEmpty()) {
//					Object[] tagArray = new Object[objectTypes.size()];
//					for(int i =0;i<objectTypes.size();i++) {
//						Integer level = StringUtil.nullToInteger(objectTypes.get(i));
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_VIP0)) {
//							//推送对象vo
//							tagArray[i] = "v0";
//						}
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_VIP1)) {
//							//推送对象v1、v2、v3
//							tagArray[i] = "v1";
//						}
//						
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_V1)) {
//							//推送对象v1
//							tagArray[i] = "VIP1";
//						}
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_V2)) {
//							//推送对象v1
//							tagArray[i] = "v2";
//						}
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_V3)) {
//							//推送对象v1
//							tagArray[i] = "v3";
//						}
//						
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_PUSH1)) {
//							//推送对象实习推手
//							tagArray[i] = "commompusher";
//						}
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_PUSH2)) {
//							//推送对象高级推手
//							tagArray[i] = "higherpusher";
//						}
//					}
//					audienceMap.put(PushConstants.PARSE_PARAM_TAG, tagArray);
//				}
//			}else if (StringUtil.compareObject(type, PushConstants.PUSH_TYPE_ALL)){
//				//正式环境推送给所有用户
//				pushMap.put(PushConstants.PARAM_AUDIENCE, PushConstants.PARSE_PARAM_ALL);
//			}
//		}else{
//			// 测试环境
//			String testPushLabel = "test";
//			if (StringUtil.compareObject(type, PushConstants.PUSH_TYPE_ALIAS)){
//				//根据别名推送
//				Object[] aliasArray = null;
//				if(StringUtil.compareObject(PushMessage.MSG_TYPE_COUPON, message.getMsgType())) {
//					//推送优惠券
//					aliasArray = aliasUserIdArray;
//				}else {
//					aliasArray = new Object[]{String.format("%s_%s", testPushLabel, message.getUserId())};
//				}
//				audienceMap.put(PushConstants.PARAM_ALIAS, aliasArray);
//			}else if (StringUtil.compareObject(type, PushConstants.PUSH_TYPE_TAG)){
//				//根据标签推送
//				List<Integer> objectTypes = StringUtil.stringToIntegerArray(message.getObjectTypes());
//				if(objectTypes != null && !objectTypes.isEmpty()) {
//					Object[] tagArray = new Object[objectTypes.size()];
//					Object[] testTagArray = new Object[]{testPushLabel};
//					for(int i =0;i<objectTypes.size();i++) {
//						Integer level = StringUtil.nullToInteger(objectTypes.get(i));
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_VIP0)) {
//							//推送对象vo
//							tagArray[i] = "v0";
//							
//						}
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_VIP1)) {
//							//推送对象v1、v2、v3
//							tagArray[i] = "v1";
//						}
//						
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_V1)) {
//							//推送对象v1
//							tagArray[i] = "VIP1";
//						}
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_V2)) {
//							//推送对象v1
//							tagArray[i] = "v2";
//						}
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_V3)) {
//							//推送对象v1
//							tagArray[i] = "v3";
//						}
//						
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_PUSH1)) {
//							//推送对象实习推手
//							tagArray[i] = "commompusher";
//						}
//						if(StringUtil.compareObject(level, SystemSendMsg.OBJECT_TYPE_PUSH2)) {
//							//推送对象高级推手
//							tagArray[i] = "higherpusher";
//						}
//					}
//					audienceMap.put(PushConstants.PARSE_PARAM_TAG, tagArray);
//					audienceMap.put(PushConstants.PARSE_PARAM_AND_TAG, testTagArray);
//				}
//
//			}else if (StringUtil.compareObject(type, PushConstants.PUSH_TYPE_ALL)){
//				//所有用户推送
//				Object[] tagArray = new Object[]{ testPushLabel };
//				audienceMap.put(PushConstants.PARSE_PARAM_AND_TAG, tagArray);
//			}
//		}
//		
//		//设置受众
//		if (audienceMap != null && audienceMap.size() > 0){
//			pushMap.put(PushConstants.PARAM_AUDIENCE, audienceMap);
//		}
//		
//		//APNs是否生产环境  True 表示推送生产环境，False 表示要推送开发环境； 如果不指定则为推送生产环境。
//		Map<String,Object> apnsMap = new HashMap<String,Object>();
//		apnsMap.put(PushConstants.APNS_PRODUCTION,StringUtil.nullToBoolean(Constants.conf.getProperty("jpush.apns_production")));
//		
//		//android消息
//		Map<String, Object> androidMap = new HashMap<String, Object>();
//		androidMap.put(PushConstants.PARAM_TITLE, title);
//		androidMap.put(PushConstants.PARAM_ALERT, alert);
//		androidMap.put(PushConstants.PARAM_BUILDER_ID, 1);
//		androidMap.put(PushConstants.PARAM_STYLE, PushConstants.PARAM_STYLE_VALUE);
//		androidMap.put(PushConstants.PARAM_BIG_PIC_PATH, message.getImageUrl());
//		androidMap.put(PushConstants.PARAM_EXTRAS, extrasMap);
//
//		//ios消息
//		Map<String, Object> iosMap = new HashMap<String, Object>();
//		JSONObject object = new JSONObject();
//		object.put("title", title);
//		object.put("body", alert);
//		iosMap.put(PushConstants.PARAM_ALERT, object);
//		iosMap.put(PushConstants.PARAM_BADGE, PushConstants.PARAM_BADGE_ADD);
//		iosMap.put(PushConstants.PARAM_EXTRAS, extrasMap);
//		iosMap.put("sound", "sound.caf");
//		iosMap.put(PushConstants.MUTABLE_CONTENT, 1);
//		//android消息和ios消息加入消息推送
//		Map<String, Object> notiDataMap = new HashMap<String, Object>();
//		notiDataMap.put(PushConstants.PARAM_ANDROID, androidMap);
//		notiDataMap.put(PushConstants.PARAM_IOS, iosMap);
//		
//		pushMap.put(PushConstants.OPTIONS, apnsMap);
//		pushMap.put(PushConstants.PARAM_PLATFORM, PushConstants.PARSE_PARAM_ALL);
//		pushMap.put(PushConstants.PARAM_NOTIFI, notiDataMap);
//		return HttpClientUtil.post(PushConstants.JPUSH_API_URL, PushConstants.HEADER_MAP, JsonUtil.map2json(pushMap));
//	}	
}
