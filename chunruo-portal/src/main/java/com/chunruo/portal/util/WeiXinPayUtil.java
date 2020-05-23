package com.chunruo.portal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.SSLContext;
import java.util.UUID;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import com.chunruo.core.Constants;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.util.HttpClientUtil;
import com.chunruo.core.util.Md5Util;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.XmlParseUtil;
import com.chunruo.core.vo.MsgModel;

/**
 * 微信支付
 * @author chunruo
 *
 */
@SuppressWarnings("deprecation")
public class WeiXinPayUtil {
	public final static transient Log log = LogFactory.getLog(WeiXinPayUtil.class);
	public static final String DES_ENCRYPT_CRYPT_KEY = "2934chunruo@163.com#secret";
	
	/**
	 * 订单金额转换成分
	 * @param orderAmount
	 * @return
	 */
	public static int orderAmountToBranch(Double orderAmount){
		try{
			BigDecimal bigDecimal1 = new BigDecimal(Double.toString(100));
			BigDecimal bigDecimal2 = new BigDecimal(Double.toString(orderAmount));
			return bigDecimal1.multiply(bigDecimal2).intValue();
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 微信支付单信息
	 * @param weChatAppConfig
	 * @param outTradeNo
	 * @param openId
	 * @param notifyURL
	 * @param orderTotal
	 * @param body
	 * @param clientIp
	 * @return
	 */
	public static MsgModel<Map<String, String>> getWeixinH5PayInfo(WeChatAppConfig weChatAppConfig, String outTradeNo, String openId, String notifyURL, int orderTotal, String body, String clientIp, String tradeType, Long productId){
		MsgModel<Map<String, String>> resultModel = new MsgModel<Map<String, String>> ();
		String errorMsg = "微信支付单信息请求异常";
		try {
			// 第一步获取统一下单
			MsgModel<Map<String, Object>> msgModel = WeiXinPayUtil.requestUnifiedOrder(weChatAppConfig, outTradeNo, openId, notifyURL, orderTotal, body, clientIp, tradeType, productId);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
				resultModel.setIsSucc(false);
				resultModel.setMessage(msgModel.getMessage());
				return resultModel;
			}
			
			Map<String, Object> objectMap = msgModel.getData();
			if(objectMap != null  && objectMap.size() > 0) {
				// 获取微信支付请求错误信息
				if(objectMap.containsKey("err_code_des")) {
					errorMsg = StringUtil.null2Str(objectMap.get("err_code_des"));
				}
				
				// 获取微信请求支付信息
				if(objectMap.containsKey("prepay_id")){
					//判断是否跨号支付
					tradeType = StringUtil.nullToString(objectMap.get("trade_type"));
					if(tradeType.equals("NATIVE")){
						Map<String, String> resultMap = new HashMap<String, String> ();
						String code_url = StringUtil.nullToString(objectMap.get("code_url"));
						resultMap.put("code_url", code_url);
						
						resultModel.setIsSucc(true);
						resultModel.setData(resultMap);
						resultModel.setPaymentBody(msgModel.getPaymentBody());
						return resultModel;
					}else{
						String nonceStr = StringUtil.nullToString(objectMap.get("nonce_str"));
						String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
						String packages = "prepay_id=" + StringUtil.null2Str(objectMap.get("prepay_id"));
						List<NameValuePair> nvpList = new ArrayList<NameValuePair> ();
						nvpList.add(new NameValuePair("appId", StringUtil.null2Str(weChatAppConfig.getAppId())));			//公众号id
						nvpList.add(new NameValuePair("timeStamp", StringUtil.null2Str(timeStamp)));						//时间戳
						nvpList.add(new NameValuePair("nonceStr", StringUtil.null2Str(nonceStr))); 							//随机字符串，长度要求在32位以内。推荐随机数生成算法
						nvpList.add(new NameValuePair("package", StringUtil.null2Str(packages)));							//统一下单接口返回的prepay_id参数值，提交格式如：prepay_id=***
						nvpList.add(new NameValuePair("signType", StringUtil.null2Str("MD5")));								//签名算法，暂支持MD5
						//参数名ASCII码从小到大排序（字典序）； 
						String strAsciiSign = WeiXinPayUtil.getSignStrByAsciiRuleSort(nvpList);
						String stringSignTemp = strAsciiSign + "key=" + StringUtil.null2Str(weChatAppConfig.getSecretKey());
						String paySign = Md5Util.md5String(stringSignTemp).toUpperCase();
						nvpList.add(new NameValuePair("paySign", paySign));
						
						Map<String, String> resultMap = new HashMap<String, String> ();
						for(NameValuePair nameValuePair : nvpList){
							resultMap.put(nameValuePair.getName(), nameValuePair.getValue());
						}
						
						resultModel.setIsSucc(true);
						resultModel.setData(resultMap);
						resultModel.setPaymentBody(msgModel.getPaymentBody());
						return resultModel;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		resultModel.setIsSucc(false);
		resultModel.setMessage(StringUtil.null2Str(errorMsg));
		return resultModel;
	}
	
	/**
	 * 微信支付单信息
	 * @param weChatAppConfig
	 * @param outTradeNo
	 * @param openId
	 * @param notifyURL
	 * @param orderTotal
	 * @param body
	 * @param clientIp
	 * @return
	 */
	public static MsgModel<Map<String, String>> getWeixinAppPayInfo(WeChatAppConfig weChatAppConfig, String outTradeNo, String openId, String notifyURL, int orderTotal, String body, String clientIp){
		MsgModel<Map<String, String>> resultModel = new MsgModel<Map<String, String>> ();
		try {
			// 第一步获取统一下单
			MsgModel<Map<String, Object>> xmsgModel = WeiXinPayUtil.requestUnifiedOrder(weChatAppConfig, outTradeNo, openId, notifyURL, orderTotal, body, clientIp, null, null);
			if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())) {
				resultModel.setIsSucc(false);
				resultModel.setMessage(xmsgModel.getMessage());
				return resultModel;
			}
			Map<String,Object> objectMap = xmsgModel.getData();
			if(objectMap != null 
					&& objectMap.size() > 0
					&& objectMap.containsKey("prepay_id")){
				String nonceStr = StringUtil.nullToString(objectMap.get("nonce_str"));
				String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
				String prepaId = StringUtil.null2Str(objectMap.get("prepay_id"));
				List<NameValuePair> nvpList = new ArrayList<NameValuePair> ();
				nvpList.add(new NameValuePair("appid", StringUtil.null2Str(weChatAppConfig.getAppId())));			//公众号id
				nvpList.add(new NameValuePair("partnerid", StringUtil.null2Str(weChatAppConfig.getMchId())));		//商户ID
				nvpList.add(new NameValuePair("prepayid", StringUtil.null2Str(prepaId)));							//统一下单接口返回的prepay_id参数值，提交格式如：prepay_id=***
				nvpList.add(new NameValuePair("timestamp", StringUtil.null2Str(timeStamp)));						//时间戳
				nvpList.add(new NameValuePair("noncestr", StringUtil.null2Str(nonceStr))); 							//随机字符串，长度要求在32位以内。推荐随机数生成算法
				nvpList.add(new NameValuePair("package", StringUtil.null2Str("Sign=WXPay")));						//格式

				//参数名ASCII码从小到大排序（字典序）； 
				String strAsciiSign = WeiXinPayUtil.getSignStrByAsciiRuleSort(nvpList);
				String stringSignTemp = strAsciiSign + "key=" + StringUtil.null2Str(weChatAppConfig.getSecretKey());
				String paySign = Md5Util.md5String(stringSignTemp).toUpperCase();
				nvpList.add(new NameValuePair("sign", paySign));
				
				Map<String, String> resultMap = new HashMap<String, String> ();
				for(NameValuePair nameValuePair : nvpList){
					resultMap.put(nameValuePair.getName(), nameValuePair.getValue());
				}
				resultModel.setIsSucc(true);
				resultModel.setData(resultMap);
				resultModel.setPaymentBody(xmsgModel.getPaymentBody());
				return resultModel;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		resultModel.setIsSucc(false);
		resultModel.setMessage("微信支付单信息请求异常");
		return resultModel;
	}
	
	/**
	 * 微信支付单信息
	 * @param weChatAppConfig
	 * @param transactionId
	 * @param tradeNo
	 * @return
	 */
	public static MsgModel<String> getQueryPayInfo(WeChatAppConfig weChatAppConfig, String transactionId, String outTradeNo){
		MsgModel<String> msgModel = new MsgModel<String> ();
		try{
			// 第一步获取统一下单
			String nonceStr = UUID.randomUUID().toString().replace("-", "");
			List<NameValuePair> nvpList = new ArrayList<NameValuePair> ();
			nvpList.add(new NameValuePair("appid", StringUtil.null2Str(weChatAppConfig.getAppId())));			//公众号id
			nvpList.add(new NameValuePair("mch_id", StringUtil.null2Str(weChatAppConfig.getMchId())));			//商户号
			nvpList.add(new NameValuePair("nonce_str", StringUtil.null2Str(nonceStr))); 						//随机字符串，长度要求在32位以内。推荐随机数生成算法
			if(!StringUtil.isNull(transactionId)){
				nvpList.add(new NameValuePair("transaction_id", StringUtil.null2Str(transactionId)));			//微信订单号
			}else{
				nvpList.add(new NameValuePair("out_trade_no", StringUtil.null2Str(outTradeNo)));				//商家订单号
			}
			
			//参数名ASCII码从小到大排序（字典序）； 
			String strAsciiSign = WeiXinPayUtil.getSignStrByAsciiRuleSort(nvpList);
			String stringSignTemp = strAsciiSign + "key=" + StringUtil.null2Str(weChatAppConfig.getSecretKey());
			String paySign = Md5Util.md5String(stringSignTemp).toUpperCase();
			nvpList.add(new NameValuePair("sign", paySign));
			
			String xml = toXml(nvpList);
			String resultXML = HttpClientUtil.postXML("https://api.mch.weixin.qq.com/pay/orderquery", xml);
			log.debug("resultXML==" + resultXML);
			Map<String, Object> resultMap = XmlParseUtil.xmlCont2Map(resultXML);
			if(resultMap != null 
					&& resultMap.size() > 0
					&& StringUtil.compareObject(resultMap.get("return_code"), "SUCCESS")
					&& StringUtil.compareObject(resultMap.get("result_code"), "SUCCESS")
					&& StringUtil.compareObject(resultMap.get("trade_state"), "SUCCESS")
					&& !StringUtil.isNull(resultMap.get("transaction_id"))
					&& StringUtil.null2Str(resultMap.get("transaction_id")).length() > 20
					&& StringUtil.isNumber(resultMap.get("total_fee"))){
				msgModel.setMap(resultMap);
				msgModel.setIsSucc(true);
				msgModel.setTransactionId(StringUtil.null2Str(resultMap.get("transaction_id")));
				msgModel.setData(StringUtil.null2Str(resultMap.get("total_fee")));
				msgModel.setPaymentBody(StringUtil.null2Str(resultXML));
				return msgModel;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 微信预支付单信息
	 * @param weChatAppConfig
	 * @param outTradeNo
	 * @param openId
	 * @param notifyURL
	 * @param orderTotal
	 * @param body
	 * @param clientIp
	 * @param tradeType
	 * @return
	 */
	public static String getWeixinBeforePayInfo(WeChatAppConfig weChatAppConfig, String outTradeNo, String openId, String notifyURL, int orderTotal, String body, String clientIp, String tradeType, Long productId){
		List<NameValuePair> nvpList = new ArrayList<NameValuePair> ();
		nvpList.add(new NameValuePair("appid", StringUtil.null2Str(weChatAppConfig.getAppId())));				//公众账号ID
		nvpList.add(new NameValuePair("mch_id", StringUtil.null2Str(weChatAppConfig.getMchId())));				//商户号
		nvpList.add(new NameValuePair("nonce_str", UUID.randomUUID().toString().replace("-", ""))); 			//随机字符串，长度要求在32位以内。推荐随机数生成算法
		nvpList.add(new NameValuePair("body", StringUtil.null2Str(body)));										//商品描述
		nvpList.add(new NameValuePair("out_trade_no", StringUtil.null2Str(outTradeNo)));						//商户订单号
		nvpList.add(new NameValuePair("total_fee", StringUtil.null2Str(orderTotal)));							//标价金额
		nvpList.add(new NameValuePair("spbill_create_ip", StringUtil.null2Str(clientIp)));						//终端IP
		nvpList.add(new NameValuePair("notify_url", StringUtil.null2Str(notifyURL)));							//通知地址
		if(StringUtil.isNull(tradeType)){
			tradeType = StringUtil.null2Str(weChatAppConfig.getTradeType());
		}
		nvpList.add(new NameValuePair("trade_type", StringUtil.null2Str(tradeType))); 		//交易类型取值如下：JSAPI，NATIVE，APP等，说明详见参数规定
		//公众号支付时，openid必传
		if(tradeType.equals("JSAPI")){
			nvpList.add(new NameValuePair("device_info", StringUtil.null2Str("WEB")));
			nvpList.add(new NameValuePair("openid", StringUtil.null2Str(openId))); 		//交易类型取值如下：JSAPI，NATIVE，APP等，说明详见参数规定
		}else if(tradeType.equals("NATIVE")){
			//trade_type=NATIVE时（即扫码支付），此参数必传。此参数为二维码中包含的商品ID，商户自行定义。
			nvpList.add(new NameValuePair("product_id", StringUtil.null2Str(productId)));
		}

		//参数名ASCII码从小到大排序（字典序）；
		String strAsciiSign = WeiXinPayUtil.getSignStrByAsciiRuleSort(nvpList);
		String stringSignTemp = strAsciiSign + "key=" + StringUtil.null2Str(weChatAppConfig.getSecretKey());
		String sign = Md5Util.md5String(stringSignTemp).toUpperCase();
		nvpList.add(new NameValuePair("sign", sign));
		return WeiXinPayUtil.toXml(nvpList);
	}
	
	/**
	 * 微信订单回调通知
	 * 签名字符串
	 * @param notifyObjectMap
	 * @return
	 */
	public static String getNotifySignString(Map<String, Object> notifyObjectMap, String secretKey){
		List<NameValuePair> nvpList = new ArrayList<NameValuePair> ();
		if(notifyObjectMap != null && notifyObjectMap.size() > 0){
			for(Entry<String, Object> entry : notifyObjectMap.entrySet()){
				nvpList.add(new NameValuePair(entry.getKey(), StringUtil.null2Str(entry.getValue())));
			}
			String strAsciiSign = WeiXinPayUtil.getSignStrByAsciiRuleSort(nvpList);
			String stringSignTemp = strAsciiSign + "key=" + StringUtil.null2Str(secretKey);
			return Md5Util.md5String(stringSignTemp).toUpperCase();
		}
		return null;
	}

	/**
	 * 获取微信授权地址
	 * @param appId
	 * @param scope
	 * @param redirectURL
	 * @param requestURL
	 * @return
	 */
	public static String getAuthorizeURL(String appId, String scope, String redirectURL, String requestURL){
		StringBuffer weiXinOauthUrlBuffer = new StringBuffer ();
		try {
			weiXinOauthUrlBuffer.append("https://open.weixin.qq.com/connect/oauth2/authorize?");
			weiXinOauthUrlBuffer.append("appid=" + StringUtil.null2Str(appId));
			weiXinOauthUrlBuffer.append("&redirect_uri=" + StringUtil.null2Str(redirectURL));
			weiXinOauthUrlBuffer.append("&response_type=code");
			weiXinOauthUrlBuffer.append("&scope=" + StringUtil.null2Str(scope));
			weiXinOauthUrlBuffer.append("&state=" + StringUtil.null2Str(requestURL));
			weiXinOauthUrlBuffer.append("#wechat_redirect");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weiXinOauthUrlBuffer.toString();
	}

	/**
	 * 获取微信accessToken地址(获取用户信息需要单独获取accessToken)
	 * @param appId
	 * @param scope
	 * @param redirectURL
	 * @param requestURL
	 * @return
	 */
	public static Map<String, String> requestAccessToken(String appId, String appSecret, String code){
		Map<String, String> resultMap = new HashMap<String, String> ();
		try {
			StringBuffer accessTokenUrlBuffer = new StringBuffer ();
			accessTokenUrlBuffer.append("https://api.weixin.qq.com/sns/oauth2/access_token?");
			accessTokenUrlBuffer.append("appid=" + StringUtil.null2Str(appId));
			accessTokenUrlBuffer.append("&secret=" + StringUtil.null2Str(appSecret));
			accessTokenUrlBuffer.append("&code=" + StringUtil.null2Str(code));
			accessTokenUrlBuffer.append("&grant_type=authorization_code");

			log.debug("weChat accessTokenURL===>>> " + accessTokenUrlBuffer.toString());
			String resultObject = HttpClientUtil.get(accessTokenUrlBuffer.toString());
			log.debug(String.format("weChat accessTokenURL[%s]", resultObject));
			resultMap = StringUtil.jsonToHashMap(resultObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 获取微信用户信息地址
	 * @param appId
	 * @param scope
	 * @param redirectURL
	 * @param requestURL
	 * @return
	 */
	public static Map<String, String> requestWeChatUserInfo(String openId, String accessToken){
		Map<String, String> resultMap = new HashMap<String, String> ();
		try {
			String userInfoURL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
			userInfoURL = String.format(userInfoURL, accessToken, openId);
			log.debug("weChat userInfoURL===>>> " + userInfoURL);
			String weChatUserInfo = HttpClientUtil.get(userInfoURL);
			log.debug(String.format("weChat userInfoURL[%s]", weChatUserInfo));
			resultMap = StringUtil.jsonToHashMap(weChatUserInfo);
			//昵称特殊处理
			try {
				String filterName = StringUtil.filterName(resultMap.get("nickname"));
				resultMap.put("nickname", filterName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 统一下单
	 * @param weChatAppConfig
	 * @param outTradeNo
	 * @param openId
	 * @param notifyURL
	 * @param orderTotal
	 * @param body
	 * @param clientIp
	 * @return
	 */
	public static MsgModel<Map<String, Object>> requestUnifiedOrder(WeChatAppConfig weChatAppConfig, String outTradeNo, String openId, String notifyURL, int orderTotal, String body, String clientIp, String tradeType, Long productId){
		MsgModel<Map<String, Object>> msgModel = new MsgModel<Map<String, Object>> ();
		try {
			String unifiedOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
			String orderXML = WeiXinPayUtil.getWeixinBeforePayInfo(weChatAppConfig, outTradeNo, openId, notifyURL, orderTotal, body, clientIp, tradeType, productId);
			log.debug("weChat unifiedOrder===>>> " + orderXML);
			String resultXML = HttpClientUtil.postXML(unifiedOrderURL, orderXML);
			log.info("resultXML=====>>>"+resultXML);
			Map<String, Object> map = XmlParseUtil.xmlCont2Map(resultXML);
			msgModel.setIsSucc(true);
			msgModel.setData(map);
			msgModel.setPaymentBody(resultXML);
			return msgModel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		msgModel.setMessage("请求微信支付统一下单异常");
		return msgModel;
	}
	
	/**
	 * 参数名ASCII码从小到大排序（字典序）； 
	 */
	public static String getSignStrByAsciiRuleSort(List<NameValuePair> nvpList){
		StringBuffer strSignBuffer = new StringBuffer ();
		if(nvpList != null && nvpList.size() > 0){
			List<NameValuePair> mappingList = new ArrayList<NameValuePair> (nvpList);
			Collections.sort(mappingList, new Comparator<NameValuePair>(){
				@Override
				public int compare(NameValuePair o1, NameValuePair o2) {
					return StringUtil.null2Str(o1.getName()).compareTo(StringUtil.null2Str(o2.getName()));
				}
			});

			for(NameValuePair entry : mappingList){
				strSignBuffer.append(entry.getName() + "=" + entry.getValue() + "&");
			}
		}
		return strSignBuffer.toString();
	}
	
	/**
	 * 微信支付XML
	 * @param params
	 * @return
	 */
	private static String toXml(List<NameValuePair> nvpList) {
		StringBuilder xmlBuffer = new StringBuilder();
		xmlBuffer.append("<xml>");
		for(NameValuePair nvp : nvpList){
			xmlBuffer.append("<" + nvp.getName() + ">");
			xmlBuffer.append(nvp.getValue());
			xmlBuffer.append("</" + nvp.getName() + ">");
		}
		xmlBuffer.append("</xml>");
		return xmlBuffer.toString();
	}
	
	/**
	 * 微信支付单信息
	 * @param weChatAppConfigList
	 * @param dateList
	 * @return
	 */
	public static Map<String, Long> downloadbill(List<WeChatAppConfig> weChatAppConfigList, List<String> dateList){
		Map<String, Long> map = new HashMap<String, Long>();
		try{
			for(WeChatAppConfig weChatAppConfig : weChatAppConfigList){
				for(String data : dateList){
					// 第一步获取统一下单
					String nonceStr = UUID.randomUUID().toString().replace("-", "");
					List<NameValuePair> nvpList = new ArrayList<NameValuePair> ();
					nvpList.add(new NameValuePair("appid", StringUtil.null2Str(weChatAppConfig.getAppId())));			//公众号id
					nvpList.add(new NameValuePair("mch_id", StringUtil.null2Str(weChatAppConfig.getMchId())));			//商户号
					nvpList.add(new NameValuePair("nonce_str", StringUtil.null2Str(nonceStr))); 						//随机字符串，长度要求在32位以内。推荐随机数生成算法
					nvpList.add(new NameValuePair("bill_date", StringUtil.null2Str(data))); 
					nvpList.add(new NameValuePair("bill_type", StringUtil.null2Str("ALL"))); 

					//参数名ASCII码从小到大排序（字典序）； 
					String strAsciiSign = WeiXinPayUtil.getSignStrByAsciiRuleSort(nvpList);
					String stringSignTemp = strAsciiSign + "key=" + StringUtil.null2Str(weChatAppConfig.getSecretKey());
					String paySign = Md5Util.md5String(stringSignTemp).toUpperCase();
					nvpList.add(new NameValuePair("sign", paySign));
					
					String xml = toXml(nvpList);
					String resultXML = HttpClientUtil.postXML("https://api.mch.weixin.qq.com/pay/downloadbill", xml);
					log.debug("resultXML==" + resultXML);
					String str = resultXML;
			        String newStr = str.replaceAll(",", " "); // 去空格
			        String[] tempStr = newStr.split("`"); // 数据分组
			        String[] t = tempStr[0].split(" ");// 分组标题
			        int k = 1; // 纪录数组下标
			        int j = tempStr.length / t.length; // 计算循环次数
			        for (int i = 0; i < j; i++) {
			            for (int l = 0; l < t.length; l++) {
			            	if(l == 6){
			            		String string = tempStr[l + k];
			            		string = string.replace(" ", "");
			            		map.put(string, weChatAppConfig.getConfigId());
			            	}
			            }
			            k = k + t.length;
			        }
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		
		return map;
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
				System.out.println("1========" + StringUtil.null2Str(result));
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

	/***
    * 获取jsapiTicket
    * @return
    */
	public static String getJSApiTicket(String appId, String appSecret){ 
		//获取token
		String acess_token = getWeiXinToken(appId, appSecret);
		String urlStr = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+acess_token+"&type=jsapi";  
		//获取jsapiTicket
		return HttpClientUtil.get(urlStr);
	}  
	
	/**
	 * 解析json获取ticket
	 * @param jsonStr
	 * @return
	 */
	public static String getJSApiTicketByJson(String jsonStr){
		try {
			JSONObject json = new JSONObject(jsonStr);
			if (json != null && json.has("ticket")){
				return json.getString("ticket");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 解析json获取ticket
	 * @param weChatAppConfig
	 * @return
	 */
	public static String getJSApiTicket(WeChatAppConfig weChatAppConfig){
		String result = getJSApiTicket(weChatAppConfig.getAppId(), weChatAppConfig.getAppSecret());
		String ticketString = getJSApiTicketByJson(result);
		//如果ticket为空则刷新token
		if (StringUtil.isNull(ticketString)){
			Constants.WEIXIN_TOKEN_MAP.clear();
			result = getJSApiTicket(weChatAppConfig.getAppId(), weChatAppConfig.getAppSecret());
			ticketString = getJSApiTicketByJson(result);
		}
		return ticketString;
		
	}
	
	/**
	 * 微信签名
	 * @param jsapi_ticket
	 * @param url
	 * @return
	 */
	public static Map<String, String> sign(String jsApiTicket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = UUID.randomUUID().toString();
        String timestamp = StringUtil.null2Str(System.currentTimeMillis()/1000);
        String string1;
        String signature = "";
 
        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsApiTicket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
 
        try{
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }catch (Exception e){
            e.printStackTrace();
        }
 
        ret.put("url", url);
        ret.put("jsapi_ticket", jsApiTicket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        return ret;
    }
	
	/**
	 * 加密
	 * @param hash
	 * @return
	 */
	private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
	
	
	/**
	 * 获取微信授权地址
	 * @param appId
	 * @param scope
	 * @param redirectURL
	 * @param requestURL
	 * @return
	 */
	public static void getQrCode(String appId, String scope, String redirectURL, String requestURL){
		StringBuffer weiXinOauthUrlBuffer = new StringBuffer ();
		try {
			weiXinOauthUrlBuffer.append("https://open.weixin.qq.com/connect/qrconnect?");
			weiXinOauthUrlBuffer.append("appid=" + StringUtil.null2Str(appId));
			weiXinOauthUrlBuffer.append("&redirect_uri=" + StringUtil.null2Str(redirectURL));
			weiXinOauthUrlBuffer.append("&response_type=code");
			weiXinOauthUrlBuffer.append("&scope=" + StringUtil.null2Str(scope));
			weiXinOauthUrlBuffer.append("&state=" + StringUtil.null2Str(requestURL));
			weiXinOauthUrlBuffer.append("#wechat_redirect");
			HttpClientUtil.get(weiXinOauthUrlBuffer.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 微信退款
	 * @param weChatAppConfig
	 * @param transactionId
	 * @param tradeNo
	 * @return
	 */
	public static MsgModel<Double> getQueryRefundInfo(WeChatAppConfig weChatAppConfig, String transactionId, String outTradeNo,Double refundFee,Double totalFee,String refundNumber){
		MsgModel<Double> msgModel = new MsgModel<Double> ();
		try{
	        StringBuilder sb2 = new StringBuilder();
	        //微信签名需要的参数
	        String nonceStr = UUID.randomUUID().toString().replace("-", "");
	        List<NameValuePair> nvpList = new ArrayList<NameValuePair> ();
			nvpList.add(new NameValuePair("appid", StringUtil.null2Str(weChatAppConfig.getAppId())));			//公众号id
			nvpList.add(new NameValuePair("mch_id", StringUtil.null2Str(weChatAppConfig.getMchId())));			//商户号
			nvpList.add(new NameValuePair("nonce_str", StringUtil.null2Str(nonceStr))); 						//随机字符串，长度要求在32位以内。推荐随机数生成算法
			nvpList.add(new NameValuePair("out_refund_no", StringUtil.null2Str(refundNumber)));                 //退款流水号
			nvpList.add(new NameValuePair("op_user_id", StringUtil.null2Str(weChatAppConfig.getMchId())));
			nvpList.add(new NameValuePair("refund_fee", StringUtil.null2Str(orderAmountToBranch(refundFee)))); //退款金额
			nvpList.add(new NameValuePair("total_fee", StringUtil.null2Str(orderAmountToBranch(totalFee)))); //总金额
			if(!StringUtil.isNull(transactionId)){
				nvpList.add(new NameValuePair("transaction_id", StringUtil.null2Str(transactionId)));			//微信订单号
			}else{
				nvpList.add(new NameValuePair("out_trade_no", StringUtil.null2Str(outTradeNo)));					//商户订单号
			}
	        
			//参数名ASCII码从小到大排序（字典序）； 
			String strAsciiSign = WeiXinPayUtil.getSignStrByAsciiRuleSort(nvpList);
			String stringSignTemp = strAsciiSign + "key=" + StringUtil.null2Str(weChatAppConfig.getSecretKey());
			String sign = Md5Util.md5String(stringSignTemp).toUpperCase();
			nvpList.add(new NameValuePair("sign", sign));
			
			//生成xml,微信要求的xml形式
			String xmlString = toXml(nvpList);
	        
	         //JAVA使用证书文件
	        //指定读取证书格式为PKCS12
	        KeyStore keyStore = KeyStore.getInstance("PKCS12");
	        String certUrl = StringUtil.nullToString((Constants.conf.getProperty("chunruo.refund.cert")));

	        //读取本机存放的PKCS12证书文件
	        FileInputStream instream = new FileInputStream(new File(certUrl));
	        try {
	            //指定PKCS12的密码(商户ID)
	            keyStore.load(instream, weChatAppConfig.getMchId().toCharArray());
	        } finally {
	            instream.close();
	        }
	        
	        //ssl双向验证发送http请求报文
	        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore,StringUtil.null2Str(weChatAppConfig.getMchId()).toCharArray()).build();
	        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	        HttpPost httppost = new HttpPost("https://api.mch.weixin.qq.com/secapi/pay/refund");
	        StringEntity se = new StringEntity(xmlString, "UTF-8");
	        httppost.setEntity(se);
	        
	        //定义响应实例对象
	        CloseableHttpResponse responseEntry =  httpclient.execute(httppost);//发送请求
	        String xmlStr2 = null;//读入响应流中字符串的引用
	        HttpEntity entity = responseEntry.getEntity();//获得响应实例对象
	        if (entity != null) {//读取响应流的内容
	            BufferedReader bufferedReader = null;
	            bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
	            while ((xmlStr2 = bufferedReader.readLine()) != null) {
	                sb2.append(xmlStr2);
	            }
	        }
	        
	        Map<String, Object> resultMap = XmlParseUtil.xmlCont2Map(sb2.toString());

	        System.out.println("微信退款接口========="+sb2.toString());
	        //return_code为微信返回的状态码，SUCCESS表示申请退款成功，return_msg 如非空，为错误原因 签名失败 参数格式校验错误
	        if(resultMap != null && resultMap.size() > 0) {
	        	if(resultMap.containsKey("return_code")
						&& StringUtil.compareObject("SUCCESS", StringUtil.null2Str(resultMap.get("return_code")))
						&& StringUtil.isNumber(resultMap.get("total_fee"))
						&& StringUtil.isNumber(resultMap.get("refund_fee"))) {
		            log.info("****************退款申请成功！**********************");
		        	msgModel.setIsSucc(true);
					msgModel.setData(orderAmountToYuan(StringUtil.nullToInteger((resultMap.get("refund_fee")))));
					msgModel.setMessage("微信退款成功");
					msgModel.setMap(resultMap);
					return msgModel;
	        	}else {
	        		//退款失败原因
	        		msgModel.setIsSucc(false);
	        		msgModel.setMessage(StringUtil.null2Str(resultMap.get("err_code_des")));
	        		return msgModel;
	        	}
	        }
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
		msgModel.setIsSucc(false);
		msgModel.setMessage("微信退款失败");
		return msgModel;
	}
	
	
	/**
	 * 退款查询
	 * @param weChatAppConfig
	 * @param tradeNo
	 * @param refundNumber
	 * @return
	 */
	public static MsgModel<Boolean> checkRefundInfo(WeChatAppConfig weChatAppConfig,String transactionId,String refundNumber,String outTradeNo){
		MsgModel<Boolean> msgModel = new MsgModel<Boolean> ();
		try {

			StringBuilder sb2 = new StringBuilder();
			// 微信签名需要的参数
			String nonceStr = UUID.randomUUID().toString().replace("-", "");
			List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
			nvpList.add(new NameValuePair("appid", StringUtil.null2Str(weChatAppConfig.getAppId()))); // 公众号id
			nvpList.add(new NameValuePair("mch_id", StringUtil.null2Str(weChatAppConfig.getMchId()))); // 商户号
			nvpList.add(new NameValuePair("nonce_str", StringUtil.null2Str(nonceStr)));                // 随机字符串，长度要求在32位以内。推荐随机数生成算法
			nvpList.add(new NameValuePair("transaction_id", StringUtil.null2Str(transactionId)));      // 微信订单号
			nvpList.add(new NameValuePair("out_refund_no", StringUtil.null2Str(refundNumber)));        // 商户退款单号
			nvpList.add(new NameValuePair("out_trade_no", StringUtil.null2Str(outTradeNo)));		       //商户订单号

			// 参数名ASCII码从小到大排序（字典序）；
			String strAsciiSign = WeiXinPayUtil.getSignStrByAsciiRuleSort(nvpList);
			String stringSignTemp = strAsciiSign + "key=" + StringUtil.null2Str(weChatAppConfig.getSecretKey());
			String sign = Md5Util.md5String(stringSignTemp).toUpperCase();
			nvpList.add(new NameValuePair("sign", sign));

			// 生成xml,微信要求的xml形式
			String xmlString = toXml(nvpList);

			// JAVA使用证书文件
			// 指定读取证书格式为PKCS12
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
	        String certUrl = StringUtil.nullToString((Constants.conf.getProperty("chunruo.refund.cert")));

			// 读取本机存放的PKCS12证书文件
			FileInputStream instream = new FileInputStream(new File(certUrl));
			try {
				// 指定PKCS12的密码(商户ID)
				keyStore.load(instream, weChatAppConfig.getMchId().toCharArray());
			} finally {
				instream.close();
			}
			// ssl双向验证发送http请求报文
			SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, StringUtil.null2Str(weChatAppConfig.getMchId()).toCharArray()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" },
					null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			HttpPost httppost = new HttpPost("https://api.mch.weixin.qq.com/pay/refundquery");
			StringEntity se = new StringEntity(xmlString, "UTF-8");
			httppost.setEntity(se);

			// 定义响应实例对象
			CloseableHttpResponse responseEntry = httpclient.execute(httppost);// 发送请求
			String xmlStr2 = null;// 读入响应流中字符串的引用
			HttpEntity entity = responseEntry.getEntity();// 获得响应实例对象
			if (entity != null) {// 读取响应流的内容
				BufferedReader bufferedReader = null;
				bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
				while ((xmlStr2 = bufferedReader.readLine()) != null) {
					sb2.append(xmlStr2);
				}
			}

			Map<String, Object> resultMap = XmlParseUtil.xmlCont2Map(sb2.toString());
			// logger.info("申请退款接口返回的结果集======>" + resultMap);

			log.info("微信退款查询接口==========="+sb2.toString());
			
			if(resultMap != null
					 && resultMap.size() > 0
					 && resultMap.containsKey("return_code")
					 && StringUtil.compareObject("SUCCESS", StringUtil.null2Str(resultMap.get("return_code")))
					 && StringUtil.compareObject(transactionId, StringUtil.null2Str(resultMap.get("transaction_id")))
					 && StringUtil.compareObject(refundNumber, StringUtil.null2Str(resultMap.get("out_refund_no_0")))
					 && StringUtil.compareObject("SUCCESS", StringUtil.null2Str(resultMap.get("refund_status_0")))){
				msgModel.setIsSucc(true);
				msgModel.setData(true);
				msgModel.setMessage("已退款成功");
				msgModel.setMap(resultMap);
				return msgModel;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		msgModel.setData(false);
		msgModel.setMessage("微信查询退款失败");
		return msgModel;
	}
	
	/**
	 * 微信小程序code授权验证
	 * @param code
	 * @param nonceStr
	 * @param request
	 * @return
	 */
	public static MsgModel<String> validateWechatCode(String wechatCode, Long wechatConfigId){
		MsgModel<String> msgModel = new MsgModel<String>();
		try {
			StringBuffer weiXinOauthUrlBuffer = new StringBuffer();
			WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(wechatConfigId);
			if (weChatAppConfig != null && weChatAppConfig.getConfigId() != null) {
				String appId = StringUtil.null2Str(weChatAppConfig.getAppId());
				String appSecret = StringUtil.null2Str(weChatAppConfig.getAppSecret());
				
				weiXinOauthUrlBuffer.append("https://api.weixin.qq.com/sns/jscode2session?");
				weiXinOauthUrlBuffer.append("appid=" + StringUtil.null2Str(appId));
				weiXinOauthUrlBuffer.append("&secret=" + StringUtil.null2Str(appSecret));
				weiXinOauthUrlBuffer.append("&js_code=" + StringUtil.null2Str(wechatCode));
				weiXinOauthUrlBuffer.append("&grant_type=authorization_code");
				String result = HttpClientUtil.get(weiXinOauthUrlBuffer.toString());
				log.debug("wechatCode========" + StringUtil.null2Str(result));
				if(!StringUtil.isNull(result)){
					JSONObject obj = new JSONObject(result);
					if (obj != null 
							&& obj.has("session_key") 
							&& obj.has("openid")) {
						msgModel.setIsSucc(true);
						msgModel.setTransactionId(StringUtil.null2Str(obj.get("openid")));
						msgModel.setData(StringUtil.null2Str(obj.getString("session_key")));
						return msgModel;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 订单金额转换成元
	 * @param orderAmount
	 * @return
	 */
	public static double orderAmountToYuan(int orderAmount){
		try{
			BigDecimal bigDecimal1 = new BigDecimal(Double.toString(0.01));
			BigDecimal bigDecimal2 = new BigDecimal(Double.toString(orderAmount));
			return bigDecimal1.multiply(bigDecimal2).doubleValue();
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
}
