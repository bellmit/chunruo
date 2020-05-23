package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.InviteTask;
import com.chunruo.core.repository.InviteTaskRepository;
import com.chunruo.core.service.InviteTaskManager;

@Component("inviteTaskManager")
public class InviteTaskManagerImpl extends GenericManagerImpl<InviteTask, Long> implements InviteTaskManager{
	private InviteTaskRepository inviteTaskRepository;

	@Autowired
	public InviteTaskManagerImpl(InviteTaskRepository inviteTaskRepository) {
		super(inviteTaskRepository);
		this.inviteTaskRepository = inviteTaskRepository;
	}

	@Override
	public List<InviteTask> getInviteTaskListByUpdateTime(Date updateTime) {
		return this.inviteTaskRepository.getInviteTaskListByUpdateTime(updateTime);
	}
}
