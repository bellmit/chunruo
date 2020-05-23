package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ScheduleTask;
import com.chunruo.core.repository.ScheduleTaskRepository;
import com.chunruo.core.service.ScheduleTaskManager;

@Component("scheduleTaskManager")
public class ScheduleTaskManagerImpl extends GenericManagerImpl<ScheduleTask, Long> implements ScheduleTaskManager{
	private ScheduleTaskRepository scheduleTaskRepository;

	@Autowired
	public ScheduleTaskManagerImpl(ScheduleTaskRepository scheduleTaskRepository) {
		super(scheduleTaskRepository);
		this.scheduleTaskRepository = scheduleTaskRepository;
	}

	@Override
	public List<ScheduleTask> getScheduleTaskListByIsEnable(Boolean isEnable) {
		return this.scheduleTaskRepository.getScheduleTaskListByIsEnable(isEnable);
	}


}
