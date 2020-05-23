package com.chunruo.core.service;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.InviteTaskRecord;

public interface InviteTaskRecordManager extends GenericManager<InviteTaskRecord, Long> {

	public InviteTaskRecord getInviteTaskRecordByUserId(Long userId);

	public InviteTaskRecord getInviteTaskRecordByUserIdAndMonthDate(Long userId, String monthDate);
}
