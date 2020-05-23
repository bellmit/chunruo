package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chunruo.cache.portal.impl.RefundByOrderItemIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.RefundReason;
import com.chunruo.core.model.RollingNotice;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.OrderItemUtil;
import com.chunruo.portal.util.OrderUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.util.UserAddressUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 订单详情
 * @author chunruo
 */
public class OrderDetailTag extends BaseTag {

	public TagModel<Order> getData(Object orderId_1, Object isNeedCheckPayment_1){
		Long orderId = StringUtil.nullToLong(orderId_1);
		TagModel<Order> tagModel = new TagModel<Order> ();
		try{
			RefundByOrderItemIdCacheManager refundByOrderItemIdCacheManager = Constants.ctx.getBean(RefundByOrderItemIdCacheManager.class);

			// 检查用户是否登录
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_SUCCESS);
				tagModel.setMsg("用户未登录");
				return tagModel;
			}

			// 检查订单是否有效
			MsgModel<Order> orderModel = OrderUtil.getOrderByOrderId(orderId);
			if(!StringUtil.nullToBoolean(orderModel.getIsSucc())){
				tagModel.setCode(PortalConstants.CODE_ERROR);
				tagModel.setMsg(orderModel.getMessage());
				return tagModel;
			}
			
			// 订单信息
			Order order = orderModel.getData();
			if(StringUtil.nullToBoolean(order.getIsShareBuy())) {
				if(!StringUtil.compareObject(userInfo.getUserId(), order.getUserId())
						&& !StringUtil.compareObject(userInfo.getUserId(), order.getStoreId())) {
					tagModel.setCode(PortalConstants.CODE_ERROR);
					tagModel.setMsg("无权限查看该订单");
					return tagModel;
				}
			}else {
				if(!StringUtil.compareObject(userInfo.getUserId(), order.getUserId())){
					tagModel.setCode(PortalConstants.CODE_ERROR);
					tagModel.setMsg("无权限查看该订单");
					return tagModel;
				}
			}

			// 退款中
			List<Integer> refundWaitStatusList = new ArrayList<Integer> ();
			refundWaitStatusList.add(Refund.REFUND_STATUS_WAIT);
			refundWaitStatusList.add(Refund.REFUND_STATUS_SUCCESS);
			refundWaitStatusList.add(Refund.REFUND_STATUS_RECEIPT);

			// 退款关闭
			List<Integer> refundCloseStatusList = new ArrayList<Integer> ();
			refundCloseStatusList.add(Refund.REFUND_STATUS_REFUSE);
			refundCloseStatusList.add(Refund.REFUND_STATUS_TIMEOUT);

			List<OrderItems> orderItemsList = order.getOrderItemsList();
			for(OrderItems orderItem : orderItemsList){
				orderItem.setRefundStatus(Refund.REFUND_STATUS_INIT);

				// 检查是否有退款退货记录
				Refund refund = refundByOrderItemIdCacheManager.getSession(orderItem.getItemId());
				if(refund != null && refund.getRefundId() != null){
					orderItem.setRefundStatus(refund.getRefundStatus());

					if(refundWaitStatusList.contains(refund.getRefundStatus())){
						//退款中
						orderItem.setRufundStatusName("退款中");
					}else if(refundCloseStatusList.contains(refund.getRefundStatus())){
						//退款关闭
						orderItem.setRufundStatusName("退款关闭");
					}else if(StringUtil.compareObject(Refund.REFUND_STATUS_COMPLETED, refund.getRefundStatus())){
						//退款完成
						orderItem.setRufundStatusName("退款完成");
					}
				}else{
					// 检查订单申请退款退货是否超时
					if(StringUtil.compareObject(order.getStatus(), OrderStatus.OVER_ORDER_STATUS) && StringUtil.nullToBoolean(order.getIsCheck())){
						// 申请请求超时
						orderItem.setRefundStatus(Refund.REFUND_STATUS_TIMEOUT);
					}
				}

				// 默认设置商品正常出售状态
				orderItem.setIsSoldout(Constants.NO);

				// 已下单未支付,需要判断批发市场是否有货
				if (StringUtil.compareObject(order.getStatus(), OrderStatus.NEW_ORDER_STATUS)) {
					// 非秒杀秒杀订单
					if(!StringUtil.nullToBoolean(order.getIsSeckillProduct())){
						// 订单待支付的情况下,显示商品是否有库存
						MsgModel<Product> msgModel = ProductUtil.getProductByUserLevel(orderItem.getProductId(), userInfo, true);
						if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
							// 判断商品状态不是上架状态,购物车统一任务是售罄状态
							orderItem.setIsSoldout(Constants.YES);
						}
					}
				}
			}

			// 组合商品订单合并
			if (StringUtil.nullToBoolean(order.getIsGroupProduct())) {
				List<OrderItems> orderItemList = OrderItemUtil.mergeGroupItems(order.getOrderItemsList());
				if (!StringUtil.compareObject(order.getStatus(), OrderStatus.NEW_ORDER_STATUS)) {
					// 非等待支付组合没有抢光状态
					if(orderItemList != null && orderItemList.size() > 0){
						for(OrderItems orderItem : orderItemList){
							if(StringUtil.nullToBoolean(orderItem.getIsGroupProduct())){
								orderItem.setIsSoldout(Constants.NO);
							}
						}
					}
				}
				order.setOrderItemsList(orderItemList);
			}
			
			// 订单用户全地址
			order.setFullAddress(UserAddressUtil.getFullAddressInfo(order));
			Map<String,Object> dataMap = new HashMap<String, Object>();
			// 滚动通知信息
			RollingNotice rollingNotice = Constants.ROLLING_NOTICE_MAP.get(RollingNotice.ROLLING_NOTICE_FRAUD);
			if(rollingNotice != null && rollingNotice.getNoticeId() != null){
				dataMap.put("rollingNotice",StringUtil.null2Str(rollingNotice.getContent()));
			}
			
			
			dataMap.put("storeMobile", StringUtil.null2Str(userInfo.getMobile()));
			dataMap.put("cancelReasonList", OrderDetailTag.getOrderCancelReasonList());
			tagModel.setDataMap(dataMap);
			tagModel.setData(order);
		}catch(Exception e){
			e.printStackTrace();
		}

		tagModel.setCode(PortalConstants.CODE_SUCCESS);

		tagModel.setMsg("请求成功");
		return tagModel;
	}
	
	/**
	 * 取消订单原因
	 * @return
	 */
	public static List<RefundReason> getOrderCancelReasonList(){
		List<RefundReason> reasonList = new ArrayList<RefundReason>();
		//非退款、退货原因
		List<Integer> reasonTypeList = new ArrayList<Integer>();
		reasonTypeList.add(RefundReason.REFUND_REASON_CANCEL);
		reasonTypeList.add(RefundReason.REFUND_REASON_REMIND);
		
		Map<Long, RefundReason> reasonMap = Constants.REFUND_REASON_MAP;
		if(reasonMap != null && reasonMap.size() > 0) {
			for (Map.Entry<Long, RefundReason> entry : reasonMap.entrySet()) {
				if(reasonTypeList.contains(entry.getValue().getReasonType())) {
					reasonList.add(entry.getValue());
				}
			}
		}
		return reasonList;
	}
}
