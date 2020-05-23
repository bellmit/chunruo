package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderSync;

@Repository("orderSyncRepository")
public interface OrderSyncRepository extends GenericRepository<OrderSync, Long> {
	
	@Query("from OrderSync where orderNumber =:orderNumber order by createTime desc")
	public List<OrderSync> getOrderSyncListByOrderNumber(@Param("orderNumber")String orderNumber);
	
	@Query("from OrderSync where orderNumber =:orderNumber and isHandler =:isHandler order by createTime desc")
	public List<OrderSync> getOrderSyncListByOrderNumber(@Param("orderNumber")String orderNumber, @Param("isHandler")Boolean isHandler);

	@Query("from OrderSync where orderNumber in (:orderNumberList) order by createTime desc")
	public List<OrderSync> getOrderSyncListByOrderNumberList(@Param("orderNumberList")List<String> orderNumberList);
	
	@Query("from OrderSync where batchNumber =:batchNumber")
	public List<OrderSync> getOrderSyncListByBatchNumber(@Param("batchNumber")String batchNumber);
	
	@Transactional
	@Modifying
	@Query("update OrderSync set isSyncSucc =:isSyncSucc, updateTime =:modiDate where recordId =:recordId")
	public void updateOrderSyncStatusById(@Param("recordId")Long recordId, @Param("isSyncSucc")Boolean isSyncSucc, @Param("modiDate") Date modiDate);

}
