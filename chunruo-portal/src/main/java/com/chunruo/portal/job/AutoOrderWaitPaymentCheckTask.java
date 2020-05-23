package com.chunruo.portal.job;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.Constants.PaymentType;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.controller.OrderPayNotifyController;
import com.chunruo.portal.util.WeiXinPayUtil;

/**
 * 未支付订单检查
 * 
 */
@Component
public class AutoOrderWaitPaymentCheckTask {
	protected final transient Log log = LogFactory.getLog(getClass());
	@Autowired
	private OrderManager orderManager;

	@Scheduled(cron="0 0/5 * * * ?")
	public void execute(){
		log.info("AutoOrderWaitPaymentCheckTask ........");
		int size = 0;
		try{
			    List<Order> orderList = this.orderManager.getOrderListByStatus(OrderStatus.NEW_ORDER_STATUS, false);
                if(orderList != null && !orderList.isEmpty()) {
                	for(Order order : orderList) {
                		try{

    						// 检查订单是否有效
    						if(order == null 
    								|| order.getOrderId() == null 
    								|| StringUtil.nullToBoolean(order.getIsDelete())){
    							return;
    						}

    						// 检查订单是否支付成功
    						boolean isPaymentSucc = StringUtil.nullToBoolean(order.getIsPaymentSucc());
    						if(!StringUtil.nullToBoolean(isPaymentSucc)){
    							BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
									@Override
									public void run() {
										try{
											Integer paymentType = 0;
											Long weChatConfigId = 1L;
											String orderNo = StringUtil.null2Str(order.getOrderNo());
										
											
											StringBuffer strBuffer = new StringBuffer (String.format("OrderWaitPaymentCheck[orderNo=%s,paymentType=%s]", order.getOrderNo(), paymentType));
											if(StringUtil.compareObject(paymentType, PaymentType.PAYMENT_TYPE_WECHAT)){
												//微信支付
												WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(weChatConfigId);
												if(weChatAppConfig != null && weChatAppConfig.getConfigId() != null){
													strBuffer.append("weChatConfigId=" + weChatConfigId);
													
													//根据支付流水号获取支付信息
													MsgModel<String> msModel = WeiXinPayUtil.getQueryPayInfo(weChatAppConfig, null, orderNo);
													if(!StringUtil.nullToBoolean(msModel.getIsSucc())){
														strBuffer.append(",支付获取失败" + msModel.getMessage());
													}else {
														String tradeNo = StringUtil.null2Str(msModel.getTransactionId());
														strBuffer.append(",tradeNo=" + tradeNo);
														// 普通支付
														int orderTotal = WeiXinPayUtil.orderAmountToBranch(order.getPayAmount());
														if(StringUtil.compareObject(msModel.getData(), orderTotal)) {
															OrderPayNotifyController.updateOrderPaymentSuccStatus(order, tradeNo, PaymentType.PAYMENT_TYPE_WECHAT, weChatAppConfig.getConfigId(), msModel.getPaymentBody());
														}
													}
												}
											}
											log.debug(strBuffer.toString());
										}catch(Exception e){
											e.printStackTrace();
										}
									}
								});
    						}
    					}catch(Exception e){
    						e.printStackTrace();
    					}
                	}
                }
				
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("AutoOrderWaitPaymentCheckTask  === " + String.format("[size=%s]", size));
	}
}
