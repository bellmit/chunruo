package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserAmountChangeRecord;

@Repository("userAmountChangeRecordRepository")
public interface UserAmountChangeRecordRepository extends GenericRepository<UserAmountChangeRecord, Long> {

	@Query("from UserAmountChangeRecord where objectId =:objectId and type =:type")
	public List<UserAmountChangeRecord> getUserAmountChangeRecordByObjectId(@Param("objectId") Long objectId, @Param("type") Integer type);
}
