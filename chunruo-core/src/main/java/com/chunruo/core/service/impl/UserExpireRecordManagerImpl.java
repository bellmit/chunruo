package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserExpireRecord;
import com.chunruo.core.repository.UserExpireRecordRepository;
import com.chunruo.core.service.UserExpireRecordManager;

@Component("userExpireRecordManager")
public class UserExpireRecordManagerImpl extends GenericManagerImpl<UserExpireRecord, Long> implements UserExpireRecordManager{
	private UserExpireRecordRepository userExpireRecordRepository;

	@Autowired
	public UserExpireRecordManagerImpl(UserExpireRecordRepository userExpireRecordRepository) {
		super(userExpireRecordRepository);
		this.userExpireRecordRepository = userExpireRecordRepository;
	}

	
}
