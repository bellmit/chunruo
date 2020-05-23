package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserFriend;

public interface UserFriendManager extends GenericManager<UserFriend, Long> {

	public UserFriend getUserFriendByFriendUserId(Long friendUserId);

	public List<UserFriend> getUserFriendListByUserId(Long userId);

	public List<UserFriend> getUserFriendListByUpdateTime(Date updateTime);

	public List<UserFriend> getUserFriendListByUserIdList(List<Long> userIdList);

	public void deleteUserFriendByOperatorId(Long operatorId);
	
	public List<UserFriend> getUserFriendByUserIdAndFriendUserIdList(Long userId,List<Long> friendUserIdList);

	public void deleteUserFriendByFriendUserIdList(List<Long> friendUserIdList);

	public List<UserFriend> getUserFriendByFriendUserIdList(List<Long> validUserIdList);

	public List<UserFriend> getUserFriendByStatus(Integer status);
	
	public List<UserFriend> getUserFriendByType(Integer type);

	public void removeUserFriend(UserFriend userFriend);

	public void deleteUserFriendByUserId(Long userId);


}
