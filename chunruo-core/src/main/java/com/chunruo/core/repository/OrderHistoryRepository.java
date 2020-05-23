package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderHistory;

@Repository("orderHistoryRepository")
public interface OrderHistoryRepository extends GenericRepository<OrderHistory, Long> {

	@Query("from OrderHistory where orderId=:orderId order by createTime")
	public List<OrderHistory> getOrderHistoryListByOrderId(@Param("orderId") Long orderId);
}
