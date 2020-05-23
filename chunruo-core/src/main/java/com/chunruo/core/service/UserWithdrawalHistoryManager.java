package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserWithdrawalHistory;

public interface UserWithdrawalHistoryManager extends GenericManager<UserWithdrawalHistory, Long>{

	public List<UserWithdrawalHistory> getUserWithdrawalHistoryListByRecordId(Long recordId);
}
