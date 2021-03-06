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
		// ?????????????????????????????????????????????
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
		//??????
		if (!checkResult) {
			refund.setRefundStatus(Refund.REFUND_STATUS_REFUSE);
			refund.setRefusalReason(reason);
			refund.setUpdateTime(DateUtil.getCurrentDate());
			refund = this.refundRepository.save(refund);
			
			String title = "??????????????????";
			if(StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_GOODS)){
				// ??????
				title = "????????????????????????";
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
			
			// ????????????
			if(StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_MONEY)){
				// ??????
				
				boolean isRefundSucc = false;
				MsgModel<Double> msgModel = RefundUtil.getRefundInfo(refund, amount);
				if(StringUtil.nullToBoolean(msgModel.getIsSucc())) {
					isRefundSucc = true;
				}
				
				title = "??????????????????";
				reason =String.format("%s %s?????????????????????",DateUtil.formatDate(DateUtil.DATE_FORMAT_HOUR, DateUtil.getCurrentDate()),userName);
				
				if(StringUtil.nullToBoolean(isRefundSucc)) {
                	//????????????
                	refund.setRefundStatus(Refund.REFUND_STATUS_COMPLETED);
                	refund.setCompletedTime(DateUtil.getCurrentDate());
                	//?????????????????????????????????????????????
					isNeedUpdateOrderStatus = true; 
                }else {
                	//????????????
                	reason = "????????????";
                }
				
			}else if(StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_GOODS)){
				// ??????
				title = "????????????????????????";
				reason =String.format("%s %s???????????????????????????",DateUtil.formatDate(DateUtil.DATE_FORMAT_HOUR, DateUtil.getCurrentDate()),userName);
				if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_WAIT)) {
					//??????????????????
					refund.setRefundStatus(Refund.REFUND_STATUS_SUCCESS);
					refund.setRefundAddress(address);
				}else if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_RECEIPT)) {
					//??????????????????????????????????????????
					boolean isRefundSucc = false;
					MsgModel<Double> msgModel = RefundUtil.getRefundInfo(refund, amount);
					if(StringUtil.nullToBoolean(msgModel.getIsSucc())) {
						isRefundSucc = true;
					}
                    if(StringUtil.nullToBoolean(isRefundSucc)) {
                    	//????????????
                    	refund.setRefundStatus(Refund.REFUND_STATUS_COMPLETED);
                    	refund.setCompletedTime(DateUtil.getCurrentDate());
                    	//?????????????????????????????????????????????
    					isNeedUpdateOrderStatus = true; 
                    }else {
                    	//????????????
                    	reason = "????????????";
                    }
				}
			}
			refund.setRefundAmount(StringUtil.nullToString(amount));
			refund.setUpdateTime(DateUtil.getCurrentDate());
			refund = this.refundRepository.save(refund);
			
			//?????????????????????????????????????????????
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
			
			// ????????????
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
//				refund.setRefundStatus(Refund.REFUND_STATUS__MANAGER); //???????????????????????????????????????
				refund.setIsReceive(true);
				refund.setCompletedTime(DateUtil.getCurrentDate());
				refund.setUpdateTime(refund.getCompletedTime());
				this.checkRefund(refund, true, null, null, StringUtil.nullToDouble(refund.getRefundAmount()), userId, userName);
				
				RefundHistory refundHistory = new RefundHistory();
				refundHistory.setUserId(userId);
				refundHistory.setAdminName(userName);
				refundHistory.setContent(String.format("%s %s?????????????????????", DateUtil.formatDate(DateUtil.DATE_FORMAT_HOUR, DateUtil.getCurrentDate()),userName));
				refundHistory.setTitle("?????????????????????");
				refundHistory.setRefundId(refund.getRefundId());
				refundHistory.setCreateTime(DateUtil.getCurrentDate());
				refundHistoryList.add(refundHistory);
			}
			refundList = this.batchInsert(refundList, refundList.size());
			// ????????????
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
	 * ??????????????????????????????
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
		
		// ??????????????????
		Map<Long, Boolean> orderItemCloseStatusMap = new HashMap<Long, Boolean> ();
		Map<Long, OrderItems> orderItemMap = new HashMap<Long, OrderItems> ();
		Map<String, List<OrderItems>> groupOrderItemsListMap = new HashMap<String, List<OrderItems>> ();
		
		//??????????????????
		List<OrderItems> orderItemList = order.getOrderItemsList();
		for(OrderItems orderItems : orderItemList){
			if(StringUtil.nullToBoolean(orderItems.getIsGiftProduct())) {
				continue;
			}
			orderItemMap.put(orderItems.getItemId(), orderItems);
			orderItemCloseStatusMap.put(orderItems.getItemId(), false);
			
			// ????????????
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
		
		// ?????????????????????????????????????????????????????????
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
			//???????????????????????????
			if(refundStatusList.contains(refund.getRefundStatus())
					&& orderItemCloseStatusMap.containsKey(refund.getOrderItemId())){
				orderItemCloseStatusMap.put(refund.getOrderItemId(), true);
				
				// ????????????????????????
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
		
		// ????????????????????????????????????
		boolean isOrderClose = true;
		Double totalProfit = 0.0D;
		Double totalTopProfit = 0.0D;
		for(Entry<Long, Boolean> entry : orderItemCloseStatusMap.entrySet()){
			// ????????????????????????
			if(!StringUtil.nullToBoolean(entry.getValue())){
				isOrderClose = false;
				OrderItems orderItems = orderItemMap.get(entry.getKey());
				//????????????
				totalTopProfit += StringUtil.nullToDoubleFormat(orderItems.getTopProfit());
				if(StringUtil.nullToBoolean(order.getIsShareBuy())) {
					//????????????
					totalProfit += StringUtil.nullToDoubleFormat(orderItems.getProfit());
				}
			}
		}
		
		// ?????????????????????????????????????????????????????????
		if(isOrderClose){
			List<Order> subOrderList = this.orderManager.getOrderSubListByParentOrderId(order.getOrderId());
			List<Long> subOrderIdList = new ArrayList<Long>();
			if(subOrderList != null && subOrderList.size() > 0) {
				for(Order subOrder :subOrderList) {
					//??????????????????????????????????????????
					if(!StringUtil.compareObject(OrderStatus.CANCEL_ORDER_STATUS, subOrder.getStatus())) {
						subOrderIdList.add(subOrder.getOrderId());    //?????????
					}
				}
			}
			
			if(subOrderIdList != null && subOrderIdList.size() > 0) {
				// ???????????????????????????
				this.orderManager.updateSubOrderCloseStatus(subOrderIdList, "??????????????????,????????????", userId);
			}
			// ??????????????????
			this.orderManager.updateOrderCloseStatus(Arrays.asList(orderId), "??????????????????,????????????", userId);
			
		}else{
			//???????????????????????????
			if(StringUtil.nullToBoolean(order.getIsSplitSingle())){
				List<Order> subOrderList = this.orderManager.getOrderSubListByParentOrderId(order.getOrderId());
				if(subOrderList != null && subOrderList.size() > 0){
					List<Long> subOrderCloseList = new ArrayList<Long> ();
					for(Order subOrder : subOrderList){
						// ???????????????????????????
						if(!StringUtil.compareObject(OrderStatus.CANCEL_ORDER_STATUS, subOrder.getStatus())){
							Map<Long, Boolean> subOrderItemCloseStatusMap = new HashMap<Long, Boolean> ();
							for(OrderItems orderItems : subOrder.getOrderItemsList()){
								subOrderItemCloseStatusMap.put(orderItems.getItemId(), false);
							}
							
							// ???????????????????????????????????????
							for(Refund refund : refundList){
								//?????????????????????????????????
								if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_COMPLETED)
										&& subOrderItemCloseStatusMap.containsKey(refund.getOrderItemId())){
									subOrderItemCloseStatusMap.put(refund.getOrderItemId(), true);
								}
							}
							
							// ???????????????????????????????????????
							boolean isSubOrderClose = true;
							for(Entry<Long, Boolean> entry : subOrderItemCloseStatusMap.entrySet()){
								// ????????????????????????
								if(!StringUtil.nullToBoolean(entry.getValue())){
									isSubOrderClose = false;
								}
							}
							
							// ??????????????????????????????
							if(isSubOrderClose){
								subOrderCloseList.add(subOrder.getOrderId());
							}
						}
					}
					
					// ??????????????????????????????
					if(subOrderCloseList != null && subOrderCloseList.size() > 0){
						// ???????????????????????????
						this.orderManager.updateSubOrderCloseStatus(subOrderCloseList, "??????????????????,????????????", userId);
						this.userSaleRecordManager.updateUserSaleRecordByStatus(subOrderCloseList, OrderStatus.CANCEL_ORDER_STATUS);
					}
				}
			}
			
//			// ??????????????????
//			List<Object[]> storeProfitList = new ArrayList<Object[]> ();
//			storeProfitList.add(new Object[]{totalProfit, userInfoId, order.getOrderId(), UserProfitRecord.DISTRIBUTION_TYPE_FX});
//			this.batchUpdate("update jkd_user_profit_record set income=?, update_time=now() where user_id=? and order_id=? and type=?", storeProfitList);
		}
		
		// ??????????????????
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
