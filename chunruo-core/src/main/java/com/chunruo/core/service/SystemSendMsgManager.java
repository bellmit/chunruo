package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.SystemSendMsg;

public interface SystemSendMsgManager extends GenericManager<SystemSendMsg, Long> {

	List<SystemSendMsg> getMsgListByUpdateTime(Date updateTime);



}
