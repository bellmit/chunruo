package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ScheduleTask;

public interface ScheduleTaskManager extends GenericManager<ScheduleTask, Long> {

	public List<ScheduleTask> getScheduleTaskListByIsEnable(Boolean isEnable);
	

}
