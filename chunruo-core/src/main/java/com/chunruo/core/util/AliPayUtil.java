package com.chunruo.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.chunruo.core.Constants;
import com.chunruo.core.vo.AlipayRefund;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.HttpUtils;
import com.chunruo.core.util.JsonParseUtil;
import com.chunruo.core.util.JsonUtil;
import com.chunruo.core.util.StringUtil;


/**
 * 支付宝支付
 * @author chunruo
 *
 */
public class AliPayUtil {
	public static final String ALI_PAY_NEW_WAP_TYPE = "QUICK_WAP_PAY";			//新手机网站支付
	public static final String ALI_PAY_CLT_APP_TYPE = "QUICK_MSECURITY_PAY";	//客户端app支付
	
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
		    	
		//实例化客户端
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, appPrivateKey, "json", AlipayConfig.input_charset, aliPayPublicKey, "RSA2");
		//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
		//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody(StringUtil.null2Str(body));
		model.setSubject(StringUtil.null2Str(body));
		model.setOutTradeNo(outTradeNo);
		model.setTimeoutExpress("30m");
		model.setTotalAmount(StringUtil.null2Str(orderTotal));
		model.setProductCode(StringUtil.null2Str(aliPayType));
		alipayRequest.setBizModel(model);
		alipayRequest.setNotifyUrl(StringUtil.null2Str(notifyURL));
		alipayRequest.setReturnUrl(StringUtil.null2Str(returnURL));
		try {
			//这里和普通的接口调用不同，使用的是sdkExecute
			if(StringUtil.compareObject(AliPayUtil.ALI_PAY_NEW_WAP_TYPE, aliPayType)){
				String content = alipayClient.pageExecute(alipayRequest).getBody();
				return content;
			}else{
				AlipayTradeAppPayResponse alipayResponse = alipayClient.sdkExecute(alipayRequest);
				//就是orderString 可以直接给客户端请求，无需再做处理。
				return alipayResponse.getBody();
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
			
			
//			String appId ="2017021505686225";
//			String appPrivateKey = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDFQXRzS+Ud3mbZWxdeEXf+hgx11EgKw6JeKMjdxcjIZcMPwheo7MfCE/cXEbdYvQuhoMcEXq44uFdYPJe0A0/837D0hAvkckwG1azYS9WMsKPKstDYai7veaoVWHcJgauIIPnW7EDihUAYksqvH/V5dCQAdqMNTUDDwWbBeWWf63EJwN5+r/8t8uGZTrEM0nCmy98WXIHpeA7nk5e9DQ893Sf5Uz+j7K7I7rLjKr6my6CwKYgVzbHphnGG2q4QjkvCXY6M4dbX4AstiwgIHDadzTWC0245E+9Uq5pWlMsROe9P6fxucFJHxNusqM5GBxr/gHQpC9Sfr6yvYKkZMIAvAgMBAAECggEBAK/KHmJl36DXw+aupEuD2+ErS01yymU1ZazuQdxKWB4neke/3GAB7B/MtSaM9k2R5By9cL83NaJ8vfJ3xWZ6kT01iLjQONz080YgBLKaGo0QGlgZNb+4GW33ihkNxr/lHJ3rbVwZHdYBlFaR8ylQnO4JD9CN6c9/6ljKMa34ZxW1JokpM9nBDCcUcojPnn/jO8QLSjOKzZBbHlXzrhC0XXeIAyrUEmM5+uJizTfOS9GaLr1RnncKXrDRwbMrnw8qLCOpEBIEBEIcWDfHc6qVrRXGIND+OVTQaj1yil6mC0E+sEtPLF5VMv4d21suGdNfEzFctZUe7SWrx671GgvXYYkCgYEA8I+E87DeqOJPdIeqhEsSUok1/KRd6KXPUwzs99wf+VvcqFVMvNhqrJayeodbA+hZC/qxTx+t0ceVnALOvtEFZOt5AvCxONqmRyJ3uwJWM+NPyzDBwonMxWcZ4KvlpwSO8DpGWgq4NabR3rkfHnB7cLnIlonxcZA2fffdN7RhP5UCgYEA0epsQCPyodbQqGhId+wATDrXz5yJp6G15dp+NiMOyFy7w0YkExaQXO9zMz/jZyF2uLSt180CzZLGcSVCQR9mjuDdFhr1xYv/HOltV7wiax7wPlJWEr8SW/rvPciuTLcl2bNJhrUd6g76AOVh7Uwp7kVrrEs6g7ipCVwvsqEGH7MCgYEAravPnmdouKB6IHT5Y/0Ww0OQGE9KBPYAEcNZKTBgXRQ238iFWCkeCm/ZdqBV0yJhd0aLz8XdJYBjwHg+boBU1qExgi/molcoiF6X9gkf1uIa72TJ3frCPRnMEunHKKcf0ssfk039464GAjfAAafPVniqGhSInSYyUQsrSFYPh2kCgYEA0U3O9f1h9mC82M5yQ7C7c0bQ3J+W2eByAGflpJE8MifUyBZt7eJ3u1K+KQGw+qnxOKZfGGfAPb31w+eEsm5e0mP/zgS/vmOi0e8mKW7bWrgUj++FB1ghNDl2xsIMmU2cnf8Ydwqscy7PtIkDJZrlzEDH5Zl1FNXRJhYXMSNBeeUCgYEAiNvFnzdL3w/NvUcNOAPRfo/pnVksGTFrXmz4E25x5KreYzFioKTMGIb/766aYU/oyhE1A2Kz8N54HDMtbIktisRDlfPkgDgHkmBPafYn6crigKKOU3bDuNy+RvQ/pKUOCcoj75HusDwEND727dXmONBVvUzb0fthGr+TGX3KcB0="; 
//			String aliPayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiGXR6mODQ/CaQlOOGzSe+4RfLVMCnEXlyzWctyOhHOGCrxDpleUPwETNk6a/uafi3ZKKwYUs+Yxan88x84Y7xYYa5Ooqd+Ssqh54VpnxSkMZrrCxALGPqnSFLXA9Ga3ilK5DuiBcWohTyrpD+BIxvjIDVfy70BaXJJtoqnbcF7LJLdQVjvjtuoIvx/mKNfvADWp4nQh+taurRpbdxXCXvlVjLIoznSOGt8KwKe9JmxJaG1n9UEHbD0HCK02josBdpzg80lXWe2rcmZZgDzQ/jxCrJJbM3XuMQpmZjKE1KEe/P0+yVss2lzDpxGwbKkhr8NqvJv3zGR7PD5jK/T1z7wIDAQAB" ;
			
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
					msgModel.setData(StringUtil.nullToDoubleFormatStr(response.getTotalAmount()));
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
	public static MsgModel<Double> getAliPayRefundInfo(String outTradeNo, String tradeNo, Double refund_fee,String outRequestNo){
		MsgModel<Double> msgModel = new MsgModel<Double> ();
		//JAVA服务端SDK生成APP支付订单信息示例
		try {
			
//		String appId ="2019060365453178";
//		String appPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCb23dnLYvrYrAp3sDe76FKlOCpRMh45b6kIgmyOOI0H3MP8G5JfGesuFScACL3N30M9MoKZPD2erMCZWFkVaIPGJdrF+dNNr3pb80XtOLorsYr5eiXQJrFV4ACR0kZfr8Q3z1xPLtuqTVfx0dMzAABNOoK1kCZ60wq+TQOMOcIkFWT6UGYWti9RQihepKO6Ud1xCexfW0LNMO54JvBCRnVvWMWsxfJV93XW/O9byigsyJJXbBITW4Q5kEzky6bRmGCIcrORjJo/KfghZssZ+F3SH64rzU+qX/DRWippuptuLbNeKcV6EFBUW6EkfkuZXe45sHqG+TBTPI0oeTAHAwbAgMBAAECggEAcQuzRHwtw5J2Mjhmbo0i0QTBGyVPvxjsuQITRFisAaBaBdKfO9/nFskwXWJf+i4eHrqinP1wJWcD1hYQmQTfodF1anERDNlK0DmOacaC2sCB7q84kBSE7eQ0bfKkm8Bp92PknfCuBmrm0syo85P34OgvZaLXgoy/V2AfmLdxq6xbEW0atyo0GtHw1M3axgmN7mdT8b/Kr7W/l9vA0zdDd6u6YAhNgYbnTOsMBH7aMqsHGC3oYmYW+w88qonmVvDHYkOi+6vwIOMc5aDS1baI8T4pKp1GVLGF2P45O+nsgH/uRoH+RNntPK8c5sBsHoNcYnax8HRCm/7rUOEUe3JoWQKBgQDIh7zBZH6yEkQdsoSQTppOk+UuKVJEF2f5CLT4sBsP0yrCg/LpBHAMimv5NuhucPsbdj73HFzmAqiqvTdQxaBGQshdqw+Q7+fyXZZEz7i/UIC0yhoyzpA5RA4FlhKwXxo6y+oVH1sA/V6Pe/6UMECB5PN6fQd1zCmXs5FSWzTzdwKBgQDG+EdibLcWJ+jMQ6052KsFfA/fjUPDlWqpLL/x2dHJ15RHxH4ztAUP0mNsMydqkHUc77uybsbKsI78Whe5chCZARDa9QW4fdcjQNhySzTGzcHpN7Ld96h3uRrSMeNF4wQEv0EnnGh6c9hzwUUZyNYH5bHzXkpHAarVw2gZvATtfQKBgCDrn8JEBESpmQMjFn42WUDDKOGuKPJCwW/xzPvytU6gxSlPziYQ83ArnVQ5fmj7JU452FZrEpk0IIp/K8CX4RsHxasS9Sg/Kl3wnUXIsDTAO5DyiOCEZsrv8DlcjM7lngG/DjvPfPopnkeB/KZJUZ3Bf3NBaWMeVEmyUg4qKQQ7AoGAZa50LKSKXFRA8EKVwD4uCDuCkShMPSOawqIP5bT/NTtql9Ke/CU/gPpvvNfXiOoSL5uEPIfIJ8VbDTOtdCQKv2aACqW1KreEvothEefFLOPx+RFx42WxPp3mSw0brJd7ckGbNncEHbAeKenXOnochySncEv7gZYki+G0zZH8orECgYEAgRZEpPjqcnNHtexAeWjJen/nUftBU2hNFbhJID8ZKq4AeuHNyLELZN/PqDbvMR95s0AmiPdb36ioF56wgP81rIvm/5/IrduZJMt03LcxGuxA9jyg+q9k2ZmlYI1jtfdRDauskI21wsM9cYzmFX1RmFYtjcd2w386xbitwox5CRQ="; 
//		String aliPayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh/MBO2via0CS1A1jYeiTl1RA7V2bl+h1teqZ3FtXZJqgGjQXQHXtY6bCFJP8VSZlkpC6jE/s/JQ+vq9BJxyuM9dQkKr4S3NvTLdv0o8tnkY4yk6XJ9+7uSfxrQF0eXelgBfHbIlnKohEC+Ib1K1NzB5wAGfVUbMnsysTS+A6/Y4JMNk40rvkpk7VXk5+JN7hbG8ou0kVqjSHrTq4hS+cMHMib/as9k5PrLtzZyiWcDv/9RDbw/OlCE+Q2BKLyEb+gB/TknOMSVH3fdCeQYzomA5bohGCDpqK0UAz8l3ftAecYdr1Y5Yvtrd2fnuW6qupdXYQKpvVsctR61nsPBdknQIDAQAB" ;
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
			System.out.println("退款接口=========="+body);
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
	public static MsgModel<Boolean> checkQueryInfo( String outTradeNo,String tradeNo,String outRequestNo,Double refund_fee){
		MsgModel<Boolean> msgModel = new MsgModel<Boolean> ();
		try {
////			String appId ="2019060365453178";
//			String appPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCb23dnLYvrYrAp3sDe76FKlOCpRMh45b6kIgmyOOI0H3MP8G5JfGesuFScACL3N30M9MoKZPD2erMCZWFkVaIPGJdrF+dNNr3pb80XtOLorsYr5eiXQJrFV4ACR0kZfr8Q3z1xPLtuqTVfx0dMzAABNOoK1kCZ60wq+TQOMOcIkFWT6UGYWti9RQihepKO6Ud1xCexfW0LNMO54JvBCRnVvWMWsxfJV93XW/O9byigsyJJXbBITW4Q5kEzky6bRmGCIcrORjJo/KfghZssZ+F3SH64rzU+qX/DRWippuptuLbNeKcV6EFBUW6EkfkuZXe45sHqG+TBTPI0oeTAHAwbAgMBAAECggEAcQuzRHwtw5J2Mjhmbo0i0QTBGyVPvxjsuQITRFisAaBaBdKfO9/nFskwXWJf+i4eHrqinP1wJWcD1hYQmQTfodF1anERDNlK0DmOacaC2sCB7q84kBSE7eQ0bfKkm8Bp92PknfCuBmrm0syo85P34OgvZaLXgoy/V2AfmLdxq6xbEW0atyo0GtHw1M3axgmN7mdT8b/Kr7W/l9vA0zdDd6u6YAhNgYbnTOsMBH7aMqsHGC3oYmYW+w88qonmVvDHYkOi+6vwIOMc5aDS1baI8T4pKp1GVLGF2P45O+nsgH/uRoH+RNntPK8c5sBsHoNcYnax8HRCm/7rUOEUe3JoWQKBgQDIh7zBZH6yEkQdsoSQTppOk+UuKVJEF2f5CLT4sBsP0yrCg/LpBHAMimv5NuhucPsbdj73HFzmAqiqvTdQxaBGQshdqw+Q7+fyXZZEz7i/UIC0yhoyzpA5RA4FlhKwXxo6y+oVH1sA/V6Pe/6UMECB5PN6fQd1zCmXs5FSWzTzdwKBgQDG+EdibLcWJ+jMQ6052KsFfA/fjUPDlWqpLL/x2dHJ15RHxH4ztAUP0mNsMydqkHUc77uybsbKsI78Whe5chCZARDa9QW4fdcjQNhySzTGzcHpN7Ld96h3uRrSMeNF4wQEv0EnnGh6c9hzwUUZyNYH5bHzXkpHAarVw2gZvATtfQKBgCDrn8JEBESpmQMjFn42WUDDKOGuKPJCwW/xzPvytU6gxSlPziYQ83ArnVQ5fmj7JU452FZrEpk0IIp/K8CX4RsHxasS9Sg/Kl3wnUXIsDTAO5DyiOCEZsrv8DlcjM7lngG/DjvPfPopnkeB/KZJUZ3Bf3NBaWMeVEmyUg4qKQQ7AoGAZa50LKSKXFRA8EKVwD4uCDuCkShMPSOawqIP5bT/NTtql9Ke/CU/gPpvvNfXiOoSL5uEPIfIJ8VbDTOtdCQKv2aACqW1KreEvothEefFLOPx+RFx42WxPp3mSw0brJd7ckGbNncEHbAeKenXOnochySncEv7gZYki+G0zZH8orECgYEAgRZEpPjqcnNHtexAeWjJen/nUftBU2hNFbhJID8ZKq4AeuHNyLELZN/PqDbvMR95s0AmiPdb36ioF56wgP81rIvm/5/IrduZJMt03LcxGuxA9jyg+q9k2ZmlYI1jtfdRDauskI21wsM9cYzmFX1RmFYtjcd2w386xbitwox5CRQ="; 
//			String aliPayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh/MBO2via0CS1A1jYeiTl1RA7V2bl+h1teqZ3FtXZJqgGjQXQHXtY6bCFJP8VSZlkpC6jE/s/JQ+vq9BJxyuM9dQkKr4S3NvTLdv0o8tnkY4yk6XJ9+7uSfxrQF0eXelgBfHbIlnKohEC+Ib1K1NzB5wAGfVUbMnsysTS+A6/Y4JMNk40rvkpk7VXk5+JN7hbG8ou0kVqjSHrTq4hS+cMHMib/as9k5PrLtzZyiWcDv/9RDbw/OlCE+Q2BKLyEb+gB/TknOMSVH3fdCeQYzomA5bohGCDpqK0UAz8l3ftAecYdr1Y5Yvtrd2fnuW6qupdXYQKpvVsctR61nsPBdknQIDAQAB" ;
////			
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
	
	
	/**
	 * 支付宝转帐
	 * @param outTradeNo
	 * @param tradeNo
	 * @return
	 */
	public static MsgModel<String> TransToaccount(String outTradeNo, String tradeNo){
		MsgModel<String> msgModel = new MsgModel<String> ();
		try{
			
			
//			String appId ="2017021505686225";
//			String appPrivateKey = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDFQXRzS+Ud3mbZWxdeEXf+hgx11EgKw6JeKMjdxcjIZcMPwheo7MfCE/cXEbdYvQuhoMcEXq44uFdYPJe0A0/837D0hAvkckwG1azYS9WMsKPKstDYai7veaoVWHcJgauIIPnW7EDihUAYksqvH/V5dCQAdqMNTUDDwWbBeWWf63EJwN5+r/8t8uGZTrEM0nCmy98WXIHpeA7nk5e9DQ893Sf5Uz+j7K7I7rLjKr6my6CwKYgVzbHphnGG2q4QjkvCXY6M4dbX4AstiwgIHDadzTWC0245E+9Uq5pWlMsROe9P6fxucFJHxNusqM5GBxr/gHQpC9Sfr6yvYKkZMIAvAgMBAAECggEBAK/KHmJl36DXw+aupEuD2+ErS01yymU1ZazuQdxKWB4neke/3GAB7B/MtSaM9k2R5By9cL83NaJ8vfJ3xWZ6kT01iLjQONz080YgBLKaGo0QGlgZNb+4GW33ihkNxr/lHJ3rbVwZHdYBlFaR8ylQnO4JD9CN6c9/6ljKMa34ZxW1JokpM9nBDCcUcojPnn/jO8QLSjOKzZBbHlXzrhC0XXeIAyrUEmM5+uJizTfOS9GaLr1RnncKXrDRwbMrnw8qLCOpEBIEBEIcWDfHc6qVrRXGIND+OVTQaj1yil6mC0E+sEtPLF5VMv4d21suGdNfEzFctZUe7SWrx671GgvXYYkCgYEA8I+E87DeqOJPdIeqhEsSUok1/KRd6KXPUwzs99wf+VvcqFVMvNhqrJayeodbA+hZC/qxTx+t0ceVnALOvtEFZOt5AvCxONqmRyJ3uwJWM+NPyzDBwonMxWcZ4KvlpwSO8DpGWgq4NabR3rkfHnB7cLnIlonxcZA2fffdN7RhP5UCgYEA0epsQCPyodbQqGhId+wATDrXz5yJp6G15dp+NiMOyFy7w0YkExaQXO9zMz/jZyF2uLSt180CzZLGcSVCQR9mjuDdFhr1xYv/HOltV7wiax7wPlJWEr8SW/rvPciuTLcl2bNJhrUd6g76AOVh7Uwp7kVrrEs6g7ipCVwvsqEGH7MCgYEAravPnmdouKB6IHT5Y/0Ww0OQGE9KBPYAEcNZKTBgXRQ238iFWCkeCm/ZdqBV0yJhd0aLz8XdJYBjwHg+boBU1qExgi/molcoiF6X9gkf1uIa72TJ3frCPRnMEunHKKcf0ssfk039464GAjfAAafPVniqGhSInSYyUQsrSFYPh2kCgYEA0U3O9f1h9mC82M5yQ7C7c0bQ3J+W2eByAGflpJE8MifUyBZt7eJ3u1K+KQGw+qnxOKZfGGfAPb31w+eEsm5e0mP/zgS/vmOi0e8mKW7bWrgUj++FB1ghNDl2xsIMmU2cnf8Ydwqscy7PtIkDJZrlzEDH5Zl1FNXRJhYXMSNBeeUCgYEAiNvFnzdL3w/NvUcNOAPRfo/pnVksGTFrXmz4E25x5KreYzFioKTMGIb/766aYU/oyhE1A2Kz8N54HDMtbIktisRDlfPkgDgHkmBPafYn6crigKKOU3bDuNy+RvQ/pKUOCcoj75HusDwEND727dXmONBVvUzb0fthGr+TGX3KcB0="; 
//			String aliPayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiGXR6mODQ/CaQlOOGzSe+4RfLVMCnEXlyzWctyOhHOGCrxDpleUPwETNk6a/uafi3ZKKwYUs+Yxan88x84Y7xYYa5Ooqd+Ssqh54VpnxSkMZrrCxALGPqnSFLXA9Ga3ilK5DuiBcWohTyrpD+BIxvjIDVfy70BaXJJtoqnbcF7LJLdQVjvjtuoIvx/mKNfvADWp4nQh+taurRpbdxXCXvlVjLIoznSOGt8KwKe9JmxJaG1n9UEHbD0HCK02josBdpzg80lXWe2rcmZZgDzQ/jxCrJJbM3XuMQpmZjKE1KEe/P0+yVss2lzDpxGwbKkhr8NqvJv3zGR7PD5jK/T1z7wIDAQAB" ;
			
			//JAVA服务端SDK生成APP支付订单信息示例
			String appId = Constants.conf.getProperty("alipay.app.appId");
			String appPrivateKey = Constants.conf.getProperty("alipay.app.private.key");
			String aliPayPublicKey = Constants.conf.getProperty("alipay.app.public.key");
			
			Map<String, String> paramMap = new HashMap<String, String> ();
			paramMap.put("out_biz_no", "20180110123456789");
			paramMap.put("payee_type", "ALIPAY_LOGONID");
			paramMap.put("payee_account", "13095520537");
			paramMap.put("amount", "0.1");
			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, appPrivateKey, "json","GBK", aliPayPublicKey,"RSA2");
			
			AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
			request.setBizContent(StringUtil.mapStrToJson(paramMap));
			AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
			String body = response.getBody();
			System.out.println(body);
//			try {
//				List<String> successStatusList = new ArrayList<String> ();
//				successStatusList.add("TRADE_SUCCESS");		//交易支付成功
//				successStatusList.add("TRADE_FINISHED");	//交易结束，不可退款
//				
//				Map<String, Object> json2Map = JsonParseUtil.json2Map(body);
//				String result = StringUtil.null2Str(json2Map.get("alipay_trade_query_response"));
//				Map<String, Object> map = JsonParseUtil.json2Map(result);
//				if(map != null
//						&& map.containsKey("trade_status")
//						&& successStatusList.contains(StringUtil.null2Str(map.get("trade_status")).toUpperCase())
//						&& !StringUtil.isNull(response.getTotalAmount())){
//					msgModel.setIsSucc(true);
//					msgModel.setData(StringUtil.nullToDoubleFormatStr(response.getTotalAmount()));
//					msgModel.setMap(map);
//					return msgModel;
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
//	public static void main(String[] args) {
//		AliPayUtil.TransToaccount("", "");
//	}
	
//	/**退款查询
//	 * 返回数据代表退款成功
//	 */
//	public static void query() {
//		try {
//			String appId ="2017021505686225";
//			String appPrivateKey = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDFQXRzS+Ud3mbZWxdeEXf+hgx11EgKw6JeKMjdxcjIZcMPwheo7MfCE/cXEbdYvQuhoMcEXq44uFdYPJe0A0/837D0hAvkckwG1azYS9WMsKPKstDYai7veaoVWHcJgauIIPnW7EDihUAYksqvH/V5dCQAdqMNTUDDwWbBeWWf63EJwN5+r/8t8uGZTrEM0nCmy98WXIHpeA7nk5e9DQ893Sf5Uz+j7K7I7rLjKr6my6CwKYgVzbHphnGG2q4QjkvCXY6M4dbX4AstiwgIHDadzTWC0245E+9Uq5pWlMsROe9P6fxucFJHxNusqM5GBxr/gHQpC9Sfr6yvYKkZMIAvAgMBAAECggEBAK/KHmJl36DXw+aupEuD2+ErS01yymU1ZazuQdxKWB4neke/3GAB7B/MtSaM9k2R5By9cL83NaJ8vfJ3xWZ6kT01iLjQONz080YgBLKaGo0QGlgZNb+4GW33ihkNxr/lHJ3rbVwZHdYBlFaR8ylQnO4JD9CN6c9/6ljKMa34ZxW1JokpM9nBDCcUcojPnn/jO8QLSjOKzZBbHlXzrhC0XXeIAyrUEmM5+uJizTfOS9GaLr1RnncKXrDRwbMrnw8qLCOpEBIEBEIcWDfHc6qVrRXGIND+OVTQaj1yil6mC0E+sEtPLF5VMv4d21suGdNfEzFctZUe7SWrx671GgvXYYkCgYEA8I+E87DeqOJPdIeqhEsSUok1/KRd6KXPUwzs99wf+VvcqFVMvNhqrJayeodbA+hZC/qxTx+t0ceVnALOvtEFZOt5AvCxONqmRyJ3uwJWM+NPyzDBwonMxWcZ4KvlpwSO8DpGWgq4NabR3rkfHnB7cLnIlonxcZA2fffdN7RhP5UCgYEA0epsQCPyodbQqGhId+wATDrXz5yJp6G15dp+NiMOyFy7w0YkExaQXO9zMz/jZyF2uLSt180CzZLGcSVCQR9mjuDdFhr1xYv/HOltV7wiax7wPlJWEr8SW/rvPciuTLcl2bNJhrUd6g76AOVh7Uwp7kVrrEs6g7ipCVwvsqEGH7MCgYEAravPnmdouKB6IHT5Y/0Ww0OQGE9KBPYAEcNZKTBgXRQ238iFWCkeCm/ZdqBV0yJhd0aLz8XdJYBjwHg+boBU1qExgi/molcoiF6X9gkf1uIa72TJ3frCPRnMEunHKKcf0ssfk039464GAjfAAafPVniqGhSInSYyUQsrSFYPh2kCgYEA0U3O9f1h9mC82M5yQ7C7c0bQ3J+W2eByAGflpJE8MifUyBZt7eJ3u1K+KQGw+qnxOKZfGGfAPb31w+eEsm5e0mP/zgS/vmOi0e8mKW7bWrgUj++FB1ghNDl2xsIMmU2cnf8Ydwqscy7PtIkDJZrlzEDH5Zl1FNXRJhYXMSNBeeUCgYEAiNvFnzdL3w/NvUcNOAPRfo/pnVksGTFrXmz4E25x5KreYzFioKTMGIb/766aYU/oyhE1A2Kz8N54HDMtbIktisRDlfPkgDgHkmBPafYn6crigKKOU3bDuNy+RvQ/pKUOCcoj75HusDwEND727dXmONBVvUzb0fthGr+TGX3KcB0="; 
//			String aliPayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiGXR6mODQ/CaQlOOGzSe+4RfLVMCnEXlyzWctyOhHOGCrxDpleUPwETNk6a/uafi3ZKKwYUs+Yxan88x84Y7xYYa5Ooqd+Ssqh54VpnxSkMZrrCxALGPqnSFLXA9Ga3ilK5DuiBcWohTyrpD+BIxvjIDVfy70BaXJJtoqnbcF7LJLdQVjvjtuoIvx/mKNfvADWp4nQh+taurRpbdxXCXvlVjLIoznSOGt8KwKe9JmxJaG1n9UEHbD0HCK02josBdpzg80lXWe2rcmZZgDzQ/jxCrJJbM3XuMQpmZjKE1KEe/P0+yVss2lzDpxGwbKkhr8NqvJv3zGR7PD5jK/T1z7wIDAQAB" ;
//			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, appPrivateKey, "json", "GBK", aliPayPublicKey, "RSA2"); //获得初始化的AlipayClient
//			AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
//			AlipayRefund alipayRefund= new AlipayRefund();
//			alipayRefund.setOut_trade_no("2018052914071023993");      //这个是商户的订单号
////			alipayRefund.setTrade_no(tradeNo);             //这个是支付宝的订单号
////			alipayRefund.setRefund_amount(refund_fee);   //退款金额
//			alipayRefund.setOut_request_no("T2018060612021077700");  //本次退款请求流水号
//			request.setBizContent(JsonUtil.bean2json(alipayRefund));
//			AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
//			System.out.println("response++++++"+response);
//			String body = response.getBody();
//			System.out.println("refund_info++++++"+body);
//	}catch(Exception e) {
//		e.printStackTrace();
//	}
//	}
	
	public static void main(String[] args) {
	}
}
