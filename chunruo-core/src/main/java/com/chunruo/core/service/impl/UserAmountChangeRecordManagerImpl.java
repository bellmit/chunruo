package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserAmountChangeRecord;
import com.chunruo.core.repository.UserAmountChangeRecordRepository;
import com.chunruo.core.service.UserAmountChangeRecordManager;

@Transactional
@Component("userAmountChangeRecordManager")
public class UserAmountChangeRecordManagerImpl extends GenericManagerImpl<UserAmountChangeRecord, Long> implements UserAmountChangeRecordManager{
	private UserAmountChangeRecordRepository userAmountChangeRecordRepository;

	@Autowired
	public UserAmountChangeRecordManagerImpl(UserAmountChangeRecordRepository userAmountChangeRecordRepository) {
		super(userAmountChangeRecordRepository);
		this.userAmountChangeRecordRepository = userAmountChangeRecordRepository;
	}

	@Override
	public List<UserAmountChangeRecord> getUserAmountChangeRecordByObjectId(Long objectId, Integer type) {
		return this.userAmountChangeRecordRepository.getUserAmountChangeRecordByObjectId(objectId, type);
	}
}
