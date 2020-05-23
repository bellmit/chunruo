package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Refund;

public interface RefundManager extends GenericManager<Refund, Long> {
	
	public List<Refund> getRefundListByOrderItemId(Long orderItemId, Boolean isCurrentTask);
	
	public List<Refund> getRefundListByUserId(Long userId, Boolean isCurrentTask);

	public List<Refund> getRefundListByUpdateTime(Date updateTime);
	
	public Refund saveRefund(Refund refund);

	public void checkRefund(Refund refund, boolean checkResult, String reason, String address, Double amount, Long userId, String userName);

	public List<Refund> getExpressTimeOutRefundList(Date updateTime);
	
	public List<Refund> getRefundListByOrderId(Long orderId, Boolean isCurrentTask);
	
	public void refundReceipt(List<Long> refundIdList, Long userId, String userName);

	public List<Refund> getRefundListByStoreId(Long storeId, Boolean isCurrentTask);

	public List<Refund> getRefundListByOrderIdList(List<Long> orderIdList);

	public List<Object[]> getRefundDetailByOrderIdList(List<Long> orderIdList);
}
