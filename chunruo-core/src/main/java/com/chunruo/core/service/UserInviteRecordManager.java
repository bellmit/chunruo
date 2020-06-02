package com.chunruo.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserInviteRecord;
import com.chunruo.core.vo.MsgModel;

public interface UserInviteRecordManager extends GenericManager<UserInviteRecord, Long>{
	
	public UserInviteRecord getUserInviteRecordByRecordNo(String recordNo);
	
	public List<UserInviteRecord> getUserInviteRecordByUserId(Long userId);
	
	public List<UserInviteRecord> getValidInvitNumberByTopUserId(Long topUserId, Integer inviteType);
	
	public MsgModel<Map<String, List<Long>>> insertInviteRecordByOrder(Order order);
	
	public MsgModel<String> insertBuyInviteRecord(UserInfo userInfo);
	
	public List<UserInviteRecord> getValidInvitNumberByTopUserId(Long topUserId, Integer inviteType, Date beginDate, Date endDate);
	
	public List<UserInviteRecord> getUserInviteRecordListByCreateTime( Date beginDate, Date endDate);
	
	public List<UserInviteRecord> getUserInviteRecordListByCurrentMonth();
	
	public List<UserInviteRecord> getUserInviteRecordList();
	
	public MsgModel<Map<String, List<Long>>> insertInviteRecordByIsFreeMemberOrder(Order order);

	public List<UserInviteRecord> getUserInviteRecordListByUserIdList(List<Long> userIdList);

}
