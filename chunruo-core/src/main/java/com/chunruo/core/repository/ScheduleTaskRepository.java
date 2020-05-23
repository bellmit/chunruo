package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ScheduleTask;

@Repository("scheduleTaskRepository")
public interface ScheduleTaskRepository extends GenericRepository<ScheduleTask, Long> {

	@Query("from ScheduleTask where isEnable=:isEnable and beginTime>=now() and isDelete=false ")
	List<ScheduleTask> getScheduleTaskListByIsEnable(@Param("isEnable")Boolean isEnable);

}
