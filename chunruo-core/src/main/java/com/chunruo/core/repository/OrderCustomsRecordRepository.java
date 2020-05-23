package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderCustomsRecord;

@Repository("orderCustomsRecordRepository")
public interface OrderCustomsRecordRepository extends GenericRepository<OrderCustomsRecord, Long> {
	
	@Query("from OrderCustomsRecord where orderNo =:orderNo")
	public List<OrderCustomsRecord> getOrderCustomsRecordListByOrderNo(@Param("orderNo") String orderNo);
	
	@Query("from OrderCustomsRecord where batchNumber =:batchNumber")
	public List<OrderCustomsRecord> getOrderCustomsRecordListByBatchNumber(@Param("batchNumber") String batchNumber);
}
