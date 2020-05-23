package com.chunruo.easypay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chunruo.core.vo.EasyRequestVo;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.HttpClientUtil;
import com.chunruo.core.util.StringUtil;

/**
 * 无跳转快捷测试
 * @author njp
 */
public class EasyPayUtil {
	protected final static Log log = LogFactory.getLog(EasyPayUtil.class);
	//商户号
	private static String merchant_id = EasyKeyUtils.DEFAULT_MERCHANT_ID;
	//接入机构号
	private static String partner = EasyKeyUtils.DEFAULT_PARTNER;
	//请求地址
	private static String url = EasyKeyUtils.SC_URL;
	//海关报关请求地址
	private static String custom_url = EasyKeyUtils.SC_CUSTOM_URL;
	//海关报关查询请求地址
	private static String custom_query_url = EasyKeyUtils.SC_CUSTOM_QUERY_URL;
	//商户私钥
	private static String key = EasyKeyUtils.MERCHANT_PRIVATE_KEY;
	//易生公钥
	private static String easypay_pub_key = EasyKeyUtils.EASYPAY_PUBLIC_KEY;
	//加密密钥
	private static String DES_ENCODE_KEY = EasyKeyUtils.SC_DES_ENCODE_KEY;

	/**
	 * 无跳转快捷--前台开通
	 * @param acc
	 * @return
	 */
	public static MsgModel<String> nopagesOpenFront(String acc, String outTradeNo, String easyPayNotify){
		Map<String, Object> paramMap = new HashMap<String, Object> ();
		paramMap.put("merchant_id", merchant_id);
		paramMap.put("out_trade_no", outTradeNo);
		paramMap.put("acc", getEncode(acc));
		paramMap.put("front_url", easyPayNotify);

		String bizContent = StringUtil.objToJson(paramMap);
		String service  = "easypay.pay.nopages.openFront";
		return EasyPayUtil.postHttpConnect(bizContent, service);
	}

	/**
	 * 无跳转快捷--快捷查询
	 * @param acc
	 * @return
	 */
	public static MsgModel<Integer> nopagesOpenQuery(String acc){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object> ();
			paramMap.put("merchant_id", merchant_id);
			paramMap.put("acc", getEncode(acc));

			String bizContent = StringUtil.objToJson(paramMap);
			String service  = "easypay.pay.nopages.openQuery";
			MsgModel<String> xsgModel = EasyPayUtil.postHttpConnect(bizContent, service);
			if(StringUtil.nullToBoolean(xsgModel.getIsSucc()) && EasyUtils.rsaVerifySign(xsgModel.getData(), easypay_pub_key)) {
				Map<String, String> objectMap = StringUtil.jsonToHashMap(xsgModel.getData());
				if(objectMap.containsKey("easypay_pay_nopages_openQuery_response")) {
					Map<String, String> dataMap = StringUtil.jsonToHashMap(objectMap.get("easypay_pay_nopages_openQuery_response"));
					if(StringUtil.null2Str(dataMap.get("code")).equals("00")
							&& StringUtil.null2Str(dataMap.get("activate_status")).equals("1")
							&& StringUtil.null2Str(dataMap.get("trade_status")).equals("SUCCESS")) {
						msgModel.setIsSucc(true);
						msgModel.setData(StringUtil.nullToInteger(dataMap.get("pay_card_type")));
						return msgModel;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("查询银行卡绑定信息错误");
		return msgModel;
	}

	/**
	 * 无跳转快捷获取验证码
	 * @param acc
	 * @param mobile
	 * @return
	 */
	public static MsgModel<Void> sendPaySMS(String orderNo, int paymentAmount, String acc, String mobile){
		MsgModel<Void> msgModel = new MsgModel<Void> ();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object> ();
			paramMap.put("merchant_id", merchant_id);
			paramMap.put("out_trade_no", orderNo);
			paramMap.put("amount", paymentAmount);
			paramMap.put("acc", getEncode(acc));   //银行卡号
			paramMap.put("mobile", getEncode(mobile)); //手机号
			//以下为非必填字段
			//paramMap.put("name", "测试");//姓名
			//paramMap.put("idno", getEncode("340827199111111111"));		//身份证
			//paramMap.put("cvv", getEncode("111"));
			//paramMap.put("validity_year", getEncode("11"));
			//paramMap.put("validity_month", getEncode("12"));
			paramMap.put("subject", "subject");
			paramMap.put("body", "body");
			paramMap.put("seller_email", "18679106330@gmail.com");
			paramMap.put("notify_url", "http://test.uzengroup.com/clt/order/alipayNotify.msp");

			String bizContent = StringUtil.objToJson(paramMap);
			String service  = "easypay.pay.nopages.union.sendSMS";
			MsgModel<String> xsgModel = EasyPayUtil.postHttpConnect(bizContent, service);
			if(StringUtil.nullToBoolean(xsgModel.getIsSucc()) && EasyUtils.rsaVerifySign(xsgModel.getData(), easypay_pub_key)) {
				Map<String, String> objectMap = StringUtil.jsonToHashMap(xsgModel.getData());
				if(objectMap.containsKey("easypay_pay_nopages_union_sendSMS_response")) {
					Map<String, String> dataMap = StringUtil.jsonToHashMap(objectMap.get("easypay_pay_nopages_union_sendSMS_response"));
					if(StringUtil.null2Str(dataMap.get("code")).equals("00")
							&& StringUtil.null2Str(dataMap.get("out_trade_no")).equals(orderNo)
							&& StringUtil.null2Str(dataMap.get("trade_status")).equals("SUCCESS")) {
						msgModel.setIsSucc(true);
						msgModel.setMessage("短信已发送成功");
						return msgModel;
					}else {
						msgModel.setIsSucc(false);
						msgModel.setMessage(StringUtil.null2Str(dataMap.get("msg")));
						return msgModel;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("短信发送失败,请检查或联系客服");
		return msgModel;
	}

	/**
	 * 银联无跳转支付
	 * @param orderId
	 * @param vcode
	 * @param acc
	 * @return
	 */
	public static MsgModel<Void> orderPay(String orderNo, int paymentAmount, String vcode, String acc){
		MsgModel<Void> msgModel = new MsgModel<Void> ();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object> ();
			paramMap.put("merchant_id", merchant_id);
			paramMap.put("orig_out_trade_no", orderNo);
			paramMap.put("amount", paymentAmount);
			paramMap.put("acc", getEncode(acc));   //银行卡号
			paramMap.put("vcode", vcode);
			//以下为非必填字段
			//sParaTemp.put("mobile", getEncode("18010461111")); //手机号

			String bizContent = StringUtil.objToJson(paramMap);
			String service  = "easypay.pay.nopages.pay";
			MsgModel<String> xsgModel = EasyPayUtil.postHttpConnect(bizContent, service);
			if(StringUtil.nullToBoolean(xsgModel.getIsSucc()) && EasyUtils.rsaVerifySign(xsgModel.getData(), easypay_pub_key)) {
				Map<String, String> objectMap = StringUtil.jsonToHashMap(xsgModel.getData());
				if(objectMap.containsKey("easypay_pay_nopages_pay_response")) {
					Map<String, String> dataMap = StringUtil.jsonToHashMap(objectMap.get("easypay_pay_nopages_pay_response"));
					if(StringUtil.null2Str(dataMap.get("code")).equals("00")
							&& StringUtil.null2Str(dataMap.get("out_trade_no")).equals(orderNo)
							&& StringUtil.null2Str(dataMap.get("trade_status")).equals("SUCCESS")) {
						msgModel.setIsSucc(true);
						msgModel.setMessage("支付成功");
						return msgModel;
					}else {
						msgModel.setIsSucc(false);
						msgModel.setMessage(StringUtil.null2Str(dataMap.get("msg")));
						return msgModel;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("支付失败,请检查或联系客服");
		return msgModel;
	}

	/**
	 * 二维码/APP支付-推送订单
	 * @param acc
	 * @return
	 */
	public static MsgModel<String> qrcodePayPush(String outTradeNo, int paymentAmount, String payType, String subject, String easyPayNotify){
		MsgModel<String> msgModel = new MsgModel<String> ();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object> ();
			paramMap.put("merchant_id", merchant_id);
			paramMap.put("out_trade_no", outTradeNo);
			paramMap.put("subject", subject);		//商户描述
			paramMap.put("amount", paymentAmount); 	//交易金额
			paramMap.put("pay_type", payType);//支付类型 	wxNative:微信本地支付； wxAPP:微信APP支付； aliPay:支付宝二维码支付； unionNative:银联二维码支付
			paramMap.put("business_time", DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN));//交易时间 	2019-03-08 16:09:00
			paramMap.put("notify_url", easyPayNotify); //异步通知地址 	http://www.baidu.com/notify/api

			String bizContent = StringUtil.objToJson(paramMap);
			String service  = "easypay.qrcode.pay.push";
			MsgModel<String> xsgModel = EasyPayUtil.postHttpConnect(bizContent, service);
			if(StringUtil.nullToBoolean(xsgModel.getIsSucc()) && EasyUtils.rsaVerifySign(xsgModel.getData(), easypay_pub_key)) {
				Map<String, String> objectMap = StringUtil.jsonToHashMap(xsgModel.getData());
				if(objectMap.containsKey("easypay_qrcode_pay_push_response")) {
					Map<String, String> dataMap = StringUtil.jsonToHashMap(objectMap.get("easypay_qrcode_pay_push_response"));
					if(StringUtil.null2Str(dataMap.get("code")).equals("00")
							&& StringUtil.null2Str(dataMap.get("out_trade_no")).equals(outTradeNo)
							&& StringUtil.null2Str(dataMap.get("trade_status")).equals("BUSINESS_OK")) {
						msgModel.setIsSucc(true);
						msgModel.setData(StringUtil.null2Str(dataMap.get("code_url")));
						msgModel.setPaymentBody(xsgModel.getData());
						return msgModel;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("获取支付信息失败");
		return msgModel;
	}

	/**
	 * 订单查询
	 * @param outTradeNo
	 * @return
	 */
	public static MsgModel<String> queryPayOrder(String outTradeNo){
		return EasyPayUtil.queryPayOrder(outTradeNo, false);
	}

	/**
	 * 订单查询
	 * @param outTradeNo
	 * @param isPaymentRefund
	 * @return
	 */
	public static MsgModel<String> queryPayOrder(String outTradeNo, boolean isPaymentRefund){
		MsgModel<String> msgModel = new MsgModel<String> ();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object> ();
			paramMap.put("merchant_id", merchant_id);
			paramMap.put("out_trade_no", outTradeNo);

			// 默认订单支付查询
			String tradeType = "CONSUME";
			if(StringUtil.nullToBoolean(isPaymentRefund)) {
				// 订单退款查询
				tradeType = "REFUND";
			}

			String bizContent = StringUtil.objToJson(paramMap);
			String service  = "easypay.merchant.query";
			MsgModel<String> xsgModel = EasyPayUtil.postHttpConnect(bizContent, service);
			if(StringUtil.nullToBoolean(xsgModel.getIsSucc()) && EasyUtils.rsaVerifySign(xsgModel.getData(), easypay_pub_key)) {
				Map<String, String> objectMap = StringUtil.jsonToHashMap(xsgModel.getData());
				if(objectMap.containsKey("easypay_merchant_query_response")) {
					Map<String, String> dataMap = StringUtil.jsonToHashMap(objectMap.get("easypay_merchant_query_response"));
					if(StringUtil.null2Str(dataMap.get("code")).equals("00")
							&& StringUtil.null2Str(dataMap.get("out_trade_no")).equals(outTradeNo)
							&& StringUtil.null2Str(dataMap.get("trade_type")).equals(tradeType)
							&& StringUtil.null2Str(dataMap.get("trade_status")).equals("SUCCESS")) {
						msgModel.setIsSucc(true);
						msgModel.setTransactionId(StringUtil.null2Str(dataMap.get("order_no")));
						msgModel.setData(StringUtil.null2Str(dataMap.get("amount")));
						msgModel.setPaymentBody(StringUtil.null2Str(xsgModel.getData()));
						return msgModel;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("获取支付信息失败");
		return msgModel;
	}

	/**
	 * 订单退款
	 * @param acc
	 * @return
	 */
	public static MsgModel<String> refundPayOrder(String refundOrderNo, String orderNo, int refundAmount){
		MsgModel<String> msgModel = new MsgModel<String> ();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object> ();
			paramMap.put("merchant_id", merchant_id);
			paramMap.put("out_trade_no", StringUtil.null2Str(refundOrderNo));
			paramMap.put("origin_trade_no", StringUtil.null2Str(orderNo));
			paramMap.put("refund_amount", StringUtil.nullToInteger(refundAmount));
			String bizContent = StringUtil.objToJson(paramMap);
			String service  = "easypay.merchant.refund";
			MsgModel<String> xsgModel = EasyPayUtil.postHttpConnect(bizContent, service);
			if(StringUtil.nullToBoolean(xsgModel.getIsSucc()) && EasyUtils.rsaVerifySign(xsgModel.getData(), easypay_pub_key)) {
				Map<String, String> objectMap = StringUtil.jsonToHashMap(xsgModel.getData());
				if(objectMap.containsKey("easypay_merchant_refund_response")) {
					Map<String, String> dataMap = StringUtil.jsonToHashMap(objectMap.get("easypay_merchant_refund_response"));
					String msg = StringUtil.null2Str(dataMap.get("msg"));
					if(StringUtil.null2Str(dataMap.get("code")).equals("00")) {
						if(StringUtil.null2Str(dataMap.get("trade_status")).equals("BUSINESS_OK")) {
							// 微信支付能立马退款成功
							msgModel.setIsSucc(true);
							return msgModel;
						}else if(StringUtil.null2Str(msg).equals("BUSINESS_OK")) {
							// 支付宝退款慢,只有状态
							msgModel.setIsSucc(true);
							return msgModel;
						}
					}else if(StringUtil.null2Str(dataMap.get("code")).equals("20")) {
						if(msg.contains("全部退款")) {
							msgModel.setIsSucc(true);
							return msgModel;
						}else {
							// 查询是否已退款成功
							MsgModel<String> qsgModel = EasyPayUtil.queryPayOrder(refundOrderNo, true);
							if(StringUtil.nullToBoolean(qsgModel.getIsSucc())) {
								msgModel.setIsSucc(true);
								return msgModel;
							}
						}	
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("获取支付信息失败");
		return msgModel;
	}

	/**
	 * 请求易生支付服务器
	 * @return
	 */
	public static MsgModel<String> postHttpConnect(String bizContent, String service){
		MsgModel<String> msgModel = new MsgModel<String> ();
		try {
			//加密类型，默认RSA
			String sign_type = EasyKeyUtils.DEFAULT_ENCODE_TYPE;
			//编码类型
			String charset = EasyKeyUtils.DEFAULT_CHARSET;

			//根据请求参数生成的机密串
			Map<String, String> requestMap = new HashMap<String, String>(6);
			requestMap.put("biz_content", bizContent);
			requestMap.put("service", service);
			requestMap.put("partner", partner);
			requestMap.put("sign_type", sign_type);
			requestMap.put("charset", charset);
			requestMap.put("sign", EasyKeyUtils.getSign(key, charset, bizContent));
			log.debug(String.format("easyPay==[%s]", StringUtil.objToJson(requestMap)));

			// 请求服务器
			StringBuilder resultStrBuilder = new StringBuilder();
			int resultCode = EasyHttpClientUtils.sendRequest(url, EasyKeyUtils.DEFAULT_CHARSET, requestMap, 30000, 60000, "POST", resultStrBuilder, null);
			log.debug(String.format("easyPay code==%s result==[%s]", resultCode, resultStrBuilder.toString()));
			if(StringUtil.compareObject(resultCode, 200)) {
				msgModel.setIsSucc(true);
				msgModel.setData(resultStrBuilder.toString());
				return msgModel;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("请求服务器失败");
		return msgModel;
	}

	/**
	 * 订单批量导入海关报关
	 * @param orderList
	 * @return
	 */
	public static MsgModel<Void> pushCustomOrder(String orderNo, String tradeNo, String identityName, String identityNo, 
			String consigneePhone, Double paymentAmount, Date payDate){
		MsgModel<Void> msgModel = new MsgModel<Void> ();
		try {
			String filePath = String.format("%s/%s.csv", EasySignUtils.localFile, System.currentTimeMillis());
			FileUtil.createNewFile(new File(filePath));

			FileWriter writer = null;
			BufferedReader bf = null;
			try {
				// 订单报关csv文件头信息
				writer = new FileWriter(filePath, false);
				bf = new BufferedReader(new FileReader(EasySignUtils.orderImportHeader));
				String str;
				while ((str = bf.readLine()) != null) {
					writer.write(str + "\r\n"); 
				}

				// 订单报关信息
				List<String> dataList = new ArrayList<String>();
				dataList.add(StringUtil.null2Str(tradeNo));
				dataList.add(StringUtil.null2Str(orderNo));
				dataList.add(StringUtil.null2Str("C"));
				dataList.add(StringUtil.null2Str(identityNo));
				dataList.add(StringUtil.null2Str(identityName));
				dataList.add(StringUtil.null2Str(consigneePhone));
				dataList.add(StringUtil.nullToDoubleFormatStr(paymentAmount));
				dataList.add(DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, payDate));
				dataList.add(StringUtil.null2Str("NB"));
				dataList.add(StringUtil.null2Str(""));
				dataList.add(StringUtil.null2Str(""));
				dataList.add(StringUtil.null2Str("Y"));
				writer.write(StringUtil.strListToString(dataList, ",") + "\r\n");
				writer.flush();
			}finally {
				try {
					if(bf != null) bf.close();  
					if(writer != null) writer.close();;  
				}catch(Exception e) {
					e.printStackTrace();
				}
			}

			EasyRequestVo importRequestVo = new EasyRequestVo();
			importRequestVo.setSupplierNo(EasySignUtils.customSupplierNo);
			importRequestVo.setSubSupplierNo(EasySignUtils.customSubSupplierNo);
			importRequestVo.setCompressed("false");
			importRequestVo.setTransType("buy");
			importRequestVo.setOrderType("custom");
			importRequestVo.setRandomKey(EasyKeyUtils.getOutTradeNo());
			importRequestVo.setOrigin("SH");
			importRequestVo.setFiles(new File(filePath));
			boolean flag = EasySignUtils.fileSign(importRequestVo);  //加签文件
			if(!flag){
				msgModel.setMessage("文件签名失败！");
				msgModel.setIsSucc(false);
				return msgModel;
			}

			Map<String, Object> paramMap = new HashMap<String, Object> ();
			paramMap.put("supplierNo", importRequestVo.getSupplierNo());
			paramMap.put("subSupplierNo", importRequestVo.getSubSupplierNo());
			paramMap.put("randomKey", importRequestVo.getRandomKey());
			paramMap.put("sign", importRequestVo.getSign());
			paramMap.put("origin", importRequestVo.getOrigin());
			paramMap.put("compressed", importRequestVo.getCompressed());
			paramMap.put("transType", importRequestVo.getTransType());
			paramMap.put("orderType", importRequestVo.getOrderType());
			paramMap.put("files", importRequestVo.getFiles());
			paramMap.put("remark", importRequestVo.getRemark());

			String result =  HttpClientUtil.postFrom(custom_url, paramMap);
			log.debug(String.format("easyPay==pushCustom[%s]", result));

			JSONObject respJson = JSON.parseObject(result);
			if("0000".equals(respJson.getString("retCode"))){
				msgModel.setIsSucc(true);
				msgModel.setMessage("汇付支付报关请求成功");
				return msgModel;
			}else if("0004".equals(respJson.getString("retCode"))) {
				JSONArray jsonArray = JSONArray.parseArray(respJson.getString("data"));
				if(jsonArray != null && jsonArray.size() > 0) {
					Map<String, String> objectMap = StringUtil.jsonToHashMap(jsonArray.get(0).toString());
					if(objectMap != null 
							&& objectMap.containsKey("orderformNo")
							&& StringUtil.compareObject(objectMap.get("orderformNo"), orderNo)) {
						msgModel.setIsSucc(true);
						msgModel.setMessage("汇付支付报关请求成功");
						return msgModel;
					}
				}
			}

			msgModel.setMessage("汇付支付报关请求失败");
			msgModel.setIsSucc(false);
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();

			msgModel.setMessage("汇付支付报关请求异常");
			msgModel.setIsSucc(false);
			return msgModel;
		}
	}

	/**
	 * 购付汇订单查询
	 * @return
	 */
	public static MsgModel<Integer> queryCustomOrder(String orderNo){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		try {
			Map<String, String> params = new HashMap<>();
			params.put("supplierNo", EasySignUtils.customSupplierNo);
			params.put("subSupplierNo", EasySignUtils.customSubSupplierNo);
			params.put("randomKey", EasyKeyUtils.getOutTradeNo());
			params.put("origin", "SH");
			params.put("orderType", "custom");
			//params.put("custom", "GZ");
			params.put("orderNo", orderNo);
			params.put("page", "1");
			params.put("pageSize", "10");
			EasySignUtils.sign(params);

			Map<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-Type", "application/json");
			headerMap.put("Accept-Encoding", "zip");
			String result =  HttpClientUtil.post(custom_query_url, headerMap, StringUtil.objToJson(params));
			log.debug(String.format("easyPay==queryCustom[%s]", result));

			JSONObject respJson = JSON.parseObject(result);
			if("0000".equals(respJson.getString("retCode"))){
				//打印解密的订单数据
				String randomData = EasySignUtils.decodeResponse(respJson, EasySignUtils.customSupplierNo, "SH");  
				if(!StringUtil.isNull(randomData)) {
					JSONArray jsonArray = JSONArray.parseArray(randomData);
					Map<String, String> objectMap = StringUtil.jsonToHashMap(jsonArray.get(0).toString());
					log.debug(String.format("easyPay==queryCustom data[%s]", StringUtil.objToJson(objectMap)));
					if(StringUtil.compareObject(objectMap.get("orderFormNo"), orderNo) 
							&& StringUtil.compareObject(objectMap.get("customStatus"), 3)) {
						msgModel.setIsSucc(true);
						msgModel.setMessage("汇付支付报关成功");
						msgModel.setData(StringUtil.nullToInteger(objectMap.get("customStatus")));
						return msgModel;
					}
				}
			}

			msgModel.setMessage("汇付支付报关失败");
			msgModel.setIsSucc(false);
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();

			msgModel.setMessage("易生支付报关异常");
			msgModel.setIsSucc(false);
			return msgModel;
		}
	}

	/**
	 * 请求参数转码
	 * @param data
	 * @return
	 */
	private static String getEncode(String data){
		return EasyUtils.bytesToHexStr(EasyDesUtil.desEncode(data, DES_ENCODE_KEY));
	}
}
