package com.chunruo.core.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderCustomsRecord;
import com.chunruo.core.model.OrderHistory;
import com.chunruo.core.repository.OrderCustomsRecordRepository;
import com.chunruo.core.service.OrderCustomsRecordManager;
import com.chunruo.core.service.OrderHistoryManager;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("orderCustomsRecordManager")
public class OrderCustomsRecordManagerImpl extends GenericManagerImpl<OrderCustomsRecord, Long> implements OrderCustomsRecordManager {
	private OrderCustomsRecordRepository orderCustomsRecordRepository;
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private OrderHistoryManager orderHistoryManager;
	
	@Autowired
	public OrderCustomsRecordManagerImpl(OrderCustomsRecordRepository orderCustomsRecordRepository) {
		super(orderCustomsRecordRepository);
		this.orderCustomsRecordRepository = orderCustomsRecordRepository;
	}
	
	@Override
	public OrderCustomsRecord getOrderCustomsRecordByOrderNo(String orderNo) {
		List<OrderCustomsRecord> list = this.orderCustomsRecordRepository.getOrderCustomsRecordListByOrderNo(orderNo);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}
	
	@Override
	public List<OrderCustomsRecord> updateLoadPushPayCustomsQueryFunction() {
		String uniqueString = StringUtil.null2Str(UUID.randomUUID().toString());
		String batchNumber = this.orderCustomsRecordRepository.executeSqlFunction("{?=call loadPushPayCustomsQuery_Fnc(?)}", new Object[] { uniqueString });
		log.debug("loadPushPayCustomsQuery_Fnc=======>>> " + StringUtil.null2Str(uniqueString));
		if (StringUtil.compareObject(uniqueString, batchNumber)) {
			return this.orderCustomsRecordRepository.getOrderCustomsRecordListByBatchNumber(batchNumber);
		}
		return null;
	}

	@Override
	public OrderCustomsRecord saveOrderCustomsRecord(OrderCustomsRecord orderCustomsRecord) {
		orderCustomsRecord.setUpdateTime(DateUtil.getCurrentDate());
		orderCustomsRecord = this.save(orderCustomsRecord);
	
		// 已提交申请订单支付报关状态
		if(StringUtil.nullToBoolean(orderCustomsRecord.getIsPushCustomSucc())){
			Order order = this.orderManager.get(orderCustomsRecord.getOrderId());
			if(order != null
					&& order.getOrderId() != null
					&& !StringUtil.nullToBoolean(order.getIsDelete())
					&& !StringUtil.nullToBoolean(order.getIsIntercept())
					&& !StringUtil.nullToBoolean(order.getIsSplitSingle())
					&& !StringUtil.nullToBoolean(order.getIsSubOrder())
					&& !StringUtil.nullToBoolean(order.getIsPushErp())
					&& !StringUtil.nullToBoolean(order.getIsDirectPushErp())
					&& StringUtil.nullToBoolean(order.getIsPaymentSucc())
					&& StringUtil.nullToBoolean(order.getIsPushCustoms())
					&& StringUtil.compareObject(OrderStatus.UN_DELIVER_ORDER_STATUS, order.getStatus())){
				// 已经申请推送海关,等待结果
				order.setIsRequestPushCustoms(true);
				order.setUpdateTime(DateUtil.getCurrentDate());
				this.orderManager.save(order);
			}
		}
		return orderCustomsRecord;
	}

	@Override
	public void updateOrderCustomsStatusSucc(Long recordId, Boolean isPushCustomSucc, String errorMsg) {
		OrderCustomsRecord orderCustomsRecord = this.get(recordId);
		if(orderCustomsRecord != null 
				&& orderCustomsRecord.getRecordId() != null
				&& StringUtil.nullToBoolean(isPushCustomSucc)
				&& !StringUtil.nullToBoolean(orderCustomsRecord.getIsPushCustomSucc())){
			orderCustomsRecord.setIsPushCustomSucc(true);
			orderCustomsRecord.setErrorMsg(StringUtil.null2Str(errorMsg));
			orderCustomsRecord.setUpdateTime(DateUtil.getCurrentDate());
			orderCustomsRecord = this.save(orderCustomsRecord);
			
			// 同步更新订单支付报关状态
			Order order = this.orderManager.get(orderCustomsRecord.getOrderId());
			if(order != null
					&& order.getOrderId() != null
					&& !StringUtil.nullToBoolean(order.getIsDelete())
					&& !StringUtil.nullToBoolean(order.getIsIntercept())
					&& !StringUtil.nullToBoolean(order.getIsSplitSingle())
					&& !StringUtil.nullToBoolean(order.getIsSubOrder())
					&& !StringUtil.nullToBoolean(order.getIsPushErp())
					&& !StringUtil.nullToBoolean(order.getIsDirectPushErp())
					&& StringUtil.nullToBoolean(order.getIsPaymentSucc())
					&& StringUtil.nullToBoolean(order.getIsPushCustoms())
					&& StringUtil.compareObject(OrderStatus.UN_DELIVER_ORDER_STATUS, order.getStatus())){
				order.setIsDirectPushErp(true);
				order.setIsRequestPushCustoms(true);
				order.setUpdateTime(DateUtil.getCurrentDate());
				this.orderManager.save(order);
				
				// 记录支付宝报关成功
				OrderHistory orderHistory = new OrderHistory();
				orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
				orderHistory.setOrderId(order.getOrderId());
				orderHistory.setName("支付记录海关清关成功");
				orderHistory.setMessage("支付记录海关清关成功");
				orderHistory.setCreateTime(DateUtil.getCurrentDate());
				this.orderHistoryManager.save(orderHistory);
			}
		}
	}

	@Transactional
	@Override
	public OrderCustomsRecord saveRecord(Long orderId, OrderCustomsRecord orderCustomsRecord) {
		orderCustomsRecord = this.saveOrderCustomsRecord(orderCustomsRecord);
		
		// 支付机构推送支付成功并更新订单身份证信息
		Order order = this.orderManager.get(orderId);
		if(StringUtil.nullToBoolean(order.getIsNeedCheckPayment())) {
			order.setIdentityName(orderCustomsRecord.getIdCardName());
			order.setIdentityNo(orderCustomsRecord.getIdCardNo());
			order.setIsNeedCheckPayment(false);
			order.setUpdateTime(DateUtil.getCurrentDate());
			this.orderManager.save(order);
		}
		return orderCustomsRecord;
	}
}
