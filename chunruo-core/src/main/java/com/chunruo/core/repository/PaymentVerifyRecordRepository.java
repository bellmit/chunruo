package com.chunruo.core.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.PaymentVerifyRecord;

@Repository("paymentVerifyRecordRepository")
public interface PaymentVerifyRecordRepository extends GenericRepository<PaymentVerifyRecord, Long> {

	@Query("from PaymentVerifyRecord where userId =:userId")
	public PaymentVerifyRecord getPaymentVerifyRecordByUserId(@Param("userId") Long userId);
}
