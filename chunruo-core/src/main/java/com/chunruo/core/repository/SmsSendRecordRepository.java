package com.chunruo.core.repository;

import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.SmsSendRecord;

@Repository("smsSendRecordRepository")
public interface SmsSendRecordRepository extends GenericRepository<SmsSendRecord, Long> {
}
