package com.chunruo.core.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.PushMessage;
import com.chunruo.core.repository.PushMessageRepository;
import com.chunruo.core.service.PushMessageManager;
import com.chunruo.core.util.DateUtil;

@Transactional
@Component("pushMessageManager")
public class PushMessageManagerImpl extends GenericManagerImpl<PushMessage, Long> implements PushMessageManager{
	private PushMessageRepository pushMessageRepository;

	@Autowired
	public PushMessageManagerImpl(PushMessageRepository pushMessageRepository) {
		super(pushMessageRepository);
		this.pushMessageRepository = pushMessageRepository;
	}
	
	@Override
	public boolean isExistPushMessage(Long objectId, Long userId, Integer msgType){
		List<PushMessage> list = this.getPushMessageList(objectId, userId, msgType);
		if(list != null && list.size() > 0){
			return true;
		}
		return false;
	}

	@Override
	public List<PushMessage> getPushMessageList(Long objectId, Long userId, Integer msgType) {
		return this.pushMessageRepository.getPushMessageList(objectId, userId, msgType);
	}
	
	@Override
	public List<PushMessage> getPushMessageList(Long objectId, Long userId, Integer msgType,Integer logisticsStatus) {
		return this.pushMessageRepository.getPushMessageList(objectId, userId, msgType,logisticsStatus);
	}

	@Override
	public List<PushMessage> getPushMessageListByRelationId(Long relationId, Integer msgType) {
		return this.pushMessageRepository.getPushMessageListByRelationId(relationId, msgType);
	}

	@Override
	public List<PushMessage> getPushMessageListByUserId(Long userId) {
		return this.pushMessageRepository.getPushMessageListByUserId(userId);
	}

	@Override
	public List<PushMessage> getNoPushMessageListByCreateTime(Date createTime) {
		List<PushMessage> pushMessageList = this.pushMessageRepository.getNoPushMessageListByIsPushMsg(createTime);
		if(pushMessageList != null && pushMessageList.size() > 0){
			for(PushMessage pushMessage : pushMessageList){
				pushMessage.setIsPushMsg(true);
				pushMessage.setUpdateTime(DateUtil.getCurrentDate());
			}
			pushMessageList = this.pushMessageRepository.batchInsert(pushMessageList, pushMessageList.size());
		}
		return pushMessageList;
	}
	
	@Override
	public boolean batchInsertPushMessage(List<PushMessage> modelList, int commitPerCount) {
		Long begin = new Date().getTime();  // ????????????  
		boolean result = true;
		Connection conn = null;
		int commitCount = (commitPerCount / 5000); //5000?????????????????????,???????????????

		try { 
			StringBuffer sqlPrefixBuffer = new StringBuffer();
			sqlPrefixBuffer.append("insert into jkd_push_message(");
			sqlPrefixBuffer.append("object_id,");
			sqlPrefixBuffer.append("title,"); 
			sqlPrefixBuffer.append("is_push_msg,"); 
			sqlPrefixBuffer.append("is_system_msg,"); 
			sqlPrefixBuffer.append("msg_content,");
			sqlPrefixBuffer.append("msg_type,"); 
			sqlPrefixBuffer.append("user_id,"); 
			sqlPrefixBuffer.append("relation_id,"); 
			sqlPrefixBuffer.append("object_type,");
			sqlPrefixBuffer.append("image_url,"); 
			sqlPrefixBuffer.append("create_time,"); 
			sqlPrefixBuffer.append("update_time"); 
			sqlPrefixBuffer.append(") values ");
			
			conn = this.getJdbcTemplate().getDataSource().getConnection();
			StringBuffer suffix = new StringBuffer();  
			conn.setAutoCommit(false);   // ??????????????????????????????  
			// ??????st???pst????????????  
			PreparedStatement pst = conn.prepareStatement("");  
			// ????????????????????????????????????  
			for (int i = 0; i <= commitCount; i++) {  
				// ???N???????????????  
				List<PushMessage> subUserList = modelList.subList(5000 * i, 5000 * (i + 1) > commitPerCount ? commitPerCount : 5000 * (i + 1));
				for (int j = 0; j < subUserList.size(); j++) { 
					Date currentDate = DateUtil.getCurrentDate();
					PushMessage model = subUserList.get(j);
					// ??????sql
					suffix.append("(");
					suffix.append(model.getObjectId() + ",");
					suffix.append(String.format("'%s',", model.getTitle()));
					suffix.append(model.getIsPushMsg() + ",");
					suffix.append(model.getIsSystemMsg() + ",");
					suffix.append(String.format("'%s',", model.getMsgContent()));
					suffix.append(model.getMsgType() + ",");
					suffix.append(model.getUserId() + ",");
					suffix.append(model.getRelationId() + ",");
					suffix.append(model.getObjectType() + ",");
					suffix.append(String.format("'%s',", model.getImageUrl()));
					suffix.append(String.format("'%s',", DateUtil.formatDate(DateUtil.DATE_FORMAT, currentDate)));
					suffix.append(String.format("'%s'", DateUtil.formatDate(DateUtil.DATE_FORMAT, currentDate)));
					suffix.append("),");
				}  

				String sql = sqlPrefixBuffer.toString() + suffix.substring(0, suffix.length() - 1);  
				pst.addBatch(sql);   // ????????????sql  
				pst.executeBatch();  // ????????????  
				conn.commit();   // ????????????  
				suffix = new StringBuffer();   // ??????????????????????????????  
			}  
			pst.close();  
		} catch (SQLException e) {  
			result = false;
			try {  
				conn.rollback(); //??????????????????  
			} catch (SQLException ex) {   
			}
			e.printStackTrace();  
		} finally{ 
			try {
				conn.close();  //????????????
			} catch (SQLException e) {
				e.printStackTrace();
				log.error("!!!!!!!!!!!close onnection fail!!!!!!!!!!!!!!!!");
			}
		}

		Long end = new Date().getTime();  
		// ??????  
		log.debug(String.format("batchInsertPushMessage==>>[rows: %s, time: %sms]", commitPerCount, end - begin));
		return result;
	}

	@Override
	public List<PushMessage> getPushMessageListByObjectIdAndMsgType(Long objectId,Long relationId, Integer msgType) {
		return this.pushMessageRepository.getPushMessageListByObjectIdAndMsgType(objectId, relationId,msgType);
	}

	@Override
	public boolean isExistPushMessage(Long objectId, Long userId, Integer msgType, Integer logisticsStatus) {
		List<PushMessage> list = this.getPushMessageList(objectId, userId, msgType,logisticsStatus);
		if(list != null && list.size() > 0){
			return true;
		}
		return false;
	}

	@Override
	public List<PushMessage> getPushMessageListByChildMsgType(Long objectId, Long userId, Integer msgType,
			Integer childMsgType) {
		return this.pushMessageRepository.getPushMessageListByChildMsgType(objectId,userId,msgType,childMsgType);
	}

	@Override
	public List<PushMessage> getPushMessageListByUpdateTime(Date updateTime) {
		return this.pushMessageRepository.getPushMessageListByUpdateTime(updateTime);
	}
}
