package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.chunruo.core.Constants;
import com.chunruo.core.util.HttpUtils;
import com.chunruo.core.util.JsonParseUtil;
import com.chunruo.core.util.JsonUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.vo.AlipayRefund;

/**
 * 支付宝支付
 * @author chunruo
 *
 */
public class AliPayUtil {
	public final static transient Log log = LogFactory.getLog(AliPayUtil.class);
	public static final String ALI_PAY_NEW_WAP_TYPE = "QUICK_WAP_WAY";			//新手机网站支付
	public static final String ALI_PAY_CLT_APP_TYPE = "QUICK_MSECURITY_PAY";	//客户端app支付
	
	/**
	 * 支付宝电脑网站支付
	 * @param outTradeNo
	 * @param orderTotal
	 * @param body
	 * @param notifyURL
	 * @param returnURL
	 * @return
	 */
	public static MsgModel<String> getAliPayPcInfo(String outTradeNo, String orderTotal, String body, String notifyURL){
		MsgModel<String> msgModel = new MsgModel<>();
		try {
			Map<String, String> paramMap = new HashMap<String, String> ();
			paramMap.put("subject", body);			// (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
			paramMap.put("total_amount", orderTotal);	// 订单总金额
			paramMap.put("out_trade_no", outTradeNo);	// 商户订单号
			paramMap.put("timeout_express", "120m");				// 支付超时，定义为120分钟
			paramMap.put("product_code", "FAST_INSTANT_TRADE_PAY");
			paramMap.put("goods_type", "1");
			paramMap.put("qr_pay_mode", "4");
			paramMap.put("qrcode_width", "200");
			paramMap.put("integration_type", "PCWEB");
			
			String appId = Constants.conf.getProperty("alipay.app.appId");
			String appPrivateKey = Constants.conf.getProperty("alipay.app.private.key");
			String aliPayPublicKey = Constants.conf.getProperty("alipay.app.public.key");
			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId , appPrivateKey, "json", "utf-8", aliPayPublicKey, "RSA2");
			AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
			request.setBizContent(StringUtil.mapStrToJson(paramMap));
			request.setNotifyUrl(notifyURL);
			
			AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
			System.out.println(response.getBody());
			if(response.isSuccess()){
				msgModel.setIsSucc(true);
				msgModel.setData(response.getBody());
				msgModel.setPaymentBody(StringUtil.mapStrToJson(paramMap));
				return msgModel;
			} else {
				msgModel.setIsSucc(false);
				msgModel.setMessage("调用失败" + response.getMsg() + "--" + response.getSubMsg());
				return msgModel;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		msgModel.setMessage("查询订单支付异常");
		return msgModel;
	}

	/**
	 * 支付宝支付在微信浏览器使用WAP网页支付
	 * @param outTradeNo
	 * @param orderTotal
	 * @param body
	 * @param notifyURL
	 * @param returnURL
	 * @return
	 */
	public static String getAliPayWapInfo(String outTradeNo, String orderTotal, String body, String notifyURL, String returnURL){
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", AlipayConfig.service);
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("seller_id", AlipayConfig.seller_id);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("payment_type", AlipayConfig.payment_type);
		sParaTemp.put("notify_url", StringUtil.null2Str(notifyURL));
		sParaTemp.put("return_url", StringUtil.null2Str(returnURL));
		sParaTemp.put("anti_phishing_key", AlipayConfig.anti_phishing_key);
		sParaTemp.put("exter_invoke_ip", AlipayConfig.exter_invoke_ip);
		sParaTemp.put("out_trade_no", StringUtil.null2Str(outTradeNo));
		sParaTemp.put("subject",  StringUtil.null2Str(body));
		sParaTemp.put("total_fee", StringUtil.null2Str(orderTotal));
		sParaTemp.put("body",  StringUtil.null2Str(body));
		//其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.O9yorI&treeId=62&articleId=103740&docType=1
		//如sParaTemp.put("参数名","参数值");

		//建立请求
		return AlipaySubmit.buildRequest(sParaTemp,"get","确认");
	}

	/**
	 * 支付宝APP客户端支付
	 * @param outTradeNo
	 * @param orderTotal
	 * @param body
	 * @param notifyURL
	 * @param returnURL
	 * @return
	 */
	public static String getAliPayAppInfo(String aliPayType, String outTradeNo, String orderTotal, String body, String notifyURL, String returnURL){
		//JAVA服务端SDK生成APP支付订单信息示例
		String appId = Constants.conf.getProperty("alipay.app.appId");
		String appPrivateKey = Constants.conf.getProperty("alipay.app.private.key");
		String aliPayPublicKey = Constants.conf.getProperty("alipay.app.public.key");
		
		//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody(StringUtil.null2Str(body));
		model.setSubject(StringUtil.null2Str(body));
		model.setOutTradeNo(outTradeNo);
		model.setTimeoutExpress("30m");
		model.setTotalAmount(StringUtil.null2Str(orderTotal));
		model.setProductCode(StringUtil.null2Str(aliPayType));
		
		try {
			//实例化客户端
			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, appPrivateKey, "json", AlipayConfig.input_charset, aliPayPublicKey, "RSA2");
			//这里和普通的接口调用不同，使用的是sdkExecute
			if(StringUtil.compareObject(AliPayUtil.ALI_PAY_NEW_WAP_TYPE, aliPayType)){
				AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
				alipayRequest.setBizModel(model);
				alipayRequest.setNotifyUrl(StringUtil.null2Str(notifyURL));
				alipayRequest.setReturnUrl(StringUtil.null2Str(returnURL));
				
				//就是orderString 可以直接给客户端请求，无需再做处理。
				return alipayClient.pageExecute(alipayRequest).getBody();
			}else{
				AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
				alipayRequest.setBizModel(model);
				alipayRequest.setNotifyUrl(StringUtil.null2Str(notifyURL));
				alipayRequest.setReturnUrl(StringUtil.null2Str(returnURL));
				
				//就是orderString 可以直接给客户端请求，无需再做处理。
				AlipayTradeAppPayResponse alipayResponse = alipayClient.sdkExecute(alipayRequest);
				return alipayResponse.getBody();
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 签名校验
	 * @param params
	 * @return
	 */
	public static boolean verify(Map<String, String> params){
		String aliPayPublicKey = Constants.conf.getProperty("alipay.app.public.key");
		String signType = StringUtil.null2Str(params.get("sign_type"));
		boolean signVerified = false;
		try {
			if(signType.equals("MD5")){
				signVerified = AlipayNotify.verify(params);
			}else{
				signVerified = AlipaySignature.rsaCheckV1(params, aliPayPublicKey, AlipayConfig.input_charset, signType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return signVerified;
	}

	/**
	 * 支付宝APP客户端支付
	 * @param outTradeNo
	 * @param orderTotal
	 * @param body
	 * @param notifyURL
	 * @param returnURL
	 * @return
	 */
	public static MsgModel<String> getQueryAliPayInfo(String outTradeNo, String tradeNo){
		MsgModel<String> msgModel = new MsgModel<String> ();
		try{
			//JAVA服务端SDK生成APP支付订单信息示例
			String appId = Constants.conf.getProperty("alipay.app.appId");
			String appPrivateKey = Constants.conf.getProperty("alipay.app.private.key");
			String aliPayPublicKey = Constants.conf.getProperty("alipay.app.public.key");

			Map<String, String> paramMap = new HashMap<String, String> ();
			paramMap.put("out_trade_no", outTradeNo);
			paramMap.put("trade_no", tradeNo);

			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, appPrivateKey, "json","GBK", aliPayPublicKey,"RSA2");
			AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
			request.setBizContent(StringUtil.mapStrToJson(paramMap));
			AlipayTradeQueryResponse response = alipayClient.execute(request);
			String body = response.getBody();
			try {
				List<String> successStatusList = new ArrayList<String> ();
				successStatusList.add("TRADE_SUCCESS");		//交易支付成功
				successStatusList.add("TRADE_FINISHED");	//交易结束，不可退款

				Map<String, Object> json2Map = JsonParseUtil.json2Map(body);
				String result = StringUtil.null2Str(json2Map.get("alipay_trade_query_response"));
				Map<String, Object> map = JsonParseUtil.json2Map(result);
				if(map != null
						&& map.containsKey("trade_status")
						&& successStatusList.contains(StringUtil.null2Str(map.get("trade_status")).toUpperCase())
						&& !StringUtil.isNull(response.getTotalAmount())){
					msgModel.setIsSucc(true);
					msgModel.setTransactionId(response.getTradeNo());
					msgModel.setData(StringUtil.nullToDoubleFormatStr(response.getTotalAmount()));
					msgModel.setPaymentBody(StringUtil.null2Str(response.getBody()));
					msgModel.setMap(map);
					return msgModel;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 支付宝退款
	 * @param outTradeNo
	 * @param orderTotal
	 * @param body
	 * @param notifyURL
	 * @param returnURL
	 * @return
	 */
	public static MsgModel<Double> getAliPayRefundInfo(String outTradeNo, String tradeNo, Double refund_fee, String outRequestNo){
		MsgModel<Double> msgModel = new MsgModel<Double> ();
		//JAVA服务端SDK生成APP支付订单信息示例
		try {
		String appId = Constants.conf.getProperty("alipay.app.appId");
		String appPrivateKey = Constants.conf.getProperty("alipay.app.private.key");
		String aliPayPublicKey = Constants.conf.getProperty("alipay.app.public.key");
	    AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, appPrivateKey, "json", "GBK", aliPayPublicKey, "RSA2"); //获得初始化的AlipayClient
		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();//创建API对应的request类
		AlipayRefund alipayRefund= new AlipayRefund();
		alipayRefund.setOut_trade_no(outTradeNo);      //这个是商户的订单号
		alipayRefund.setTrade_no(tradeNo);             //这个是支付宝的订单号
		alipayRefund.setRefund_amount(refund_fee);   //退款金额
		alipayRefund.setOut_request_no(outRequestNo);  //本次退款请求流水号
		request.setBizContent(JsonUtil.bean2json(alipayRefund));
		AlipayTradeRefundResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
		String body = response.getBody();
		try {
			
			Map<String, Object> json2Map = JsonParseUtil.json2Map(body);
			String result = StringUtil.null2Str(json2Map.get("alipay_trade_refund_response"));
			Map<String, Object> resultMap = JsonParseUtil.json2Map(result);
			log.info("支付宝退款接口=========="+body);
			if(resultMap != null && resultMap.size() > 0) {
				if(StringUtil.compareObject("10000", StringUtil.null2Str(resultMap.get("code")))
						&& StringUtil.compareObject("Success", StringUtil.null2Str(resultMap.get("msg")))
						&& StringUtil.isNumber(resultMap.get("refund_fee"))) {
					String refundFee = StringUtil.null2Str(resultMap.get("refund_fee")); //该笔交易已退款的总金额
					msgModel.setIsSucc(true);
					msgModel.setData(StringUtil.nullToDouble((refundFee)));
					msgModel.setMap(resultMap);
					msgModel.setMessage("支付宝退款成功");
					return msgModel;
				}else if(StringUtil.compareObject("40004", StringUtil.null2Str(resultMap.get("code")))
						&& StringUtil.compareObject("Business Failed", StringUtil.null2Str(resultMap.get("msg")))){
					String subCode = StringUtil.null2Str(resultMap.get("sub_code"));
					String subMsg = StringUtil.null2Str(resultMap.get("sub_msg"));
					System.out.println(String.format("退款失败原因:subCode===%s+subMsg===%s",subCode,subMsg));
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("退款失败,原因：%s", subMsg));
					return msgModel;
				}
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		}catch(AlipayApiException e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("支付宝退款失败");
		return msgModel;
	}
	
	/**
	 * 退款查询
	 * @param outTradeNo
	 * @param tradeNo
	 * @param outRequestNo
	 * @return
	 */
	public static MsgModel<Boolean> checkQueryInfo(String outTradeNo,String tradeNo,String outRequestNo,Double refund_fee){
		MsgModel<Boolean> msgModel = new MsgModel<Boolean> ();
		try {
			String appId = Constants.conf.getProperty("alipay.app.appId");
			String appPrivateKey = Constants.conf.getProperty("alipay.app.private.key");
			String aliPayPublicKey = Constants.conf.getProperty("alipay.app.public.key");
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, appPrivateKey, "json", "GBK", aliPayPublicKey, "RSA2"); //获得初始化的AlipayClient
			AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
			AlipayRefund alipayRefund= new AlipayRefund();
			alipayRefund.setOut_trade_no(outTradeNo);      //这个是商户的订单号
			alipayRefund.setTrade_no(tradeNo);             //这个是支付宝的订单号
			alipayRefund.setOut_request_no(outRequestNo);  //本次退款请求流水号
			request.setBizContent(JsonUtil.bean2json(alipayRefund));
			AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
			System.out.println("response++++++"+response);
			String body = response.getBody();
			System.out.println("查询退款接口==========="+body);
			
			try {
				Map<String, Object> json2Map = JsonParseUtil.json2Map(body);
				String result = StringUtil.null2Str(json2Map.get("alipay_trade_fastpay_refund_query_response"));
				Map<String, Object> resultMap = JsonParseUtil.json2Map(result);
				if(resultMap != null 
						&& resultMap.size() > 0
						&& StringUtil.compareObject("10000", StringUtil.null2Str(resultMap.get("code")))
						&& StringUtil.compareObject("Success", StringUtil.null2Str(resultMap.get("msg")))
						&& resultMap.containsKey("out_request_no")
						&& resultMap.containsKey("trade_no")
						&& resultMap.containsKey("out_trade_no")) {
					msgModel.setIsSucc(true);
					msgModel.setData(true);
					msgModel.setMessage("已退款成功");
					return msgModel;
				}
			}catch (JSONException e) {
				e.printStackTrace();
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setData(false);
		return msgModel;
	}
	
	/**
	 * 汇率转化 美元 -> 人名币
	 * @param amount
	 * @return
	 */
	public static MsgModel<Double> exchangeRate(){
		MsgModel<Double> msgModel = new MsgModel<Double>();
		try {
		    if(StringUtil.nullToDouble(Constants.REAL_RATE).compareTo(5.5D) <= 0) {
		    	    String host = "https://jisuhuilv.market.alicloudapi.com";
				    String path = "/exchange/convert";
				    String method = "GET";
				    String appcode = "eeee8e66befd44838b790bfee0becb82";
				    Map<String, String> headers = new HashMap<String, String>();
				    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
				    headers.put("Authorization", "APPCODE " + appcode);
				    Map<String, String> querys = new HashMap<String, String>();
				    querys.put("amount", "1");
				    querys.put("from", "USD");
				    querys.put("to", "CNY");


				    try {
				    	HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
				    	//获取response的body
				    	String body = EntityUtils.toString(response.getEntity());
				    	System.out.println("汇率转化:"+body);
				    	JSONObject jsonObject = JSON.parseObject(body);
				    	if(jsonObject != null && !jsonObject.isEmpty()
				    			&& StringUtil.compareObject(jsonObject.getInteger("status"), 0)
				    			&& StringUtil.compareObject(jsonObject.getString("msg"), "ok")) {
				    		JSONObject result = jsonObject.getJSONObject("result");
				    		if(result != null && !result.isEmpty()) {
				    		    Constants.REAL_RATE = StringUtil.nullToDouble(result.getString("rate"));
				    		}
				    	}
				    } catch (Exception e) {
				    	e.printStackTrace();
				    }
		    }
		    msgModel.setIsSucc(true);
			msgModel.setData(Constants.REAL_RATE);
		    return msgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("汇率计算错误");
		return msgModel;
	}
	
}
