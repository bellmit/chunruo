package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderLockStock;

@Repository("orderLockStockRepository")
public interface OrderLockStockRepository extends GenericRepository<OrderLockStock, Long> {
	
	@Query("from OrderLockStock where productId=:productId")
	public List<OrderLockStock> getOrderLockStockListByProductId(@Param("productId") Long productId);
	
	@Query("from OrderLockStock where status=:status and productId in (:productIdList)")
	public List<OrderLockStock> getOrderLockStockListByProductIdList(@Param("productIdList") List<Long> productIdList, @Param("status")Boolean status);
	
	@Query("from OrderLockStock where productId=:productId and status=:status")
	public List<OrderLockStock> getOrderLockStockListByProductId(@Param("productId") Long productId, @Param("status")Boolean status);

	@Query("from OrderLockStock where orderId=:orderId")
	public List<OrderLockStock> getOrderLockStockListByOrderId(@Param("orderId") Long orderId);
	
	@Modifying 
	@Query("delete from OrderLockStock where orderId=:orderId")
	public void deleteOrderLockStockByOrderId(@Param("orderId")Long orderId);
	
	@Query("from OrderLockStock where updateTime >:updateTime")
	public List<OrderLockStock> getOrderLockStockListByUpdateTime(@Param("updateTime")Date updateTime);
	
	@Query("from OrderLockStock where status=:status and createTime <=:createTime")
	public List<OrderLockStock> getOrderLockStockListByCreateTime(@Param("status")Boolean status, @Param("createTime")Date createTime);
}
