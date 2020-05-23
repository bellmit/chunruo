package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.RefundRequestRecord;
import com.chunruo.core.repository.RefundRequestRecordRepository;
import com.chunruo.core.service.RefundRequestRecordManager;

@Transactional
@Component("refundRequestRecordManager")
public class RefundRequestRecordManagerImpl extends GenericManagerImpl<RefundRequestRecord, Long> implements RefundRequestRecordManager{
	private RefundRequestRecordRepository refundRequestRecordRepository;

	@Autowired
	public RefundRequestRecordManagerImpl(RefundRequestRecordRepository refundRequestRecordRepository) {
		super(refundRequestRecordRepository);
		this.refundRequestRecordRepository = refundRequestRecordRepository;
	}

	@Override
	public RefundRequestRecord getRefundRequestRecordByOrderNoAndRefundNumber(String orderNo, String refundNumber) {
		return this.refundRequestRecordRepository.getRefundRequestRecordByOrderNoAndRefundNumber(orderNo,refundNumber);
	}

}
