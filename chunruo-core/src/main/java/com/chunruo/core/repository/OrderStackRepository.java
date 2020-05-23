package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderStack;

@Repository("orderStackRepository")
public interface OrderStackRepository extends GenericRepository<OrderStack, Long> {

	@Query("from OrderStack where userId=:userId order by createTime")
	public List<OrderStack> getOrderStackListByUserId(@Param("userId") Long userId);
	
	@Query("from OrderStack where groupKey=:groupKey order by createTime")
	public List<OrderStack> getOrderStackListByGroupKey(@Param("groupKey") String groupKey);
}
