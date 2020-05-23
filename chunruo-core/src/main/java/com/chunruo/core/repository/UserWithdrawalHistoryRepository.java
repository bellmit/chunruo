package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserWithdrawalHistory;

@Repository("userWithdrawalHistoryRepository")
public interface UserWithdrawalHistoryRepository extends GenericRepository<UserWithdrawalHistory, Long> {

	@Query("from UserWithdrawalHistory where recordId=:recordId order by createTime")
	public List<UserWithdrawalHistory> getUserWithdrawalHistoryListByRecordId(@Param("recordId") Long recordId);
}
