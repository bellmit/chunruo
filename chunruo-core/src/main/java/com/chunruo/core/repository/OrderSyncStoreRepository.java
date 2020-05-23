package com.chunruo.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderSyncStore;

@Repository("orderSyncStoreRepository")
public interface OrderSyncStoreRepository extends GenericRepository<OrderSyncStore, Long> {

	@Modifying
	@Query("update OrderSyncStore set lastSyncTime =:lastSyncTime, updateTime =:modiDate where appStoreId =:appStoreId")
	public void updateOrderSyncStoreLastSyncTime(@Param("appStoreId") Long appStoreId, @Param("lastSyncTime")Date lastSyncTime, @Param("modiDate")Date updateTime);
}
