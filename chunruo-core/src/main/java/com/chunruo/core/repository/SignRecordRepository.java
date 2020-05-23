package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.SignRecord;

@Repository("signRecordRepository")
public interface SignRecordRepository extends GenericRepository<SignRecord, Long>{

	@Query("from SignRecord where userId=:userId order by signDate desc")
	List<SignRecord> getSignRecordListByUserId(@Param("userId") Long userId);

	@Query("from SignRecord where updateTime>:updateTime")
	List<SignRecord> getSignRecordListByUpdateTime(@Param("updateTime")Date updateTime);
}
