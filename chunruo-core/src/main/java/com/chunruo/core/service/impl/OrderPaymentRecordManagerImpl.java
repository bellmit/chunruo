package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants.PaymentType;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.OrderPaymentRecord;
import com.chunruo.core.repository.OrderPaymentRecordRepository;
import com.chunruo.core.service.OrderPaymentRecordManager;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("orderPaymentRecordManager")
public class OrderPaymentRecordManagerImpl extends GenericManagerImpl<OrderPaymentRecord, Long> implements OrderPaymentRecordManager {
	private OrderPaymentRecordRepository orderPaymentRecordRepository;

	@Autowired
	public OrderPaymentRecordManagerImpl(OrderPaymentRecordRepository orderPaymentRecordRepository) {
		super(orderPaymentRecordRepository);
		this.orderPaymentRecordRepository = orderPaymentRecordRepository;
	}
	
	@Override
	public void saveOrderPaymentRecord(OrderPaymentRecord record) {
		if (record != null) {
			Long orderId = record.getOrderId();
			String orderNo = record.getOrderNo();
			Integer paymentType = record.getPaymentType();
			Long weChatConfigId = record.getWeChatConfigId();
			OrderPaymentRecord orderPaymentRecord = this.getByOrderIdAndPaymentType(orderId, orderNo, paymentType, weChatConfigId);
			if(orderPaymentRecord != null && orderPaymentRecord.getRecordId() != null){
				record.setRecordId(orderPaymentRecord.getRecordId());
				record.setCreateTime(orderPaymentRecord.getCreateTime());
			}
			this.save(record);
		}
	}
	
	@Override
	public void deleteOtherByOrderId(Long orderId) {
		List<OrderPaymentRecord> list = this.orderPaymentRecordRepository.getListByOrderId(orderId);
		if(list == null || list.isEmpty()) {
			return;
		}
		
		// 删除未支付成功的支付请求记录
		List<Long> idList = new ArrayList<Long>();
		for (OrderPaymentRecord orderPaymentRecord : list) {
			if(!StringUtil.nullToBoolean(orderPaymentRecord.getIsPaymentSucc())){
				idList.add(orderPaymentRecord.getRecordId());
			}
		}
		this.orderPaymentRecordRepository.deleteByIdList(idList);
	}
	
	@Override
	public OrderPaymentRecord getByOrderIdAndPaymentType(Long orderId, String orderNo, Integer paymentType, Long weChatConfigId) {
		List<OrderPaymentRecord> recordList = this.orderPaymentRecordRepository.getListByOrderIdAndPaymentType(orderId, orderNo, paymentType, weChatConfigId);
		return (recordList != null && recordList.size() > 0) ? recordList.get(0) : null;
	}
	
	@Override
	public List<OrderPaymentRecord> updateOrderPaymentRecordByLoadFunction() {
		String uniqueString = StringUtil.null2Str(UUID.randomUUID().toString());
		String batchNumber = this.orderPaymentRecordRepository.executeSqlFunction("{?=call loadOrderPaymentRecordList_Fnc(?)}", new Object[]{uniqueString});
		log.debug("updateOrderSyncStatusByLoadFunction=======>>> " + StringUtil.null2Str(uniqueString));
		if(StringUtil.compareObject(uniqueString, batchNumber)){
			return this.orderPaymentRecordRepository.getOrderPaymentRecordListByBatchNumber(batchNumber);
		}
		return null;
	}
}
