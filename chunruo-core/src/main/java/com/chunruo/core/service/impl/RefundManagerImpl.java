package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.RefundHistory;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserProfitRecord;
import com.chunruo.core.repository.RefundRepository;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.RefundHistoryManager;
import com.chunruo.core.service.RefundManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserSaleRecordManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.RefundUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.WeiXinPayUtil;
import com.chunruo.core.util.WxSendUtil;
import com.chunruo.core.vo.MsgModel;

@Transactional
@Component("refundManager")
public class RefundManagerImpl extends GenericManagerImpl<Refund, Long> implements RefundManager {
	private RefundRepository refundRepository;
	
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private RefundHistoryManager refundHistoryManager;
	@Autowired
	private UserSaleRecordManager userSaleRecordManager;

	@Autowired
	public RefundManagerImpl(RefundRepository refundRepository) {
		super(refundRepository);
		this.refundRepository = refundRepository;
	}

	@Override
	public List<Refund> getRefundListByOrderItemId(Long orderItemId, Boolean isCurrentTask) {
		return refundRepository.getRefundListByOrderItemId(orderItemId, isCurrentTask);
	}

	@Override
	public List<Refund> getRefundListByUserId(Long userId, Boolean isCurrentTask) {
		return refundRepository.getRefundListByUserId(userId, isCurrentTask);
	}
	
	@Override
	public List<Refund> getRefundListByUpdateTime(Date updateTime) {
		return refundRepository.getRefundListByUpdateTime(updateTime);
	}

	@Override
	public Refund saveRefund(Refund refund) {
		// 更新其他退款退货记录为过往记录
		List<Refund> refundList = this.getRefundListByOrderItemId(refund.getOrderItemId(), true);
		if (refundList != null && refundList.size() > 0) {
			for (Refund xRefund : refundList) {
				xRefund.setIsCurrentTask(false);
				xRefund.setCompletedTime(DateUtil.getCurrentDate());
			}
			this.batchInsert(refundList, refundList.size());
		}

		refund.setIsCurrentTask(true);
		refund.setUpdateTime(DateUtil.getCurrentDate());
		return this.save(refund);
	}

	@Override
	public void checkRefund(Refund refund, boolean checkResult, String reason, String address,Double amount, Long userId, String userName){
		//拒绝
		if (!checkResult) {
			refund.setRefundStatus(Refund.REFUND_STATUS_REFUSE);
			refund.setRefusalReason(reason);
			refund.setUpdateTime(DateUtil.getCurrentDate());
			refund = this.refundRepository.save(refund);
			
			String title = "拒绝退款申请";
			if(StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_GOODS)){
				// 退货
				title = "拒绝退货退款申请";
			}
			
			RefundHistory refundHistory = new RefundHistory();
			refundHistory.setUserId(userId);
			refundHistory.setAdminName(userName);
			refundHistory.setTitle(title);
			refundHistory.setContent(reason);
			refundHistory.setRefundId(refund.getRefundId());
			refundHistory.setCreateTime(DateUtil.getCurrentDate());
			this.refundHistoryManager.save(refundHistory);
		}else{
			String title = "";
			boolean isNeedUpdateOrderStatus = false;
			
			// 审核通过
			if(StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_MONEY)){
				// 退款
				
				boolean isRefundSucc = false;
				MsgModel<Double> msgModel = RefundUtil.getRefundInfo(refund, amount);
				if(StringUtil.nullToBoolean(msgModel.getIsSucc())) {
					isRefundSucc = true;
				}
				
				title = "同意退款申请";
				reason =String.format("%s %s同意了退款申请",DateUtil.formatDate(DateUtil.DATE_FORMAT_HOUR, DateUtil.getCurrentDate()),userName);
				
				if(StringUtil.nullToBoolean(isRefundSucc)) {
                	//退款成功
                	refund.setRefundStatus(Refund.REFUND_STATUS_COMPLETED);
                	refund.setCompletedTime(DateUtil.getCurrentDate());
                	//全部退款时，更新订单状态为关闭
					isNeedUpdateOrderStatus = true; 
                }else {
                	//退款失败
                	reason = "退款失败";
                }
				
			}else if(StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_GOODS)){
				// 退货
				title = "同意退货退款申请";
				reason =String.format("%s %s同意了退货退款申请",DateUtil.formatDate(DateUtil.DATE_FORMAT_HOUR, DateUtil.getCurrentDate()),userName);
				if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_WAIT)) {
					//客服审核退货
					refund.setRefundStatus(Refund.REFUND_STATUS_SUCCESS);
					refund.setRefundAddress(address);
				}else if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_RECEIPT)) {
					//平台收货通过之后执行自动退款
					boolean isRefundSucc = false;
					MsgModel<Double> msgModel = RefundUtil.getRefundInfo(refund, amount);
					if(StringUtil.nullToBoolean(msgModel.getIsSucc())) {
						isRefundSucc = true;
					}
                    if(StringUtil.nullToBoolean(isRefundSucc)) {
                    	//退款成功
                    	refund.setRefundStatus(Refund.REFUND_STATUS_COMPLETED);
                    	refund.setCompletedTime(DateUtil.getCurrentDate());
                    	//全部退款时，更新订单状态为关闭
    					isNeedUpdateOrderStatus = true; 
                    }else {
                    	//退款失败
                    	reason = "退款失败";
                    }
				}
			}
			refund.setRefundAmount(StringUtil.nullToString(amount));
			refund.setUpdateTime(DateUtil.getCurrentDate());
			refund = this.refundRepository.save(refund);
			
			//全部退款时，更新订单状态为关闭
			if(isNeedUpdateOrderStatus){
				if(!StringUtil.compareObject(StringUtil.nullToInteger(refund.getRefundType()), Refund.REFUND_TYPE_PART)) {
					this.checkIsCloseOrder(refund.getOrderId(), userId);
				}
				
				try {
					Order order = this.orderManager.get(StringUtil.nullToLong(refund.getOrderId()));
					UserInfo userInfo = this.userInfoManager.get(StringUtil.nullToLong(refund.getUserId()));
					if(order != null && order.getOrderId() != null
							&& userInfo != null && userInfo.getUserId() != null) {
						WxSendUtil.refundSucc(refund, order, userInfo);
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			// 操作日志
			RefundHistory refundHistory = new RefundHistory();
			refundHistory.setUserId(userId);
			refundHistory.setAdminName(userName);
			refundHistory.setTitle(title);
			refundHistory.setContent(reason);
			refundHistory.setRefundId(refund.getRefundId());
			refundHistory.setCreateTime(DateUtil.getCurrentDate());
			this.refundHistoryManager.save(refundHistory);
		}
	}
	
	@Override
	public void refundReceipt(List<Long> refundIdList, Long userId, String userName){
		List<Refund> refundList = this.refundRepository.getRefundListByIdList(refundIdList, Refund.REFUND_STATUS_RECEIPT);
		if(refundList != null && refundList.size() > 0){
			List<RefundHistory> refundHistoryList = new ArrayList<RefundHistory> ();
			for(Refund refund : refundList){
//				refund.setRefundStatus(Refund.REFUND_STATUS__MANAGER); //平台收货后交由客服主管审核
				refund.setIsReceive(true);
				refund.setCompletedTime(DateUtil.getCurrentDate());
				refund.setUpdateTime(refund.getCompletedTime());
				this.checkRefund(refund, true, null, null, StringUtil.nullToDouble(refund.getRefundAmount()), userId, userName);
				
				RefundHistory refundHistory = new RefundHistory();
				refundHistory.setUserId(userId);
				refundHistory.setAdminName(userName);
				refundHistory.setContent(String.format("%s %s确认收货并退款", DateUtil.formatDate(DateUtil.DATE_FORMAT_HOUR, DateUtil.getCurrentDate()),userName));
				refundHistory.setTitle("确认收货并退款");
				refundHistory.setRefundId(refund.getRefundId());
				refundHistory.setCreateTime(DateUtil.getCurrentDate());
				refundHistoryList.add(refundHistory);
			}
			refundList = this.batchInsert(refundList, refundList.size());
			// 操作日志
			this.refundHistoryManager.batchInsert(refundHistoryList, refundHistoryList.size());
		}
	}

	@Override
	public List<Refund> getExpressTimeOutRefundList(Date updateTime) {
		return this.refundRepository.getExpressTimeOutRefundList(updateTime);
	}

	@Override
	public List<Refund> getRefundListByOrderId(Long orderId, Boolean isCurrentTask) {
		return this.refundRepository.getRefundListByOrderId(orderId, isCurrentTask);
	}
	
	/**
	 * 退款退货更新订单状态
	 * @param orderId
	 * @param userId
	 */
	private void checkIsCloseOrder(Long orderId, Long userId){
		Order order = this.orderManager.getOrderByOrderId(orderId);
		if(order == null 
				|| order.getOrderId() == null
				|| order.getOrderItemsList() == null
				|| order.getOrderItemsList().size() <= 0){
			return ;
		}
		
		// 订单商品列表
		Map<Long, Boolean> orderItemCloseStatusMap = new HashMap<Long, Boolean> ();
		Map<Long, OrderItems> orderItemMap = new HashMap<Long, OrderItems> ();
		Map<String, List<OrderItems>> groupOrderItemsListMap = new HashMap<String, List<OrderItems>> ();
		
		//得到订单详情
		List<OrderItems> orderItemList = order.getOrderItemsList();
		for(OrderItems orderItems : orderItemList){
			if(StringUtil.nullToBoolean(orderItems.getIsGiftProduct())) {
				continue;
			}
			orderItemMap.put(orderItems.getItemId(), orderItems);
			orderItemCloseStatusMap.put(orderItems.getItemId(), false);
			
			// 组合商品
			if(StringUtil.nullToBoolean(order.getIsGroupProduct())){
				if(groupOrderItemsListMap.containsKey(orderItems.getGroupUniqueBatch())){
					groupOrderItemsListMap.get(orderItems.getGroupUniqueBatch()).add(orderItems);
				}else{
					List<OrderItems> list = new ArrayList<OrderItems> ();
					list.add(orderItems);
					groupOrderItemsListMap.put(orderItems.getGroupUniqueBatch(), list);
				}
			}
		}
		
		// 检查订单商品列表是否有退款完成状态更新
		List<Refund> refundList = this.getRefundListByOrderId(orderId, true);
		if(refundList == null || refundList.size() <= 0){
			return;
		}
		
		List<Integer> refundStatusList = new ArrayList<Integer>();
//		refundStatusList.add(Refund.REFUND_STATUS__MANAGER);
//		refundStatusList.add(Refund.REFUND_STATUS_FINANCE);
//		refundStatusList.add(Refund.REFUND_STATUS_REJECT);
		refundStatusList.add(Refund.REFUND_STATUS_COMPLETED);
//		refundStatusList.add(Refund.REFUND_STATUS_SUCCESS);
//		refundStatusList.add(Refund.REFUND_STATUS_RECEIPT);
		for(Refund refund : refundList){
			//检查是否同意退款过
			if(refundStatusList.contains(refund.getRefundStatus())
					&& orderItemCloseStatusMap.containsKey(refund.getOrderItemId())){
				orderItemCloseStatusMap.put(refund.getOrderItemId(), true);
				
				// 检查是否组合商品
				if(StringUtil.nullToBoolean(order.getIsGroupProduct())){
					OrderItems orderItems = orderItemMap.get(refund.getOrderItemId());
					if(StringUtil.nullToBoolean(orderItems.getIsGroupProduct())
							&& groupOrderItemsListMap.containsKey(StringUtil.null2Str(orderItems.getGroupUniqueBatch()))){
						List<OrderItems> list = groupOrderItemsListMap.get(StringUtil.null2Str(orderItems.getGroupUniqueBatch()));
						if(list != null && list.size() > 0){
							for(OrderItems items : list){
								orderItemCloseStatusMap.put(items.getItemId(), true);
							}
						}
					}
				}
			}
		}
		
		// 检查订单是否可以直接关闭
		boolean isOrderClose = true;
		Double totalProfit = 0.0D;
		Double totalTopProfit = 0.0D;
		for(Entry<Long, Boolean> entry : orderItemCloseStatusMap.entrySet()){
			// 还有商品成功交易
			if(!StringUtil.nullToBoolean(entry.getValue())){
				isOrderClose = false;
				OrderItems orderItems = orderItemMap.get(entry.getKey());
				//上线利润
				totalTopProfit += StringUtil.nullToDoubleFormat(orderItems.getTopProfit());
				if(StringUtil.nullToBoolean(order.getIsShareBuy())) {
					//分享利润
					totalProfit += StringUtil.nullToDoubleFormat(orderItems.getProfit());
				}
			}
		}
		
		// 退款成功更新订单状态或重新计算商品利润
		if(isOrderClose){
			List<Order> subOrderList = this.orderManager.getOrderSubListByParentOrderId(order.getOrderId());
			List<Long> subOrderIdList = new ArrayList<Long>();
			if(subOrderList != null && subOrderList.size() > 0) {
				for(Order subOrder :subOrderList) {
					//检查是否还有未更新状态的订单
					if(!StringUtil.compareObject(OrderStatus.CANCEL_ORDER_STATUS, subOrder.getStatus())) {
						subOrderIdList.add(subOrder.getOrderId());    //子订单
					}
				}
			}
			
			if(subOrderIdList != null && subOrderIdList.size() > 0) {
				// 子订单更新关闭状态
				this.orderManager.updateSubOrderCloseStatus(subOrderIdList, "全部退款完成,订单关闭", userId);
			}
			// 订单直接关闭
			this.orderManager.updateOrderCloseStatus(Arrays.asList(orderId), "全部退款完成,订单关闭", userId);
			
		}else{
			//检查是否包含子订单
			if(StringUtil.nullToBoolean(order.getIsSplitSingle())){
				List<Order> subOrderList = this.orderManager.getOrderSubListByParentOrderId(order.getOrderId());
				if(subOrderList != null && subOrderList.size() > 0){
					List<Long> subOrderCloseList = new ArrayList<Long> ();
					for(Order subOrder : subOrderList){
						// 非关闭状态的子订单
						if(!StringUtil.compareObject(OrderStatus.CANCEL_ORDER_STATUS, subOrder.getStatus())){
							Map<Long, Boolean> subOrderItemCloseStatusMap = new HashMap<Long, Boolean> ();
							for(OrderItems orderItems : subOrder.getOrderItemsList()){
								subOrderItemCloseStatusMap.put(orderItems.getItemId(), false);
							}
							
							// 检查子订单是否全部退款完成
							for(Refund refund : refundList){
								//客服同意退款，订单关闭
								if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_COMPLETED)
										&& subOrderItemCloseStatusMap.containsKey(refund.getOrderItemId())){
									subOrderItemCloseStatusMap.put(refund.getOrderItemId(), true);
								}
							}
							
							// 检查子订单是否可以直接关闭
							boolean isSubOrderClose = true;
							for(Entry<Long, Boolean> entry : subOrderItemCloseStatusMap.entrySet()){
								// 还有商品成功交易
								if(!StringUtil.nullToBoolean(entry.getValue())){
									isSubOrderClose = false;
								}
							}
							
							// 可以直接关闭的子订单
							if(isSubOrderClose){
								subOrderCloseList.add(subOrder.getOrderId());
							}
						}
					}
					
					// 可以直接关闭的子订单
					if(subOrderCloseList != null && subOrderCloseList.size() > 0){
						// 子订单更新关闭状态
						this.orderManager.updateSubOrderCloseStatus(subOrderCloseList, "财务同意退款,订单关闭", userId);
						this.userSaleRecordManager.updateUserSaleRecordByStatus(subOrderCloseList, OrderStatus.CANCEL_ORDER_STATUS);
					}
				}
			}
			
//			// 利润重新计算
//			List<Object[]> storeProfitList = new ArrayList<Object[]> ();
//			storeProfitList.add(new Object[]{totalProfit, userInfoId, order.getOrderId(), UserProfitRecord.DISTRIBUTION_TYPE_FX});
//			this.batchUpdate("update jkd_user_profit_record set income=?, update_time=now() where user_id=? and order_id=? and type=?", storeProfitList);
		}
		
		// 利润重新计算
		List<Object[]> storeProfitList = new ArrayList<Object[]> ();
		storeProfitList.add(new Object[]{totalTopProfit, order.getTopUserId(), order.getOrderId(), UserProfitRecord.DISTRIBUTION_TYPE_FX});
		if(StringUtil.nullToBoolean(order.getIsShareBuy())) {
			storeProfitList.add(new Object[]{totalProfit, order.getStoreId(), order.getOrderId(), UserProfitRecord.DISTRIBUTION_TYPE_FX});
		}
		this.batchUpdate("update jkd_user_profit_record set income=?, update_time=now() where user_id=? and order_id=? and type=?", storeProfitList);
	}

	@Override
	public List<Refund> getRefundListByStoreId(Long storeId, Boolean isCurrentTask) {
		return this.refundRepository.getRefundListByStoreId(storeId,isCurrentTask);
	}

	@Override
	public List<Refund> getRefundListByOrderIdList(List<Long> orderIdList) {
		return this.refundRepository.getRefundListByOrderIdList(orderIdList);
	}

	@Override
	public List<Object[]> getRefundDetailByOrderIdList(List<Long> orderIdList) {
		if(orderIdList == null || orderIdList.size() <= 0) {
			return null;
		}
		StringBuilder strBulSql = new StringBuilder();
		strBulSql.append("select jr.order_id,joi.top_profit,jr.refund_type,jr.completed_time from jkd_refund jr,jkd_order_items joi ");
		strBulSql.append("where jr.order_id = joi.order_id ");
		strBulSql.append("and jr.order_id in(%s) and jr.refund_status = 5");
		return this.querySql(String.format(strBulSql.toString(), StringUtil.longListToStr(orderIdList)));
	}

}
