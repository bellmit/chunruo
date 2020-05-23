package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.InviteTask;

public interface InviteTaskManager extends GenericManager<InviteTask, Long> {

	public List<InviteTask> getInviteTaskListByUpdateTime(Date updateTime);
}
