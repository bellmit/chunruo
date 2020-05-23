package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserProfitRecord;

@Repository("userProfitRecordRepository")
public interface UserProfitRecordRepository extends GenericRepository<UserProfitRecord, Long> {
	
	@Query("from UserProfitRecord where userId=:userId order by createTime desc")
	public List<UserProfitRecord> getUserProfitRecordList(@Param("userId")Long userId);
	
	@Query("from UserProfitRecord where orderNo=:orderNo order by createTime desc")
	public List<UserProfitRecord> getUserProfitRecordByOrderNo(@Param("orderNo")String orderNo);
	
	@Query("from UserProfitRecord where updateTime >:updateTime")
	public List<UserProfitRecord> getUserProfitRecordListByUpdateTime(@Param("updateTime")Date updateTime);
	
	@Query("from UserProfitRecord where orderId=:orderId order by createTime desc")
	public List<UserProfitRecord> getUserProfitRecordByOrderId(@Param("orderId")Long orderId);

	@Query("from UserProfitRecord where status=:status order by createTime desc")
	public List<UserProfitRecord> getUserProfitRecordByStatus(@Param("status") int status);
	
	@Query("from UserProfitRecord where orderId=:orderId and userId=:userId")
	public List<UserProfitRecord> getUserProfitRecordByOrderId(@Param("orderId") Long orderId, @Param("userId") Long userId);
	
	@Modifying
	@Query("update UserProfitRecord set status =:status, updateTime =:modiDate where orderId in (:orderIdList)")
	public void updateUserProfitRecordStatusByOrderIdList(@Param("orderIdList") List<Long> orderIdList, @Param("status")Integer status, @Param("modiDate") Date modiDate);
	
	@Modifying
	@Query("update UserProfitRecord set status =:status, updateTime =:modiDate where orderId =:orderId")
	public void updateUserProfitRecordStatusByOrderId(@Param("orderId") Long orderId, @Param("status")Integer status, @Param("modiDate") Date modiDate);

	@Query("from UserProfitRecord where fromUserId=:fromUserId order by createTime desc")
	public List<UserProfitRecord> getUserProfitRecordListByFromUserId(@Param("fromUserId")Long fromUserId);

	@Modifying
	@Query("update UserProfitRecord set status =:status,income=:income,updateTime =:modiDate where orderId =:orderId")
	public void updateUserProfitRecordStatusByOrderId(@Param("orderId") Long orderId, @Param("status")Integer status,@Param("income")Double income, @Param("modiDate") Date modiDate);

	@Query("from UserProfitRecord where status=3 and type=5 and mtype=3 and DATE_FORMAT( updateTime, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' )")
	public List<UserProfitRecord> getUserProfitRecordListByCurrentMonth();

	@Query("from UserProfitRecord where fromUserId in(:fromUserIdList)")
	public List<UserProfitRecord> getUserProfitRecordListByFromUserIdList(@Param("fromUserIdList")List<Long> fromUserIdList);

}
