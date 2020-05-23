package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserProductTaskRecord;

@Repository("userProductTaskRecordRepository")
public interface UserProductTaskRecordRepository extends GenericRepository<UserProductTaskRecord, Long> {

	@Query("from UserProductTaskRecord where userId=:userId")
	List<UserProductTaskRecord> getUserProductTaskRecordListByUserId(@Param("userId")Long userId);

	@Query("from UserProductTaskRecord where updateTime>:updateTime")
	List<UserProductTaskRecord> getUserProductTaskRecordListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from UserProductTaskRecord where userId=:userId and taskId=:taskId")
	UserProductTaskRecord getUserProductTaskRecordById(@Param("userId")Long userId,@Param("taskId")Long taskId);

	@Query("from UserProductTaskRecord where userId=:userId and taskId in (:taskIdList)")
	List<UserProductTaskRecord> getUserProductTaskRecordById(@Param("userId")Long userId,@Param("taskIdList")List<Long> taskIdList);

	@Query("from UserProductTaskRecord where taskId in (:taskIdList)")
	List<UserProductTaskRecord> getUserProductTaskListByTaskIdList(@Param("taskIdList")List<Long> taskIdList);
}
