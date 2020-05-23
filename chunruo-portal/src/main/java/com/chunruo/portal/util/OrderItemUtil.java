package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections.CollectionUtils;

import com.chunruo.cache.portal.impl.RefundByOrderItemIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.Refund;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;

public class OrderItemUtil {

	/**
	 * 订单明细 关联商品信息返回
	 * @param orderItems
	 */
	public static List<OrderItems> getOrderItemProductInfo(List<OrderItems> orderItems){
		// 退款中
		List<Integer> refundWaitStatusList = new ArrayList<Integer> ();
		refundWaitStatusList.add(Refund.REFUND_STATUS_WAIT);
		refundWaitStatusList.add(Refund.REFUND_STATUS_SUCCESS);
		refundWaitStatusList.add(Refund.REFUND_STATUS_RECEIPT);

		// 退款关闭
		List<Integer> refundCloseStatusList = new ArrayList<Integer> ();
		refundCloseStatusList.add(Refund.REFUND_STATUS_REFUSE);
		refundCloseStatusList.add(Refund.REFUND_STATUS_TIMEOUT);

		if(!CollectionUtils.isEmpty(orderItems)){
			RefundByOrderItemIdCacheManager refundByOrderItemIdCacheManager = Constants.ctx.getBean(RefundByOrderItemIdCacheManager.class);
			for(OrderItems orderItem : orderItems){
				Refund refund = refundByOrderItemIdCacheManager.getSession(orderItem.getItemId());
				if (refund != null && refund.getRefundId() != null) {
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
				}
			}
		}
		return orderItems;
	}

	/**
	 * 组合商品订单合并
	 * @param order
	 * @return
	 */
	public static List<OrderItems> mergeGroupItems(List<OrderItems> orderItemsList){
		List<OrderItems> resultItemsList = new ArrayList<OrderItems>(); 
		try {
			if (orderItemsList != null && orderItemsList.size() > 0) {
				// 集合所有组合商品订单
				Map<String, List<OrderItems>> groupItemMap = new HashMap<String, List<OrderItems>>();
				for (OrderItems orderItem : orderItemsList) {
					if(StringUtil.nullToBoolean(orderItem.getIsGiftProduct())) {
						continue;
					}
					if(StringUtil.nullToBoolean(orderItem.getIsGroupProduct())){
						String groupUniqueBatch = StringUtil.null2Str(orderItem.getGroupUniqueBatch());
						if(groupItemMap.containsKey(groupUniqueBatch)){
							groupItemMap.get(groupUniqueBatch).add(orderItem);
						}else{
							List<OrderItems> list = new ArrayList<OrderItems> ();
							list.add(orderItem);
							groupItemMap.put(groupUniqueBatch, list);
						}
					}else{
						resultItemsList.add(orderItem);
					}
				}

				// 同一组合商品订单合并
				if(groupItemMap != null && groupItemMap.size() > 0){
					for (Entry<String, List<OrderItems>> entry : groupItemMap.entrySet()) {
						Long groupProductId = null;
						OrderItems orderItem = null;
						Double groupTotalPrice = 0.0D;
						Double groupTotalAmount = 0.0D;
						Double groupTotalProfit = 0.0D;         //规整分销收益
						Double groupTotalTopProfit = 0.0D;      //规格上级收益
						Double groupTotalPreferentialAmount = 0.0D;//优惠金额
						Double groupTotalTaxAmount = 0.0D;         //税费金额
						StringBuffer strBuffer = new StringBuffer ();
						List<OrderItems> list = entry.getValue();
						for(OrderItems item : list){
							// 随机一个子订单商品未副本
							if(StringUtil.nullToBoolean(item.getIsMainGroupItem())){
								try{
									orderItem = item.clone();
								}catch(Exception e){
									e.printStackTrace();
								}
							}

							// 组合商品的合并信息
							groupProductId = StringUtil.nullToLong(item.getGroupProductId());
							groupTotalPrice = DoubleUtil.add(groupTotalPrice, StringUtil.nullToDouble(item.getPrice()));
							groupTotalAmount = DoubleUtil.add(groupTotalAmount, StringUtil.nullToDouble(item.getAmount()));
							groupTotalProfit = DoubleUtil.add(groupTotalProfit, StringUtil.nullToDouble(item.getProfit()));
							groupTotalTopProfit = DoubleUtil.add(groupTotalTopProfit, StringUtil.nullToDouble(item.getTopProfit()));
							groupTotalPreferentialAmount = DoubleUtil.add(groupTotalPreferentialAmount, StringUtil.nullToDouble(item.getPreferentialAmount()));
							groupTotalTaxAmount = DoubleUtil.add(groupTotalTaxAmount, StringUtil.nullToDouble(item.getTax()));
							strBuffer.append(String.format("%s*%s+", item.getProductTags(), item.getSaleTimes()));
						}

						// 组合商品缺少关键主退款订单itemId
						if(orderItem == null || orderItem.getItemId() == null){
							continue;
						}

						orderItem.setIsGroupProduct(true);
						orderItem.setPrice(groupTotalPrice);
						orderItem.setAmount(groupTotalAmount);
						orderItem.setProfit(groupTotalProfit);
						orderItem.setTopProfit(groupTotalTopProfit);
						orderItem.setPreferentialAmount(groupTotalPreferentialAmount);
						orderItem.setTax(groupTotalTaxAmount);
						orderItem.setProductId(orderItem.getGroupProductId());
						orderItem.setProductTags(strBuffer.deleteCharAt(strBuffer.length() - 1).toString());
						orderItem.setQuantity(StringUtil.nullToInteger(orderItem.getGroupQuantity()));

						// 组合商品信息
						MsgModel<Product> msgModel = ProductUtil.getProductByProductId(groupProductId, false);
						if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
							Product groupProduct = msgModel.getData();
							orderItem.setProductName(StringUtil.null2Str(groupProduct.getName()));
							orderItem.setIsSoldout(StringUtil.booleanToInt(groupProduct.getIsPaymentSoldout()));
						}
						resultItemsList.add(orderItem);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultItemsList;
	}
}
