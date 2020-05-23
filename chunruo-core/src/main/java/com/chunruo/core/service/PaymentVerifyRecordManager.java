package com.chunruo.core.service;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.PaymentVerifyRecord;

public interface PaymentVerifyRecordManager extends GenericManager<PaymentVerifyRecord, Long>{

	public PaymentVerifyRecord getPaymentVerifyRecordByUserId(Long userId);
	
	public void savePaymentVerifyRecord(Long userId, int verifyType);
}
