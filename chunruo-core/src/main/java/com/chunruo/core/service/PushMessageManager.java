package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.PushMessage;

public interface PushMessageManager extends GenericManager<PushMessage, Long>{
	
	public boolean isExistPushMessage(Long objectId, Long userId, Integer msgType);
	
	public boolean isExistPushMessage(Long objectId, Long userId, Integer msgType,Integer logisticsStatus);

	public List<PushMessage> getPushMessageListByRelationId(Long relationId, Integer msgType);
	
	public List<PushMessage> getPushMessageList(Long objectId, Long userId, Integer msgType);
	
	public List<PushMessage> getPushMessageListByChildMsgType(Long objectId, Long userId, Integer msgType,Integer childMsgType);

	
	public List<PushMessage> getPushMessageList(Long objectId, Long userId, Integer msgType,Integer logisticsStatus); 
	
	public List<PushMessage> getPushMessageListByUserId(Long userId);
	
	public List<PushMessage> getNoPushMessageListByCreateTime(Date createTime);
	
	public boolean batchInsertPushMessage(List<PushMessage> modelList, int commitPerCount);
	
	public List<PushMessage> getPushMessageListByObjectIdAndMsgType(Long objectId,Long relationId,Integer msgType);

	public List<PushMessage> getPushMessageListByUpdateTime(Date updateTime);
}
