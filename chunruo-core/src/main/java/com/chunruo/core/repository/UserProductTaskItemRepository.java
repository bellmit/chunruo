package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserProductTaskItem;

@Transactional
@Repository("userProductTaskItemRepository")
public interface UserProductTaskItemRepository extends GenericRepository<UserProductTaskItem, Long> {

	@Query("from UserProductTaskItem where taskId=:taskId and userId=:userId")
	List<UserProductTaskItem> getUserProductTaskItemListById(@Param("taskId")Long taskId, @Param("userId")Long userId);

	@Query("from UserProductTaskItem where orderId=:orderId ")
	List<UserProductTaskItem> getUserProductTaskItemListByOrderId(@Param("orderId")Long orderId);

	@Query("from UserProductTaskItem where orderItemId=:orderItemId ")
	UserProductTaskItem getUserProductTaskItemListByOrderItemId(@Param("orderItemId")Long orderItemId);

	@Query("from UserProductTaskItem where orderItemId in(:orderItemIdList) ")
	List<UserProductTaskItem> getUserProductTaskItemListByOrderItemIdList(@Param("orderItemIdList")List<Long> orderItemIdList);

	@Modifying
	@Query("update UserProductTaskItem set status=:status,update_time=now() where itemId in(:itemIdList) ")
	void updateUserProductTaskItemByItemIdList(@Param("status")Integer status, @Param("itemIdList")List<Long> itemIdList);

	@Query("from UserProductTaskItem where userId=:userId")
	List<UserProductTaskItem> getUserProductTaskItemListByUserId(@Param("userId")Long userId);

	@Query("from UserProductTaskItem where updateTime>:updateTime")
	List<UserProductTaskItem> getUserProductTaskItemListByUpdateTime(@Param("updateTime")Date updateTime);

}
