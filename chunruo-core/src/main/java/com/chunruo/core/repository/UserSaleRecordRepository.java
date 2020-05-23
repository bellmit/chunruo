package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserSaleRecord;

@Repository("userSaleRecordRepository")
public interface UserSaleRecordRepository extends GenericRepository<UserSaleRecord, Long> {

	@Query("from UserSaleRecord where userId=:userId")
	public List<UserSaleRecord> getUserSaleRecordListByUserId(@Param("userId")Long userId);

	@Query("from UserSaleRecord where updateTime > :updateTime")
	public List<UserSaleRecord> getUserSaleRecordListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from UserSaleRecord where orderId=:orderId")
	public UserSaleRecord getUserSaleRecordByOrderId(@Param("orderId")Long orderId);

	@Modifying
	@Query("update UserSaleRecord set orderStatus=:orderStatus,update_time = now() where orderId in(:orderIdList)")
	public void updateUserSaleRecordByStatus(@Param("orderIdList")List<Long> orderIdList, @Param("orderStatus")Integer status);

	@Query("from UserSaleRecord where orderId in(:orderIdList)")
	public List<UserSaleRecord> getUserSaleRecordListByOrderIdList(@Param("orderIdList")List<Long> orderIdList);

	
}
