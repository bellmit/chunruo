package com.chunruo.core.repository;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserRecharge;

@Repository("userRechargeRepository")
public interface UserRechargeRepository extends GenericRepository<UserRecharge, Long> {

	@Modifying
	@Query("update UserRecharge set status=:status,refuseReason=:refuseReason,adminName=:adminName,updateTime=now() where recordId in(:recordIdList)")
	public void updateUserRechargeStatus(@Param("recordIdList")List<Long> recordIdList,@Param("status")Integer status,@Param("refuseReason")String refuseReason,@Param("adminName")String adminName);

	@Query("from UserRecharge where userId=:userId and status=:status")
	public List<UserRecharge> getUserRechargeListByUserIdAndStatus(@Param("userId")Long userId,@Param("status")Integer status);

	@Query("from UserRecharge where updateTime>:updateTime")
	public List<UserRecharge> getUserRechargeListByUpdateTime(@Param("updateTime")Date updateTime);

}
