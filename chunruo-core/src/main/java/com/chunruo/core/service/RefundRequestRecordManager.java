package com.chunruo.core.service;
import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.RefundRequestRecord;

public interface RefundRequestRecordManager extends GenericManager<RefundRequestRecord, Long> {

	public RefundRequestRecord getRefundRequestRecordByOrderNoAndRefundNumber(String orderNo, String refundNumber);
}
