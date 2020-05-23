package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.InviteTask;

@Repository("inviteTaskRepository")
public interface InviteTaskRepository extends GenericRepository<InviteTask, Long> {

	@Query("from InviteTask where updateTime >:updateTime")
	public List<InviteTask> getInviteTaskListByUpdateTime(@Param("updateTime")Date updateTime);
}
