package com.chunruo.core.repository;

import java.util.Date;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderSyncList;

@Repository("orderSyncListRepository")
public interface OrderSyncListRepository extends GenericRepository<OrderSyncList, Long> {
	
	@Query("select max(endTime) from OrderSyncList where status = true")
	public Date getOrderSyncListMaxEndTime();

}
