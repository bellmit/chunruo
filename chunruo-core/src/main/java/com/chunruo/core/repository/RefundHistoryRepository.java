package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.RefundHistory;

@Repository("refundHistoryRepository")
public interface RefundHistoryRepository extends GenericRepository<RefundHistory, Long> {
	
	@Query("from RefundHistory where refundId=:refundId")
	public List<RefundHistory> getRefundHistoryListByRefundId(@Param("refundId") Long refundId);
	
}
