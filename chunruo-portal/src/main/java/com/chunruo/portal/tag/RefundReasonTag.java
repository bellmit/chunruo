package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chunruo.cache.portal.impl.OrderByIdCacheManager;
import com.chunruo.cache.portal.impl.RefundByOrderItemIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.RefundReason;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.OrderItemUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.vo.TagModel;

public class RefundReasonTag extends BaseTag {

	public TagModel<List<RefundReason>> getData(Object orderId_1, Object itemId_1) {
		Long orderId = StringUtil.nullToLong(orderId_1);
		Long itemId = StringUtil.nullToLong(itemId_1);
		TagModel<List<RefundReason>> tagModel = new TagModel<List<RefundReason>>();
		try{
			final OrderByIdCacheManager orderByIdCacheManager = Constants.ctx.getBean(OrderByIdCacheManager.class);
			RefundByOrderItemIdCacheManager refundByOrderItemIdCacheManager = Constants.ctx.getBean(RefundByOrderItemIdCacheManager.class);
			OrderItemsManager orderItemsManager = Constants.ctx.getBean(OrderItemsManager.class);
			
			final UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}
			
			Order order = orderByIdCacheManager.getSession(orderId);
			if (order == null 
					|| order.getOrderItemsList() == null 
					|| order.getOrderItemsList().size() == 0) {
				tagModel.setCode(PortalConstants.CODE_ERROR);
				tagModel.setMsg("未找到对应订单");
				return tagModel;
			}
			
			List<Integer> refundStatusList = new ArrayList<>();
			refundStatusList.add(Refund.REFUND_STATUS_WAIT);
			refundStatusList.add(Refund.REFUND_STATUS_SUCCESS);
			refundStatusList.add(Refund.REFUND_STATUS_RECEIPT);
			refundStatusList.add(Refund.REFUND_STATUS_COMPLETED);
			
			int result = 0;
			OrderItems realItems = null;
			List<OrderItems> orderItemList = OrderItemUtil.mergeGroupItems(order.getOrderItemsList());
			if(orderItemList != null && orderItemList.size() > 0){
				for (OrderItems items : orderItemList) {
					Refund refund = refundByOrderItemIdCacheManager.getSession(items.getItemId());
					if (refund != null 
							&& refund.getRefundId() != null
							&& refundStatusList.contains(refund.getRefundStatus())){
						result++;
					}
					
					//找到对应的订单item
					if (StringUtil.compareObject(items.getItemId(), itemId)) {
						// 普通订单商品
						realItems = items;
					}
				}
			}
			
			// 检查退款订单商品是否有效
			if(realItems == null || realItems.getItemId() == null){
				tagModel.setCode(PortalConstants.CODE_ERROR);
				tagModel.setMsg("未找到对应订单");
				return tagModel;
			}else if(orderItemsManager.get(realItems.getItemId()) == null){
				tagModel.setCode(PortalConstants.CODE_ERROR);
				tagModel.setMsg("未找到对应订单");
				return tagModel;
			}
			
			//非退款、退货原因
			List<Integer> reasonTypeList = new ArrayList<Integer>();
			reasonTypeList.add(RefundReason.REFUND_REASON_CANCEL);
			reasonTypeList.add(RefundReason.REFUND_REASON_REMIND);
			
			List<RefundReason> reasonList = new ArrayList<RefundReason> ();
			Map<Long, RefundReason> refundReasonMap = Constants.REFUND_REASON_MAP;
			for (Map.Entry<Long, RefundReason> entry : refundReasonMap.entrySet()) {
				if(reasonTypeList.contains(entry.getValue().getReasonType())) {
				    //取消订单原因
					continue;
				}
				RefundReason realReason = new RefundReason();
				realReason.setReason(entry.getValue().getReason());
				realReason.setReasonId(entry.getValue().getReasonId());
				realReason.setReasonType(entry.getValue().getReasonType());
				
				//未发货
				boolean sendStatus = false;
				if (order.getStatus().equals(Constants.OrderStatus.UN_DELIVER_ORDER_STATUS)) {
					sendStatus = true;
				}
				
				// 检查是否最后一个订单退款
				boolean isLastRefund = false;
				// 是否退还税费
				boolean isRefundTax = false; 
				if(StringUtil.nullToBoolean(sendStatus) || StringUtil.compareObject(realReason.getReasonType(), 2)){
					if(result == (orderItemList.size() - 1)) {
						//最后一件商品，且未发货，或者type=2，退运费
						isLastRefund = true;
					}
					//未发货或者type=2退税费
					isRefundTax = true;
				}
				
				Double amount = StringUtil.nullToDouble(realItems.getAmount());
				Double tax = StringUtil.nullToDouble(realItems.getTax());
				Double preferentialAmount = StringUtil.nullToDouble(realItems.getPreferentialAmount());
				Double refundAmount = DoubleUtil.sub(amount , preferentialAmount);

				if (StringUtil.nullToBoolean(isLastRefund)) {
					// 退款返还邮费
					refundAmount = DoubleUtil.add(refundAmount, DoubleUtil.add(StringUtil.nullToDouble(order.getPostage()), StringUtil.nullToDouble(order.getPostageTax())));
				}
				if(StringUtil.nullToBoolean(isRefundTax)) {
					//退款返还税费
					refundAmount = DoubleUtil.add(refundAmount, tax);
				}
				
				//赠品
				if(StringUtil.nullToBoolean(realItems.getIsRechargeProductCoupon())) {
					//赠品退还税费
					if(!isRefundTax) {
						refundAmount = DoubleUtil.add(refundAmount, DoubleUtil.divide(tax, StringUtil.nullToDouble(realItems.getQuantity())));
					}
					if( StringUtil.compareObject(StringUtil.nullToInteger(order.getProductNumber()), 1)) {
						refundAmount = StringUtil.nullToDoubleFormat(order.getPayAmount());	
					}
				}
				
				realReason.setAmount(StringUtil.nullToDoubleFormat(refundAmount));
				reasonList.add(realReason);
			}
			
			
			// 退款标签
			Map<String, Object> dataMap = new HashMap<String, Object> ();
			dataMap.put("productName", StringUtil.null2Str(realItems.getProductName()));
			dataMap.put("productTags", StringUtil.null2Str(realItems.getProductTags()));
			dataMap.put("productImage",StringUtil.null2Str(realItems.getProductImagePath()));
			dataMap.put("quantity", StringUtil.nullToInteger(realItems.getQuantity()));
			dataMap.put("notice", "本商品支持7天无理由退货,自商品签收之日起7日内;");
			//商品类型
			Integer productType = ProductUtil.getProductType(StringUtil.nullToLong(realItems.getWareHouseId()));
			if(!StringUtil.compareObject(productType, GoodsType.GOODS_TYPE_COMMON)) {
				dataMap.put("notice", "本商品不支持7天无理由退货;");
			}
			tagModel.setDataMap(dataMap);
			tagModel.setCode(PortalConstants.CODE_SUCCESS);
			tagModel.setMsg("操作成功");
			tagModel.setData(reasonList);
			return tagModel;
		}catch(Exception e){
			e.printStackTrace();
		}
	
		tagModel.setCode(PortalConstants.CODE_ERROR);
		tagModel.setMsg("请求异常,稍后访问");
		return tagModel;
	}
}
