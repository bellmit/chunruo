package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.SystemSendMsg;

@Repository("systemSendMsgRepository")
public interface SystemSendMsgRepository extends GenericRepository<SystemSendMsg, Long> {

	@Query("from SystemSendMsg where updateTime>:updateTime")
	List<SystemSendMsg> getMsgListByUpdateTime(@Param("updateTime")Date updateTime);
	
}
