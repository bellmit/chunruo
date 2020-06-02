package com.chunruo.core.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.RefundReason;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserSociety;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.RefundReasonManager;
import com.chunruo.core.service.UserSocietyManager;
import com.chunruo.core.vo.SendVo;
import com.chunruo.core.vo.TemplateVo;

public class WxSendUtil {

	protected final static transient Log log = LogFactory.getLog(WxSendUtil.class);

	public static void succOrder(Order order,UserInfo userInfo,String productName) {
		try {
			Map<String,Object> data = new HashMap<String,Object>();
			SendVo keyword1 = new SendVo(StringUtil.nullToDoubleFormatStr(order.getPayAmount()));
			SendVo keyword2 = new SendVo(productName);
			SendVo keyword3 = new SendVo(StringUtil.null2Str(userInfo.getNickname()));
			SendVo keyword4 = new SendVo(StringUtil.null2Str(order.getConsigneePhone()));
			
			data.put("amount1", keyword1);
			data.put("thing2", keyword2);
			data.put("name3", keyword3);
			data.put("phone_number4", keyword4);
			TemplateVo templateVo = new TemplateVo(StringUtil.null2Str(userInfo.getOpenId()), "kujnUNPhS7YDuBq9cBg1S2LPo30fuwAHZVIZapB6-Lw",  "pages/allOrder/allOrder", data);
			WxSendUtil.sendWeiXinCircular(templateVo);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void refundSucc(Refund refund,Order order,UserInfo userInfo) {
		try {
			Map<String,Object> data = new HashMap<String,Object>();
			
			data.put("character_string1", StringUtil.null2Str(order.getOrderNo()));
			
			if(StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_MONEY)) {
				data.put("phrase2", "退款");
			}else {
				data.put("phrase2", "退货退款");
			}
			data.put("phrase3", "退款完成");
			data.put("amount4", StringUtil.nullToDoubleFormatStr(refund.getRefundAmount()));
			data.put("thing8", "无");

			RefundReasonManager refundReasonManager = Constants.ctx.getBean(RefundReasonManager.class);
			RefundReason refundReason = refundReasonManager.get(StringUtil.nullToLong(refund.getReasonId()));
			if(refundReason != null && refundReason.getReasonId() != null) {
				data.put("thing8", StringUtil.null2Str(refundReason.getReason()));
			}
			
			String page = String.format("pages/serviceProgress/serviceProgress?orderId=%s&itemId=%s", refund.getOrderId(),refund.getOrderItemId());
			TemplateVo templateVo = new TemplateVo(StringUtil.null2Str(userInfo.getOpenId()), "1MBGE5ncK13z8RT0-g5sP1hIOU27myTUhW0JBD4pF5M", page, data);
			WxSendUtil.sendWeiXinCircular(templateVo);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void withdrawalSucc(Double amount,String tradeNo,UserInfo userInfo) {
		try {
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("phrase1", "已到账");
			data.put("amount2", StringUtil.nullToDoubleFormatStr(amount));
			data.put("number3", tradeNo);

			TemplateVo templateVo = new TemplateVo(StringUtil.null2Str(userInfo.getOpenId()), "QTGGtXEdU6NSyOxJfgDpAcavWWe5_F7VUiqOHykyveg",  "", data);
			WxSendUtil.sendWeiXinCircular(templateVo);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void memberSucc(UserInfo userInfo) {
		try {
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("name1", StringUtil.null2Str(userInfo.getNickname()));
			data.put("amount2", "升级成功");
			data.put("number3", "VIP");
			TemplateVo templateVo = new TemplateVo(StringUtil.null2Str(userInfo.getOpenId()), "J3fs-SkGDTus05MbqBIy7AEMxBUyNuSpvCb7yl_zsBg",  "", data);
			WxSendUtil.sendWeiXinCircular(templateVo);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void sentSucc(Order order,UserInfo userInfo,String productName) {
		try {
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("character_string1", StringUtil.null2Str(userInfo.getNickname()));
			data.put("thing2", productName);
			data.put("amount3", StringUtil.nullToDoubleFormatStr(order.getPayAmount()));
			data.put("phrase4", "已发红");
			data.put("thing5", StringUtil.null2Str(order.getAddress()));

			TemplateVo templateVo = new TemplateVo(StringUtil.null2Str(userInfo.getOpenId()), "HzaLvUNgEeZc2yg3SVJEmqUH82ZhTutcaWmnFptlEsQ",  "", data);
			WxSendUtil.sendWeiXinCircular(templateVo);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取用户微信公众号绑定的openid
	 * @param userInfo
	 * @return
	 */
	public static String getUserOpenId(UserInfo userInfo) {
		String openId = "";
		UserSocietyManager userSocietyManager = Constants.ctx.getBean(UserSocietyManager.class);
		List<UserSociety> userSocietyList = userSocietyManager.getUserSocietyByUnionId(StringUtil.null2Str(userInfo.getUnionId()));
		if(userSocietyList != null && userSocietyList.size() > 0) {
			for(UserSociety userSociety : userSocietyList) {
				if(StringUtil.compareObject(userSociety.getAppConfigId(), Constants.PUBLIC_INVITE_ACCOUNT_CONFIG_ID)) {
					//获取微信公众号绑定的openid
					openId = StringUtil.null2Str(userSociety.getOpenId());
				}
			}
		}
		return openId;
	}
	
	
	/**
	 * 发送微信通知给用户
	 * @param args
	 */
	public static void sendWeiXinCircular(TemplateVo templateVo) {
		try {
			String accessToken = Constants.WEIXIN_TOKEN_MAP.get(Constants.WEIXIN_TOKEN_MAP_KEY);
			if(StringUtil.isNull(accessToken)) {
				//先清空tokenMap
				Constants.WEIXIN_TOKEN_MAP.clear();
				//获取WeixinToken
				WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(Constants.PUBLIC_INVITE_ACCOUNT_CONFIG_ID);
				WxSendUtil.getWeiXinToken(weChatAppConfig.getAppId(), weChatAppConfig.getAppSecret());
				accessToken = Constants.WEIXIN_TOKEN_MAP.get(Constants.WEIXIN_TOKEN_MAP_KEY);
			}

			// 请求参数
			Map<String,Object> pushMap = new HashMap<String,Object>();
			pushMap.put(TemplateVo.WEIXIN_NOTIFY_TOUSER, templateVo.getToUser());
			pushMap.put(TemplateVo.WEIXIN_NOTIFY_URL, templateVo.getUrl());
			pushMap.put(TemplateVo.WEIXIN_NOTIFY_DATA, templateVo.getData());
			pushMap.put(TemplateVo.WEIXIN_NOTIFY_TEMPLATE_ID, templateVo.getTemplateId());

			String body = JsonUtil.map2json(pushMap);
			log.debug("body: " + body +" accessToken: " + accessToken);

			// 发送通知
			String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
			String result = HttpClientUtil.post(url, null, body);
			log.debug("result: " + result);
		} catch (Exception e) {
			log.error("发送微信通知给用户失败,error: " + e.getMessage());
		}
	}

	/**
	 * 获取微信token
	 * @param appId
	 * @param scope
	 * @param redirectURL
	 * @param requestURL
	 * @return
	 */
	public static String getWeiXinToken(String appId, String appSecret){
		String token = Constants.WEIXIN_TOKEN_MAP.get(Constants.WEIXIN_TOKEN_MAP_KEY);
		if (StringUtil.isNull(token)){
			StringBuffer weiXinOauthUrlBuffer = new StringBuffer ();
			try {
				weiXinOauthUrlBuffer.append("https://api.weixin.qq.com/cgi-bin/token?");
				weiXinOauthUrlBuffer.append("appid=" + StringUtil.null2Str(appId));
				weiXinOauthUrlBuffer.append("&secret=" + StringUtil.null2Str(appSecret));
				weiXinOauthUrlBuffer.append("&grant_type=client_credential");
				String result = HttpClientUtil.get(weiXinOauthUrlBuffer.toString());
				System.out.println("getWeiXinToken=========" + StringUtil.null2Str(result));
				JSONObject obj = new JSONObject(result);
				if (obj != null && obj.has("access_token")){
					token = obj.getString("access_token");
					Constants.WEIXIN_TOKEN_MAP.put(Constants.WEIXIN_TOKEN_MAP_KEY, token);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return token;
	}
	
	
		public static void main(String[] args) {
		
		Map<String,Object> data = new HashMap<String,Object>();
		SendVo keyword1 = new SendVo("已到账");
		SendVo keyword2 = new SendVo("100000元");
		SendVo keyword3 = new SendVo("2020202034343");
//		SendVo keyword4 = new SendVo("13098372738");
		data.put("phrase1", keyword1);
		data.put("amount2", keyword2);
		data.put("number3", keyword3);
//		data.put("phone_number4", keyword4);
		TemplateVo circularVo = new TemplateVo("oB1zM4l2slt12puBqAxfG3QkP6cU", "QTGGtXEdU6NSyOxJfgDpAcavWWe5_F7VUiqOHykyveg",  "", data);
		WxSendUtil.sendWeiXinCircular(circularVo);

//		
	}
}
