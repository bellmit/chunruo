package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserWithdrawal;

@Repository("userWithdrawalRepository")
public interface UserWithdrawalRepository extends GenericRepository<UserWithdrawal, Long> {

	@Query("from UserWithdrawal where userId=:userId order by createTime desc")
	public List<UserWithdrawal> getUserWithdrawalListByUserId(@Param("userId")Long userId);
	
	@Query("from UserWithdrawal where updateTime >:updateTime")
	public List<UserWithdrawal> getUserWithdrawalListByUpdateTime(@Param("updateTime")Date updateTime);
	
	@Query("from UserWithdrawal where (createTime BETWEEN :beginDate and :endDate) and status=:status order by createTime desc")
	public List<UserWithdrawal> getUserWithdrawalListByTime(@Param("beginDate")Date beginDate,@Param("endDate")Date endDate,@Param("status") Integer status);
	
	@Query("from UserWithdrawal where (createTime BETWEEN :beginDate and :endDate) order by createTime desc")
	public List<UserWithdrawal> getUserWithdrawalListByTime(@Param("beginDate")Date beginDate,@Param("endDate")Date endDate);
}
