package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.InviteTaskRecord;
import com.chunruo.core.repository.InviteTaskRecordRepository;
import com.chunruo.core.service.InviteTaskRecordManager;

@Component("inviteTaskRecordManager")
public class InviteTaskRecordManagerImpl extends GenericManagerImpl<InviteTaskRecord, Long> implements InviteTaskRecordManager{
	private InviteTaskRecordRepository inviteTaskRecordRepository;

	@Autowired
	public InviteTaskRecordManagerImpl(InviteTaskRecordRepository inviteTaskRecordRepository) {
		super(inviteTaskRecordRepository);
		this.inviteTaskRecordRepository = inviteTaskRecordRepository;
	}

	@Override
	public InviteTaskRecord getInviteTaskRecordByUserId(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InviteTaskRecord getInviteTaskRecordByUserIdAndMonthDate(Long userId, String monthDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
