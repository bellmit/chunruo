package com.chunruo.core.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.RefundManager;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

/**
 * 订单退款
 * @author chunruo
 */
public class RefundUtil {
	public final static transient Log log = LogFactory.getLog(RefundUtil.class);

	public static MsgModel<Double> getRefundInfo(Refund refund, double refundFee) {
		MsgModel<Double> msgModel = new MsgModel<Double>();
		OrderManager orderManager = Constants.ctx.getBean(OrderManager.class);
		try {
			// 财务同意退款申请
			Order order = orderManager.getOrderByOrderId(StringUtil.nullToLong(refund.getOrderId()));
			if (order == null || order.getOrderId() == null) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("错误,订单不存在");
				return msgModel;
			}

			String tradeNo = StringUtil.null2Str(order.getTradeNo());
			String orderNo = StringUtil.null2Str(order.getOrderNo());
			String refundNumber = StringUtil.nullToString(refund.getRefundNumber());
			// 微信退款
			WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(StringUtil.nullToLong(order.getWeChatConfigId()));
			//检查此退款单是否已退过款
			MsgModel<Boolean> xmsgModel = WeiXinPayUtil.checkRefundInfo(weChatAppConfig, tradeNo, refundNumber, orderNo);
			if(StringUtil.nullToBoolean(xmsgModel.getIsSucc())
					&& StringUtil.nullToBoolean(xmsgModel.getData())) {
				//已退款成功
				msgModel.setIsSucc(true);
				msgModel.setMessage("此单已退款");
				return msgModel;
			}

			//发起退款
			log.info(String.format("微信支付退款[订单号=%s,退款单号=%s,退款金额=%s]", order.getOrderNo(), refundNumber, refundFee));
			return WeiXinPayUtil.getQueryRefundInfo(weChatAppConfig, tradeNo, orderNo, refundFee,StringUtil.nullToDouble(order.getPayAmount()),refundNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("退款失败");
		return msgModel;
	}

	/**
	 * 退还整笔订单金额
	 * @param order
	 * @param refundNumber
	 * @return
	 */
	public static MsgModel<Double> getRefundInfo(Order order, String refundNumber) {
		MsgModel<Double> msgModel = new MsgModel<Double>();
		try {
			if(order != null && order.getOrderId() != null) {
				String tradeNo = StringUtil.null2Str(order.getTradeNo());
				String orderNo = StringUtil.null2Str(order.getOrderNo());       
				Double refundFee = StringUtil.nullToDoubleFormat(order.getPayAmount());   //退款金额
				// 微信退款
				WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(StringUtil.nullToLong(order.getWeChatConfigId()));
				//检查此退款单是否已退过款
				MsgModel<Boolean> xmsgModel = WeiXinPayUtil.checkRefundInfo(weChatAppConfig,tradeNo,refundNumber,orderNo);
				if(StringUtil.nullToBoolean(xmsgModel.getIsSucc())
						&& StringUtil.nullToBoolean(xmsgModel.getData())) {
					//已退款成功
					msgModel.setIsSucc(true);
					msgModel.setMessage("此单已退款");
					return msgModel;
				}

				//发起退款
				log.info(String.format("微信支付退款[订单号=%s,退款单号=%s,退款金额=%s]", order.getOrderNo(), refundNumber, refundFee));
				return WeiXinPayUtil.getQueryRefundInfo(weChatAppConfig, tradeNo,orderNo, refundFee, refundFee, refundNumber);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("退款失败");
		return msgModel;
	}



	/**
	 * 导出退款及退货完成的
	 * @return
	 */
	public static List<Object[]> refundExport(String beginTime, String endTime) {
		List<Object[]> mapList = new ArrayList<Object[]>();

		try {
			// 检查时间是否有效
			boolean isEffectiveTime = true;
			if(!DateUtil.isEffectiveTime(DateUtil.dateFormat, StringUtil.null2Str(beginTime))
					|| !DateUtil.isEffectiveTime(DateUtil.dateFormat, StringUtil.null2Str(endTime))){
				isEffectiveTime = false;
			}
			if(isEffectiveTime) {
				RefundManager refundManager = Constants.ctx.getBean(RefundManager.class);
				StringBuffer sqlBuf =new StringBuffer();
				sqlBuf.append("SELECT jo.order_no,jr.refund_type,jr.create_time,jrr.reason,jr.refund_amount,jr.refund_status,jr.completed_time,jr.remarks,jr.refund_number,jr.store_id,jui.mobile,jr.product_name,jr.refund_count ");
				sqlBuf.append(" FROM jkd_refund jr,jkd_order jo,jkd_refund_reason jrr,jkd_user_info jui");
				sqlBuf.append(" WHERE jr.store_id = jui.user_id and is_current_task = true and jr.order_id=jo.order_id AND jr.reason_id=jrr.reason_id and jr.refund_type in(1,2) and jr.create_time between '"+beginTime+"' and '"+endTime+"'");
				sqlBuf.append(" ORDER BY jr.create_time desc");
				mapList = refundManager.querySql(sqlBuf.toString());
				if(mapList != null && mapList.size() > 0) {
					for(Object[] object: mapList) {
						object[2]=StringUtil.nullToString(object[2]).substring(0, 19);
						object[6]=StringUtil.isNull(object[6]) ? "" : StringUtil.null2Str(object[6]).substring(0, 19);
						if(StringUtil.compareObject(object[1],Refund.REFUND_TYPE_MONEY)) {
							object[1]="退款";
						}else {
							object[1]="退货退款";
						}

						Integer refundStatus = StringUtil.nullToInteger(object[5]);
						object[5] = StringUtil.null2Str(RefundUtil.getRefundStatus(refundStatus));
					}
				}
			}


		}catch(Exception e) {
			e.printStackTrace();
		}
		return mapList;
	}


	private static String getRefundStatus(int status) {  
		String statusStr = null;  
		switch (status) {  
		case 1:  
			statusStr = "客服审核";  
			break;  
		case 2:  
			statusStr = "审核被拒";  
			break;  
		case 3:  
			statusStr = "退货审核通过";  
			break;  
		case 4:  
			statusStr = "平台收货";  
			break;  
		case 5:  
			statusStr = "退款完成";  
			break;  
		case 6:  
			statusStr = "申请已超时";  
			break;  
		case 7:  
			statusStr = "客服主管审核";  
			break;  
		case 8:  
			statusStr = "财务审核";  
			break;  
		case 9:  
			statusStr = "被驳回";  
			break; 
		}  
		return statusStr;  
	}  

}
