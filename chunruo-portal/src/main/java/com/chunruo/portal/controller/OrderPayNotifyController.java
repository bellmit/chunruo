package com.chunruo.portal.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.chunruo.cache.portal.impl.OrderByIdCacheManager;
import com.chunruo.cache.portal.impl.OrderListByStoreIdCacheManager;
import com.chunruo.cache.portal.impl.OrderListByUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.cache.portal.impl.UserProfitByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.PaymentType;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.FileIO;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.WxSendUtil;
import com.chunruo.core.util.XmlParseUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.util.WeiXinPayUtil;

@Controller
@RequestMapping("/api/order/")
public class OrderPayNotifyController extends BaseController{
	protected final static Log log = LogFactory.getLog(OrderPayNotifyController.class);
	
	static Lock lock = new ReentrantLock();
	@Autowired
	private OrderManager orderManager;


	public static MsgModel<Map<String, List<Long>>> updateOrderPaymentSuccStatus(Order order, String transactionId, Integer paymentType, Long weChatConfigId, String paymentResponseData){
		MsgModel<Map<String, List<Long>>> msgModel = new MsgModel<Map<String, List<Long>>> ();
		// 加锁
		lock.lock();
		try {
			OrderManager orderManager = Constants.ctx.getBean(OrderManager.class);
			OrderByIdCacheManager orderByIdCacheManager = Constants.ctx.getBean(OrderByIdCacheManager.class);
			UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
			UserInfoByIdCacheManager userInfoByIdCacheManager = Constants.ctx.getBean(UserInfoByIdCacheManager.class);
			UserProfitByUserIdCacheManager userProfitByUserIdCacheManager = Constants.ctx.getBean(UserProfitByUserIdCacheManager.class);
			OrderListByStoreIdCacheManager orderListByStoreIdCacheManager = Constants.ctx.getBean(OrderListByStoreIdCacheManager.class);
			OrderListByUserIdCacheManager orderListByUserIdCacheManager = Constants.ctx.getBean(OrderListByUserIdCacheManager.class);

			//分销金额插入
			Map<String, List<Long>> listMap = orderManager.updateOrderPaymentSuccStatus(order.getOrderId(), transactionId, paymentType, weChatConfigId, paymentResponseData);
			if(listMap != null && listMap.size() > 0){
				try{
					// 更新订单缓存信息
					orderByIdCacheManager.removeSession(order.getOrderId());
					userInfoByIdCacheManager.removeSession(order.getUserId());
					orderListByStoreIdCacheManager.removeSession(order.getStoreId());
					orderListByUserIdCacheManager.removeSession(order.getUserId());
				}catch(Exception e){
					e.printStackTrace();
				}

				// 用户缓存更新
				if(listMap.containsKey("userIdList") && !CollectionUtils.isEmpty(listMap.get("userIdList"))){
					for(Long userId : listMap.get("userIdList")){
						try{
							userInfoByIdCacheManager.removeSession(userId);
							userProfitByUserIdCacheManager.removeSession(userId);
						}catch(Exception e){
							continue;
						}
					}
				}
				
				OrderItems orderItems = order.getOrderItemsList().get(0);
				UserInfo userInfo = userInfoManager.get(order.getUserId());
				if(userInfo != null 
						&& userInfo.getUserId() != null 
						&& userInfo.getOpenId() != null
						&& orderItems != null && orderItems.getItemId() != null) {
					if(StringUtil.nullToBoolean(order.getIsInvitationAgent())) {
						WxSendUtil.memberSucc(userInfo);
					}else {
						WxSendUtil.succOrder(order, userInfo, StringUtil.null2Str(orderItems.getProductName()));
					}
				}
			}

			msgModel.setData(listMap);
			msgModel.setIsSucc(true);
			return msgModel;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放锁
			lock.unlock();     
		}

		msgModel.setIsSucc(false);
		return msgModel;
	}

	/**
	 * 微信支付回调通知
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/wxpayNotify")
	public void wxpayNotify(final HttpServletRequest request, HttpServletResponse response) {
		try{
			String resultXML = FileIO.inputStream2String(request.getInputStream());
			log.debug(String.format("weChatPayNotify[XML]==>[%s]", resultXML));

			Map<String, Object> notifyObjectMap = XmlParseUtil.xmlCont2Map(resultXML);
			if(notifyObjectMap != null 
					&& notifyObjectMap.size() > 0
					&& notifyObjectMap.containsKey("return_code")
					&& StringUtil.compareObject("SUCCESS", StringUtil.null2Str(notifyObjectMap.get("return_code")))){
				String appid  = StringUtil.null2Str(notifyObjectMap.get("appid"));
				String outTradeNo = StringUtil.null2Str(notifyObjectMap.get("out_trade_no"));
				String sign = StringUtil.null2Str(notifyObjectMap.get("sign"));

				//根据trade_type判断，是APP支付或H5微页面支付
				WeChatAppConfig weChatAppConfig = Constants.WECHAT_APP_ID_MAP.get(appid);
				if(weChatAppConfig == null || weChatAppConfig.getAppId() == null){
					this.writeTextResponse(response, "<xml><return_code><![CDATA[FAIL]]></return_code></xml>");
					return;
				}

				//标识微信支付 1 或者 微信公众号支付 5
				notifyObjectMap.remove("sign");
				String notifySign = WeiXinPayUtil.getNotifySignString(notifyObjectMap, weChatAppConfig.getSecretKey());
				// 验证签名是否正确
				if(StringUtil.compareObject(notifySign, sign)){
					Order order = this.orderManager.getOrderByOrderNo(outTradeNo);
					if(order != null && order.getOrderId() != null){
						//根据支付流水号获取支付信息
						MsgModel<String> msModel = WeiXinPayUtil.getQueryPayInfo(weChatAppConfig, null, outTradeNo);
						int orderTotal = WeiXinPayUtil.orderAmountToBranch(order.getPayAmount());
						if(StringUtil.nullToBoolean(msModel.getIsSucc()) && StringUtil.compareObject(msModel.getData(), orderTotal)){
							OrderPayNotifyController.updateOrderPaymentSuccStatus(order, msModel.getTransactionId(), PaymentType.PAYMENT_TYPE_WECHAT, weChatAppConfig.getConfigId(), msModel.getPaymentBody());
							this.writeTextResponse(response, "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
							return;
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		this.writeTextResponse(response, "<xml><return_code><![CDATA[FAIL]]></return_code></xml>");
	}

}
