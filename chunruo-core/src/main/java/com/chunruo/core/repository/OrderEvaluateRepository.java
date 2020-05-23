package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderEvaluate;

@Repository("orderEvaluateRepository")
public interface OrderEvaluateRepository extends GenericRepository<OrderEvaluate, Long> {

	@Query("from OrderEvaluate where itemId=:itemId")
	public OrderEvaluate getOrderEvaluateByItemId(@Param("itemId")Long itemId);
	
	@Query(value="select * from jkd_order_evaluate where user_id=:userId limit :limit", nativeQuery=true)
	public List<OrderEvaluate> getOrderEvaluateListByUserId(@Param("userId")Long userId, @Param("limit")int limit);
	
	@Query("from OrderEvaluate where productId=:productId")
	public List<OrderEvaluate> getOrderEvaluateListByProductId(@Param("productId")Long productId);

	@Query("from OrderEvaluate where updateTime >:updateTime")
	public List<OrderEvaluate> getOrderEvaluateListByUpdateTime(@Param("updateTime")Date updateTime);

	@Modifying
	@Query("update OrderEvaluate set status =:status, updateTime = now() where evaluateId in (:idList)")
	public void updateEvaluateStatusByIdList(@Param("idList")List<Long> idList, @Param("status")Integer status);

}
