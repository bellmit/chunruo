package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.PushMessage;

@Repository("pushMessageRepository")
public interface PushMessageRepository extends GenericRepository<PushMessage, Long> {
	
	@Query("from PushMessage where relationId=:relationId and msgType=:msgType")
	public List<PushMessage> getPushMessageListByRelationId(@Param("relationId") Long relationId, @Param("msgType") Integer msgType);
	
	@Query("from PushMessage where objectId=:objectId and userId=:userId and msgType=:msgType")
	public List<PushMessage> getPushMessageList(@Param("objectId") Long objectId, @Param("userId") Long userId, @Param("msgType") Integer msgType);
	
	@Query("from PushMessage where isPushMsg = true and userId=:userId")
	public List<PushMessage> getPushMessageListByUserId(@Param("userId") Long userId);
	
	@Query("from PushMessage where isPushMsg = false and createTime <:createTime")
	public List<PushMessage> getNoPushMessageListByIsPushMsg(@Param("createTime") Date createTime);
	
	@Query("from PushMessage where objectId=:objectId and msgType=:msgType and relationId=:relationId")
	public List<PushMessage> getPushMessageListByObjectIdAndMsgType(@Param("objectId") Long objectId, @Param("relationId") Long relationId,@Param("msgType") Integer msgType);

	@Query("from PushMessage where objectId=:objectId and userId=:userId and msgType=:msgType and logisticsStatus = :logisticsStatus")
	public List<PushMessage> getPushMessageList(@Param("objectId")Long objectId, @Param("userId") Long userId, @Param("msgType")Integer msgType, @Param("logisticsStatus")Integer logisticsStatus);

	@Query("from PushMessage where objectId=:objectId and userId=:userId and msgType=:msgType and childMsgType = :childMsgType")
	public List<PushMessage> getPushMessageListByChildMsgType(@Param("objectId")Long objectId, @Param("userId") Long userId, @Param("msgType")Integer msgType, @Param("childMsgType")Integer childMsgType);

	@Query("from PushMessage where  updateTime >:updateTime")
	public List<PushMessage> getPushMessageListByUpdateTime(@Param("updateTime")Date updateTime);
}
