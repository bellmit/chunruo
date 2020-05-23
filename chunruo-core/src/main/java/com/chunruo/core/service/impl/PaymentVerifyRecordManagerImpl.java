package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.PaymentVerifyRecord;
import com.chunruo.core.repository.PaymentVerifyRecordRepository;
import com.chunruo.core.service.PaymentVerifyRecordManager;
import com.chunruo.core.util.DateUtil;

@Transactional
@Component("paymentVerifyRecordService")
public class PaymentVerifyRecordManagerImpl extends GenericManagerImpl<PaymentVerifyRecord, Long> implements PaymentVerifyRecordManager{
	private PaymentVerifyRecordRepository paymentVerifyRecordRepository;

	@Autowired
	public PaymentVerifyRecordManagerImpl(PaymentVerifyRecordRepository paymentVerifyRecordRepository) {
		super(paymentVerifyRecordRepository);
		this.paymentVerifyRecordRepository = paymentVerifyRecordRepository;
	}

	@Override
	public PaymentVerifyRecord getPaymentVerifyRecordByUserId(Long userId) {
		return this.paymentVerifyRecordRepository.getPaymentVerifyRecordByUserId(userId);
	}

	@Override
	public void savePaymentVerifyRecord(Long userId, int verifyType) {
		PaymentVerifyRecord record  = this.getPaymentVerifyRecordByUserId(userId);
		if(record == null || record.getRecordId() == null) {
			record = new PaymentVerifyRecord ();
			record.setUserId(userId);
			record.setCreateTime(DateUtil.getCurrentDate());
		}
		
		record.setVerifyType(verifyType);
		record.setVerifyTime(DateUtil.getCurrentDate());
		this.save(record);
	}
}
