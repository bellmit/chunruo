package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.chunruo.core.Constants;
import com.chunruo.core.Constants.PaymentType;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.RefundReason;
import com.chunruo.core.model.RefundRequestRecord;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.RefundRequestRecordManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;

public class RefundUtil {
	public final static transient Log log = LogFactory.getLog(RefundUtil.class);
	
	/**
	 * 创建退款订单
	 * @param userInfo
	 * @param order
	 * @param request
	 * @param reason
	 * @return
	 */
	public static MsgModel<Map<String, Object>> createRefundRequest(Order order, RefundReason reason, String requestURL){
		MsgModel<Map<String, Object>> msgModel = new MsgModel<Map<String, Object>> ();
		// 针对同一用户加公平锁，防止用户频繁点击。
		RedissonClient redissonClient = Constants.ctx.getBean(RedissonClient.class);
		RLock lock = redissonClient.getFairLock(String.format("user_cancel_order_%s", order.getStoreId()));
		try {
			//订单未推送到erp
			String refundNumber = "T" + CoreInitUtil.getRandomNo();   //退款编号
			//直接退款
			MsgModel<Double> refundModel = RefundUtil.getRefundInfo(order, refundNumber, requestURL);
			if(StringUtil.nullToBoolean(refundModel.getIsSucc())) {
				//退款成功（生成退款记录）
				MsgModel<Refund> rsgModel = RefundUtil.saveRefund(refundNumber, order, reason.getReasonId());
				if(StringUtil.nullToBoolean(rsgModel.getIsSucc())) {
					// 订单关闭
					String cancelReason = String.format("用户手动取消订单,原因[%s]", StringUtil.null2Str(reason.getReason()));
					OrderUtil.orderCloseStatus(order, order.getUserId(), cancelReason, rsgModel.getData(), reason.getReasonId());
					
					// 检查用户是否快捷支付
					boolean isQuickPayment = false;
					StringBuffer strBuffer = new StringBuffer("取消成功");
					List<Integer> quickPaymentList = new ArrayList<Integer> ();
					quickPaymentList.add(PaymentType.PAYMENT_TYPE_HUIFU);
					if(quickPaymentList.contains(order.getPaymentType())) {
						isQuickPayment = true;
						strBuffer.append(",已提交到支付机构退款(支付退款T+3工作日之内到账,如有疑问请联系客服)。");
					}

					Map<String, Object> resultMap = new HashMap<String, Object> ();
					resultMap.put("isQuickPayment", StringUtil.booleanToInt(isQuickPayment));
					resultMap.put("status", Constants.OrderStatus.CANCEL_ORDER_STATUS);
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
					resultMap.put(PortalConstants.MSG, strBuffer.toString());
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					msgModel.setIsSucc(true);
					msgModel.setData(resultMap);
					return msgModel;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "退款失败，请联系客服");
		msgModel.setIsSucc(false);
		msgModel.setData(resultMap);
		return msgModel;
	}
	
	/**
	 * 请求支付机构退款
	 * @param order
	 * @param refundNumber
	 * @param requestURL
	 * @return
	 */
	public static MsgModel<Double> getRefundInfo(Order order, String refundNumber, String requestURL) {
		MsgModel<Double> msgModel = new MsgModel<Double>();
		try {
			if(order != null 
					&& order.getOrderId() != null
					&& StringUtil.nullToBoolean(order.getIsPaymentSucc())) {
				String tradeNo = StringUtil.null2Str(order.getTradeNo());
				String orderNo = StringUtil.null2Str(order.getOrderNo());       
				Double refundFee = StringUtil.nullToDoubleFormat(order.getPayAmount());   //退款金额
				// 微信退款
				WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(StringUtil.nullToLong(order.getWeChatConfigId()));
				//检查此退款单是否已退过款
				MsgModel<Boolean> xmsgModel = WeiXinPayUtil.checkRefundInfo(weChatAppConfig, tradeNo, refundNumber, orderNo);
				if(StringUtil.nullToBoolean(xmsgModel.getIsSucc()) && StringUtil.nullToBoolean(xmsgModel.getData())) {
					//已退款成功
					msgModel.setIsSucc(true);
					msgModel.setMessage("此单已退款");
					return msgModel;
				}
				log.info(String.format("订单号=%s发起微信退款，退款单号=%s", order.getOrderNo(),refundNumber));
				//发起退款
				RefundUtil.saveRefundRequestRecord(tradeNo, orderNo, refundNumber, refundFee);
				return WeiXinPayUtil.getQueryRefundInfo(weChatAppConfig, tradeNo, orderNo, refundFee, refundFee, refundNumber);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("退款失败");
		return msgModel;
	}
	
	/**
	 * 保存退款请求记录
	 * @param tradeNo
	 * @param orderNo
	 * @param refundNumber
	 * @param refundAmount
	 */
	public static void saveRefundRequestRecord(String tradeNo, String orderNo, String refundNumber, Double refundAmount) {
		try {
			RefundRequestRecordManager refundRequestRecordManager = Constants.ctx.getBean(RefundRequestRecordManager.class);
			RefundRequestRecord refundRequestRecord = new RefundRequestRecord();
			refundRequestRecord.setOrderNo(orderNo);
			refundRequestRecord.setRefundNumber(refundNumber);
			refundRequestRecord.setTradeNo(tradeNo);
			refundRequestRecord.setRefundAmount(refundAmount);
			refundRequestRecord.setCreateTime(DateUtil.getCurrentDate());
			refundRequestRecord.setUpdateTime(refundRequestRecord.getCreateTime());
			refundRequestRecordManager.save(refundRequestRecord);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 保存退款记录
	 * @param refundNumber
	 * @param order
	 * @param reasonId
	 * @return
	 */
	public static MsgModel<Refund> saveRefund(String refundNumber, Order order, Long reasonId){
		MsgModel<Refund> msgModel = new MsgModel<Refund>();
		try {
			if(order != null && order.getOrderId() != null) {
				log.info(String.format("[订单号%s发起退款]", order.getOrderNo()));
				//退款成功（生成退款记录）
				Refund refund = new Refund();
				refund.setRefundNumber(refundNumber); // 退款单号
				refund.setOrderId(order.getOrderId()); // 订单ID
				refund.setRefundCount(order.getProductNumber());
				refund.setIsGroupProduct(StringUtil.nullToBoolean(order.getIsGroupProduct()));
				refund.setProductPrice(StringUtil.nullToDoubleFormatStr(order.getProductAmount())); // 商品原价格
				refund.setTotalAmount(StringUtil.nullToDoubleFormatStr(order.getPayAmount())); // 商品总金额(含税)
				refund.setRefundType(Refund.REFUND_TYPE_CANCEL); // 售后类型 3:订单取消
				refund.setRefundAmount(StringUtil.nullToDoubleFormatStr(order.getPayAmount())); // 申请退货商品总金额
				refund.setReasonId(reasonId); // 申请退货原因ID
				refund.setUserId(order.getUserId()); // 用户ID
				refund.setStoreId(order.getStoreId());//店铺ID
				refund.setRefundStatus(Refund.REFUND_STATUS_COMPLETED); // 退款状态
				refund.setIsCurrentTask(true);
				refund.setIsReceive(false); // 是否收到商品
				refund.setCreateTime(DateUtil.getCurrentDate()); // 创建时间
				refund.setUpdateTime(refund.getCreateTime()); // 更新时间
				
				msgModel.setIsSucc(true);
				msgModel.setData(refund);
				return msgModel;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}
}
