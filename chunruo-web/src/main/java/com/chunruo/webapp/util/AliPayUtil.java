package com.chunruo.webapp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jettison.json.JSONException;

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
import com.chunruo.core.util.JsonParseUtil;
import com.chunruo.core.util.JsonUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.webapp.vo.AlipayRefund;


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
	public static MsgModel<Double> getAliPayRefundInfo(String outTradeNo, String tradeNo,Double refund_fee,String outRequestNo){
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
//		String appId = "2017021505686225";
//		String appPrivateKey = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDFQXRzS+Ud3mbZWxdeEXf+hgx11EgKw6JeKMjdxcjIZcMPwheo7MfCE/cXEbdYvQuhoMcEXq44uFdYPJe0A0/837D0hAvkckwG1azYS9WMsKPKstDYai7veaoVWHcJgauIIPnW7EDihUAYksqvH/V5dCQAdqMNTUDDwWbBeWWf63EJwN5+r/8t8uGZTrEM0nCmy98WXIHpeA7nk5e9DQ893Sf5Uz+j7K7I7rLjKr6my6CwKYgVzbHphnGG2q4QjkvCXY6M4dbX4AstiwgIHDadzTWC0245E+9Uq5pWlMsROe9P6fxucFJHxNusqM5GBxr/gHQpC9Sfr6yvYKkZMIAvAgMBAAECggEBAK/KHmJl36DXw+aupEuD2+ErS01yymU1ZazuQdxKWB4neke/3GAB7B/MtSaM9k2R5By9cL83NaJ8vfJ3xWZ6kT01iLjQONz080YgBLKaGo0QGlgZNb+4GW33ihkNxr/lHJ3rbVwZHdYBlFaR8ylQnO4JD9CN6c9/6ljKMa34ZxW1JokpM9nBDCcUcojPnn/jO8QLSjOKzZBbHlXzrhC0XXeIAyrUEmM5+uJizTfOS9GaLr1RnncKXrDRwbMrnw8qLCOpEBIEBEIcWDfHc6qVrRXGIND+OVTQaj1yil6mC0E+sEtPLF5VMv4d21suGdNfEzFctZUe7SWrx671GgvXYYkCgYEA8I+E87DeqOJPdIeqhEsSUok1/KRd6KXPUwzs99wf+VvcqFVMvNhqrJayeodbA+hZC/qxTx+t0ceVnALOvtEFZOt5AvCxONqmRyJ3uwJWM+NPyzDBwonMxWcZ4KvlpwSO8DpGWgq4NabR3rkfHnB7cLnIlonxcZA2fffdN7RhP5UCgYEA0epsQCPyodbQqGhId+wATDrXz5yJp6G15dp+NiMOyFy7w0YkExaQXO9zMz/jZyF2uLSt180CzZLGcSVCQR9mjuDdFhr1xYv/HOltV7wiax7wPlJWEr8SW/rvPciuTLcl2bNJhrUd6g76AOVh7Uwp7kVrrEs6g7ipCVwvsqEGH7MCgYEAravPnmdouKB6IHT5Y/0Ww0OQGE9KBPYAEcNZKTBgXRQ238iFWCkeCm/ZdqBV0yJhd0aLz8XdJYBjwHg+boBU1qExgi/molcoiF6X9gkf1uIa72TJ3frCPRnMEunHKKcf0ssfk039464GAjfAAafPVniqGhSInSYyUQsrSFYPh2kCgYEA0U3O9f1h9mC82M5yQ7C7c0bQ3J+W2eByAGflpJE8MifUyBZt7eJ3u1K+KQGw+qnxOKZfGGfAPb31w+eEsm5e0mP/zgS/vmOi0e8mKW7bWrgUj++FB1ghNDl2xsIMmU2cnf8Ydwqscy7PtIkDJZrlzEDH5Zl1FNXRJhYXMSNBeeUCgYEAiNvFnzdL3w/NvUcNOAPRfo/pnVksGTFrXmz4E25x5KreYzFioKTMGIb/766aYU/oyhE1A2Kz8N54HDMtbIktisRDlfPkgDgHkmBPafYn6crigKKOU3bDuNy+RvQ/pKUOCcoj75HusDwEND727dXmONBVvUzb0fthGr+TGX3KcB0=";
//		String aliPayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiGXR6mODQ/CaQlOOGzSe+4RfLVMCnEXlyzWctyOhHOGCrxDpleUPwETNk6a/uafi3ZKKwYUs+Yxan88x84Y7xYYa5Ooqd+Ssqh54VpnxSkMZrrCxALGPqnSFLXA9Ga3ilK5DuiBcWohTyrpD+BIxvjIDVfy70BaXJJtoqnbcF7LJLdQVjvjtuoIvx/mKNfvADWp4nQh+taurRpbdxXCXvlVjLIoznSOGt8KwKe9JmxJaG1n9UEHbD0HCK02josBdpzg80lXWe2rcmZZgDzQ/jxCrJJbM3XuMQpmZjKE1KEe/P0+yVss2lzDpxGwbKkhr8NqvJv3zGR7PD5jK/T1z7wIDAQAB";
		
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("2018072913251095369", "T2018080814241092516"); y
//		map.put("2018080709241050269", "T2018080720101062078");y
//		map.put("2018080921161039031", "T2018080921171081014");y
//		map.put("2018071213211075017", "T2018080923281010505");y
//		map.put("2018070920521088623", "T2018080921371093477");y
//		map.put("2018072810451075924", "T2018081009391008717"); 102 y
//		map.put("2018080919441010374", "T2018080920011031570"); 35
//		map.put("2018080821571069408", "T2018080908201075361"); 365
//		map.put("2018072423141062954", "T2018080818391039791"); 10
//		map.put("2018072910321054634", "T2018080817231056290");270
//		map.put("2018080708221001536", "T2018080802021096009");468.4
//		map.put("2018080522291015007", "T2018080719531084664");165
		
//		map.put("2018080600321096899", "T2018081012001040751");270
//		map.put("2018080112081085885", "T2018081012191003397");368
//		map.put("2018080815281048258", "T2018081015061085421");260
//		map.put("2018080523261036908", "T2018081016461085022");169
//		map.put("2018072421261055214", "T2018081214371064676");360
		
//		MsgModel<Boolean> msgModel = AliPayUtil.checkQueryInfo("2018071616381087157","","T2018071714301075779");
//		System.out.println(msgModel.getIsSucc());
		//		if(msgModel.getIsSucc() && StringUtil.nullToBoolean(msgModel.getData())) {
//			
//		}
		
//		MsgModel<Double> dModel = AliPayUtil.getAliPayRefundInfo("2018080709241050269","",90d,"T2018080720101062078");
//		System.out.println("刚退款了======"+dModel.getIsSucc());
		
//		MsgModel<Double> dModels = AliPayUtil.getAliPayRefundInfo("2018072810451075924","",102d,"T2018081009391008717");
//		System.out.println("刚退款了======"+dModels.getIsSucc());
		
//		MsgModel<Double> dModels1 = AliPayUtil.getAliPayRefundInfo("2018080919441010374","",35d,"T2018080920011031570");
//		System.out.println("刚退款了dModels1======"+dModels1.getIsSucc());
//		MsgModel<Double> dModels2 = AliPayUtil.getAliPayRefundInfo("2018080821571069408","",365d,"T2018080908201075361");
//		System.out.println("刚退款了dModels2======"+dModels2.getIsSucc());
//		MsgModel<Double> dModels3 = AliPayUtil.getAliPayRefundInfo("2018072423141062954","",10d,"T2018080818391039791");
//		System.out.println("刚退款了dModels3======"+dModels3.getIsSucc());
//		MsgModel<Double> dModels4 = AliPayUtil.getAliPayRefundInfo("2018072910321054634","",270d,"T2018080817231056290");
//		System.out.println("刚退款了dModels4======"+dModels4.getIsSucc());
//		MsgModel<Double> dModels5 = AliPayUtil.getAliPayRefundInfo("2018080708221001536","",468.4d,"T2018080802021096009");
//		System.out.println("刚退款了dModels5======"+dModels5.getIsSucc());
//		MsgModel<Double> dModels6 = AliPayUtil.getAliPayRefundInfo("2018080522291015007","",165d,"T2018080719531084664");
//		System.out.println("刚退款了dModels6======"+dModels6.getIsSucc());
//		
//		MsgModel<Double> dModels7 = AliPayUtil.getAliPayRefundInfo("2018080600321096899","",270d,"T2018081012001040751");
//		System.out.println("刚退款了dModels7======"+dModels7.getIsSucc());
//		MsgModel<Double> dModels8 = AliPayUtil.getAliPayRefundInfo("2018080112081085885","",368d,"T2018081012191003397");
//		System.out.println("刚退款了dModels8======"+dModels8.getIsSucc());
//		MsgModel<Double> dModels9 = AliPayUtil.getAliPayRefundInfo("2018080815281048258","",260d,"T2018081015061085421");
//		System.out.println("刚退款了dModels9======"+dModels9.getIsSucc());
//		MsgModel<Double> dModels10 = AliPayUtil.getAliPayRefundInfo("2018080523261036908","",169d,"T2018081016461085022");
//		System.out.println("刚退款了dModels10======"+dModels10.getIsSucc());
//		MsgModel<Double> dModels11 = AliPayUtil.getAliPayRefundInfo("2018072421261055214","",360d,"T2018081214371064676");
//		System.out.println("刚退款了dModels11======"+dModels11.getIsSucc());
		
//		MsgModel<Double> dModels12 = AliPayUtil.getAliPayRefundInfo("2018081409351065030","",79d,"T2018081410261037481");
//		System.out.println("刚退款了dModels12======"+dModels12.getIsSucc());
//		MsgModel<Double> dModels13 = AliPayUtil.getAliPayRefundInfo("2018081409351065030","",82d,"T2018081410261005421");
//		System.out.println("刚退款了dModels13======"+dModels13.getIsSucc());
//		MsgModel<Double> dModels14 = AliPayUtil.getAliPayRefundInfo("2018081409351065030","",98d,"T2018081410261068652");
//		System.out.println("刚退款了dModels14======"+dModels14.getIsSucc());
		
//		MsgModel<Double> dModels15 = AliPayUtil.getAliPayRefundInfo("2018073116011040748","",10d,"T2018081318501075057");
//		System.out.println("刚退款了dModels15======"+dModels15.getIsSucc());
//		MsgModel<Double> dModels16 = AliPayUtil.getAliPayRefundInfo("2018080409061062305","",194d,"T2018081319511017644");
//		System.out.println("刚退款了dModels16======"+dModels16.getIsSucc());
//		MsgModel<Double> dModels17 = AliPayUtil.getAliPayRefundInfo("2018081314551084130","",90d,"T2018081316531055088");
//		System.out.println("刚退款了dModels17======"+dModels17.getIsSucc());
//		MsgModel<Double> dModels18 = AliPayUtil.getAliPayRefundInfo("2018081019101070811","",52.6d,"T2018081312211060879");
//		System.out.println("刚退款了dModels18======"+dModels18.getIsSucc());
//		MsgModel<Double> dModels19 = AliPayUtil.getAliPayRefundInfo("2018081119091019177","",72d,"T2018081215071043745");
//		System.out.println("刚退款了dModels19======"+dModels19.getIsSucc());
//		MsgModel<Double> dModels20 = AliPayUtil.getAliPayRefundInfo("2018081118051007802","",368d,"T2018081118091095563");
//		System.out.println("刚退款了dModels20======"+dModels20.getIsSucc());
//		MsgModel<Double> dModels21 = AliPayUtil.getAliPayRefundInfo("2018081118051007802","",128d,"T2018081118091021833");
//		System.out.println("刚退款了dModels21======"+dModels21.getIsSucc());
//		MsgModel<Double> dModels22 = AliPayUtil.getAliPayRefundInfo("2018080718441084401","",385d,"T2018081015551005852");
//		System.out.println("刚退款了dModels22======"+dModels22.getIsSucc());
//		MsgModel<Double> dModels23 = AliPayUtil.getAliPayRefundInfo("2018080712351055583","",370d,"T2018080919251044361");
//		System.out.println("刚退款了dModels23======"+dModels23.getIsSucc());
//		MsgModel<Double> dModels24 = AliPayUtil.getAliPayRefundInfo("2018080723191094837","",270d,"T2018080823321085337");
//		System.out.println("刚退款了dModels24======"+dModels24.getIsSucc());
//		MsgModel<Double> dModels25 = AliPayUtil.getAliPayRefundInfo("2018080100531060411","",270d,"T2018080815351052924");
//		System.out.println("刚退款了dModels25======"+dModels25.getIsSucc());
		//2018080421551003512
//		MsgModel<Boolean> dModels25 = AliPayUtil.checkQueryInfo("2018081423191018146","2018081421001004990567822038","T2018081507171090603");
//		System.out.println("刚退款了dModels25======"+dModels25.getIsSucc());
		
//		MsgModel<Double> dModels26 =getAliPayRefundInfo("2018081423191018146", "2018081421001004990567822038",135d,"T2018081507171090603");
//		System.out.println(dModels26.getIsSucc());
		
//		MsgModel<Boolean> dModels27 = AliPayUtil.checkQueryInfo("2018081423191018146","2018081421001004990567822038","T2018081507171090603",135d);
//		System.out.println("刚退款了dModels27======"+dModels27.getIsSucc());
	
//		MsgModel<Double> dModelxs28 =getAliPayRefundInfo("2018081420421021667", "2018081421001004240547764214",385d,"T2018081421541091434");
//		System.out.println(dModelxs28.getIsSucc());
//		MsgModel<Boolean> dModels28 = AliPayUtil.checkQueryInfo("2018081420421021667","2018081421001004240547764214","T2018081421541091434",385d);
//		System.out.println("刚退款了dModels28======"+dModels28.getIsSucc());
//		
//		MsgModel<Double> dModelxs30 =getAliPayRefundInfo("2018081214301087460", "2018081221001004180574421639",20d,"T2018081513441082523");
//		System.out.println(dModelxs30.getIsSucc());
//		MsgModel<Boolean> dModels30 = AliPayUtil.checkQueryInfo("2018081214301087460","2018081221001004180574421639","T2018081513441082523",20d);
//		System.out.println("刚退款了dModels30======"+dModels30.getIsSucc());
//		
//		MsgModel<Double> dModelxs31 =getAliPayRefundInfo("2018080919271005590", "2018080921001004080568793530",8.4d,"T2018081414251003018");
//		System.out.println(dModelxs31.getIsSucc());
//		MsgModel<Boolean> dModels31 = AliPayUtil.checkQueryInfo("2018080919271005590","2018080921001004080568793530","T2018081414251003018",8.4d);
//		System.out.println("刚退款了dModels31======"+dModels31.getIsSucc());
//
//		MsgModel<Double> dModelxs32 =getAliPayRefundInfo("2018081420471084458", "2018081421001004150587257052",259d,"T2018081421231028732");
//		System.out.println(dModelxs32.getIsSucc());
//		MsgModel<Boolean> dModels32 = AliPayUtil.checkQueryInfo("2018081420471084458","2018081421001004150587257052","T2018081513441082523",259d);
//		System.out.println("刚退款了dModels32======"+dModels32.getIsSucc());
//
//		MsgModel<Double> dModelxs33 =getAliPayRefundInfo("2018081511051081195", "2018081521001004810574476351",172d,"T2018081511111027486");
//		System.out.println(dModelxs33.getIsSucc());
//		MsgModel<Boolean> dModels33 = AliPayUtil.checkQueryInfo("2018081511051081195","2018081521001004810574476351","T2018081511111027486",172d);
//		System.out.println("刚退款了dModels33======"+dModels33.getIsSucc());
//		
//		MsgModel<Double> dModelxs34 =getAliPayRefundInfo("2018081511011004777", "2018081521001004810576758504",172d,"T2018081511031075803");
//		System.out.println(dModelxs34.getIsSucc());
//		MsgModel<Boolean> dModels34 = AliPayUtil.checkQueryInfo("2018081511011004777","2018081521001004810576758504","T2018081511031075803",172d);
//		System.out.println("刚退款了dModels34======"+dModels34.getIsSucc());
//		
//		MsgModel<Double> dModelxs35 =getAliPayRefundInfo("2018080615071060429", "2018080621001004530544012502",169d,"T2018081417451097772");
//		System.out.println(dModelxs35.getIsSucc());
//		MsgModel<Boolean> dModels35 = AliPayUtil.checkQueryInfo("2018080615071060429","2018080621001004530544012502","T2018081417451097772",169d);
//		System.out.println("刚退款了dModels35======"+dModels35.getIsSucc());
//		
//		MsgModel<Double> dModelxs36 =getAliPayRefundInfo("2018081317521031954", "2018081421001004210561139354",58d,"T2018081417281088682");
//		System.out.println(dModelxs36.getIsSucc());
//		MsgModel<Boolean> dModels36 = AliPayUtil.checkQueryInfo("2018081317521031954","2018081421001004210561139354","T2018081417281088682",58d);
//		System.out.println("刚退款了dModels36======"+dModels36.getIsSucc());
//		
//		MsgModel<Double> dModelxs37 =getAliPayRefundInfo("2018081318011032432", "2018081421001004210559425820",56.3d,"T2018081412291060457");
//		System.out.println(dModelxs37.getIsSucc());
//		MsgModel<Boolean> dModels37 = AliPayUtil.checkQueryInfo("2018081318011032432","2018081421001004210559425820","T2018081412291060457",56.3d);
//		System.out.println("刚退款了dModels37======"+dModels37.getIsSucc());
//		
//		MsgModel<Double> dModelxs38 =getAliPayRefundInfo("2018081318011032432", "2018081421001004210559425820",900d,"T2018081412281051486");
//		System.out.println(dModelxs38.getIsSucc());
//		MsgModel<Boolean> dModels38 = AliPayUtil.checkQueryInfo("2018081318011032432","2018081421001004210559425820","T2018081412281051486",900d);
//		System.out.println("刚退款了dModels38======"+dModels38.getIsSucc());
//		
//		MsgModel<Double> dModelxs39 =getAliPayRefundInfo("2018081018451064761", "2018081021001004210544318822",95.05d,"T2018081411001061367");
//		System.out.println(dModelxs39.getIsSucc());
//		MsgModel<Boolean> dModels39 = AliPayUtil.checkQueryInfo("2018081018451064761","2018081021001004210544318822","T2018081411001061367",95.05d);
//		System.out.println("刚退款了dModels39======"+dModels39.getIsSucc());
//		
//		MsgModel<Double> dModelxs40 =getAliPayRefundInfo("2018081018451064761", "2018081021001004210544318822",112.44d,"T2018081411001010445");
//		System.out.println(dModelxs40.getIsSucc());
//		MsgModel<Boolean> dModels40 = AliPayUtil.checkQueryInfo("2018081018451064761","2018081021001004210544318822","T2018081411001010445",112.44d);
//		System.out.println("刚退款了dModels40======"+dModels40.getIsSucc());
//		
//		MsgModel<Double> dModelxs41 =getAliPayRefundInfo("2018081018451064761", "2018081021001004210544318822",126.28d,"T2018081411001062919");
//		System.out.println(dModelxs41.getIsSucc());
//		MsgModel<Boolean> dModels41 = AliPayUtil.checkQueryInfo("2018081018451064761","2018081021001004210544318822","T2018081411001062919",126.28d);
//		System.out.println("刚退款了dModels41======"+dModels41.getIsSucc());
//		
//		MsgModel<Double> dModelxs42 =getAliPayRefundInfo("2018081017021094703", "2018081021001004460540955476",110d,"T2018081022421013761");
//		System.out.println(dModelxs42.getIsSucc());
//		MsgModel<Boolean> dModels42 = AliPayUtil.checkQueryInfo("2018081017021094703","2018081021001004460540955476","T2018081022421013761",110d);
//		System.out.println("刚退款了dModels42======"+dModels42.getIsSucc());
//		
//		MsgModel<Double> dModelxs43 =getAliPayRefundInfo("2019061011101067800", "2019061022001455491042945850",349.21d,"T2019060612541083154");
//		System.out.println(dModelxs43.getIsSucc());
//		MsgModel<Double> dModelxs44 =getAliPayRefundInfo("2019061010471079467", "2019061022001455491043309318",387d,"T2019060612541083155");
//		System.out.println(dModelxs44.getIsSucc());
//		MsgModel<Double> dModelxs45 =getAliPayRefundInfo("2019061010311052565", "2019061022001455491043312927",282d,"T2019060612541083156");
//		System.out.println(dModelxs45.getIsSucc());
//		MsgModel<Boolean> dModels43 = AliPayUtil.checkQueryInfo("2018080101241003687","2018080121001004520546693198","T2018081012541083151",70d);
//		System.out.println("刚退款了dModels43======"+dModels43.getIsSucc());
//		
//		MsgModel<Double> dModelxs44 =getAliPayRefundInfo("2018122410091019208", "2018122422001443750513254647",349d,"T2018122410091074503");
//		MsgModel<Double> dModelxs45 =getAliPayRefundInfo("2018122420461087791", "2018122422001479450536545585",200d,"T2018122420471036208");
//		MsgModel<Double> dModelxs446 =getAliPayRefundInfo("2018122513371081842", "2018122522001466840509843907",201.94d,"T2018122513481041775");
//		MsgModel<Double> dModelxs447 =getAliPayRefundInfo("2018122521201096849", "2018122522001456050561329841",128.44d,"T2018122522061039650");
		//		System.out.println(dModelxs44.getIsSucc());
//		MsgModel<Boolean> dModels44 = AliPayUtil.checkQueryInfo("2018122818441007091","2018122822001455490549805524","T2018122818441062685",170.69d);
//		System.out.println("刚退款了dModels44======"+dModels44.getIsSucc());
//		
//		MsgModel<Double> dModelxs45 =getAliPayRefundInfo("2018122818441007091", "2018122822001455490549805524",170.69d,"T2018122818441062685");
//		System.out.println(dModelxs45.getIsSucc());
//		MsgModel<Boolean> dModels45 = AliPayUtil.checkQueryInfo("2018122821321060609","2018122822001455490551640587","T2018122821321085031",108d);
//		System.out.println("刚退款了dModels45======"+dModels45.getIsSucc());
//		
//		boolean aa = StringUtil.compareObject(StringUtil.nullToDoubleFormatStr("105.00"), StringUtil.nullToDoubleFormatStr(105d));
//		System.out.println(aa);
		
//		Map<String, String> map = new HashMap<String, String> ();
//		map.put("2019010218221034961", "373.54");
//		map.put("2019010218241000541", "258.64");
//		map.put("2019010218281031145", "128.44");
//		map.put("2019010218281043659", "128");
//		map.put("2019010218281064903", "61.49");
//		map.put("2019010218341021205", "393.88");
//		for(Entry<String, String> entry : map.entrySet()) {
//			try {
//				MsgModel<String> msModel = AliPayUtil.getQueryAliPayInfo(entry.getKey(), "");
//				if(StringUtil.nullToBoolean(msModel.getIsSucc()) && StringUtil.compareObject(msModel.getData(), StringUtil.nullToDoubleFormatStr(entry.getValue()))){
//					
//				}else {
//					
//					System.out.println(msModel.getIsSucc() +"==="+ entry.getKey() + "===" + entry.getValue() + "===" + msModel.getData() + "===" + StringUtil.nullToDoubleFormatStr(entry.getValue()));
//				}
//			}catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
	}
}
