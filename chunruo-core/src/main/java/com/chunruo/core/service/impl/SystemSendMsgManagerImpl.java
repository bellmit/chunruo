package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.SystemSendMsg;
import com.chunruo.core.repository.SystemSendMsgRepository;
import com.chunruo.core.service.SystemSendMsgManager;

@Transactional
@Component("systemSendMsgManager")
public class SystemSendMsgManagerImpl extends GenericManagerImpl<SystemSendMsg, Long> implements SystemSendMsgManager{
	private SystemSendMsgRepository systemSendMsgRepository;

	@Autowired
	public SystemSendMsgManagerImpl(SystemSendMsgRepository systemSendMsgRepository) {
		super(systemSendMsgRepository);
		this.systemSendMsgRepository = systemSendMsgRepository;
	}

	@Override
	public List<SystemSendMsg> getMsgListByUpdateTime(Date updateTime) {
		return this.systemSendMsgRepository.getMsgListByUpdateTime(updateTime);
	}


}
