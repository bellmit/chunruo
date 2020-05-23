package com.chunruo.core.repository;

import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.InviteTaskRecord;

@Repository("inviteTaskRecordRepository")
public interface InviteTaskRecordRepository extends GenericRepository<InviteTaskRecord, Long> {

}
