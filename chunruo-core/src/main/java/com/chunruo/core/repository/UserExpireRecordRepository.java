package com.chunruo.core.repository;

import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserExpireRecord;

@Repository("userExpireRecordRepository")
public interface UserExpireRecordRepository extends GenericRepository<UserExpireRecord, Long> {

	
}
