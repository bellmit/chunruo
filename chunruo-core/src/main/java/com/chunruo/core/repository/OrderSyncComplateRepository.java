package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderSyncComplate;

@Repository("orderSyncComplateRepository")
public interface OrderSyncComplateRepository extends GenericRepository<OrderSyncComplate, Long> {
	
	@Query("from OrderSyncComplate where batchNumber =:batchNumber")
	public List<OrderSyncComplate> getOrderSyncComplateListByBatchNumber(@Param("batchNumber")String batchNumber);
}
