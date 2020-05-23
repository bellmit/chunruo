package com.chunruo.core.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.RefundRequestRecord;

@Repository("refundRequestRecordRepository")
public interface RefundRequestRecordRepository extends GenericRepository<RefundRequestRecord, Long> {

	@Query("from RefundRequestRecord where orderNo=:orderNo and refundNumber=:refundNumber")
	public RefundRequestRecord getRefundRequestRecordByOrderNoAndRefundNumber(@Param("orderNo")String orderNo, @Param("refundNumber")String refundNumber);
}
