package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserWithdrawalHistory;
import com.chunruo.core.repository.UserWithdrawalHistoryRepository;
import com.chunruo.core.service.UserWithdrawalHistoryManager;

@Transactional
@Component("storeWithdrawalHistoryManager")
public class UserWithdrawalHistoryManagerImpl extends GenericManagerImpl<UserWithdrawalHistory, Long> implements UserWithdrawalHistoryManager{
	private UserWithdrawalHistoryRepository userWithdrawalHistoryRepository;

	@Autowired
	public UserWithdrawalHistoryManagerImpl(UserWithdrawalHistoryRepository userWithdrawalHistoryRepository) {
		super(userWithdrawalHistoryRepository);
		this.userWithdrawalHistoryRepository = userWithdrawalHistoryRepository;
	}

	@Override
	public List<UserWithdrawalHistory> getUserWithdrawalHistoryListByRecordId(Long recordId) {
		return this.userWithdrawalHistoryRepository.getUserWithdrawalHistoryListByRecordId(recordId);
	}
}
