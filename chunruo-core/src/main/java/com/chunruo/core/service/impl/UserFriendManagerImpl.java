package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserFriend;
import com.chunruo.core.repository.UserFriendRepository;
import com.chunruo.core.service.UserFriendManager;
import com.chunruo.core.service.UserFriendTagManager;
import com.chunruo.core.util.StringUtil;

@Component("userFriendManager")
public class UserFriendManagerImpl extends GenericManagerImpl<UserFriend, Long> implements UserFriendManager{
	private UserFriendRepository userFriendRepository;
	
	@Autowired
	private UserFriendTagManager userFriendTagManager;

	@Autowired
	public UserFriendManagerImpl(UserFriendRepository userFriendRepository) {
		super(userFriendRepository);
		this.userFriendRepository = userFriendRepository;
	}

	@Override
	public UserFriend getUserFriendByFriendUserId(Long friendUserId) {
		return this.userFriendRepository.getUserFriendByFriendUserId(friendUserId);
	}

	@Override
	public List<UserFriend> getUserFriendListByUserId(Long userId) {
		return this.userFriendRepository.getUserFriendListByUserId(userId);
	}

	@Override
	public List<UserFriend> getUserFriendListByUpdateTime(Date updateTime) {
		return this.userFriendRepository.getUserFriendListByUpdateTime(updateTime);
	}

	@Override
	public List<UserFriend> getUserFriendListByUserIdList(List<Long> userIdList) {
		if(userIdList == null || userIdList.isEmpty()) {
			return null;
		}
		return this.userFriendRepository.getUserFriendListByUserIdList(userIdList);
	}

	
	@Transactional
	@Override
	public void deleteUserFriendByOperatorId(Long operatorId) {
	}

	@Override
	public List<UserFriend> getUserFriendByUserIdAndFriendUserIdList(Long userId, List<Long> friendUserIdList) {
		if(friendUserIdList == null || friendUserIdList.isEmpty()) {
			return null;
		}
		return this.userFriendRepository.getUserFriendByFriendUserIdAndFriendUserIdList(userId,friendUserIdList);
	}

	@Override
	public void deleteUserFriendByFriendUserIdList(List<Long> friendUserIdList) {
		if(friendUserIdList != null && !friendUserIdList.isEmpty()) {
			this.userFriendRepository.deleteUserFriendByFriendUserIdList(friendUserIdList);
			this.userFriendTagManager.deleteUserFriendTagByUserIdList(friendUserIdList);
		}
	}

	@Override
	public List<UserFriend> getUserFriendByFriendUserIdList(List<Long> friendUserIdList) {
		if(friendUserIdList == null || friendUserIdList.isEmpty()) {
			return null;
		}
		return this.userFriendRepository.getUserFriendByFriendUserIdList(friendUserIdList);
	}

	@Override
	public List<UserFriend> getUserFriendByStatus(Integer status) {
		return this.userFriendRepository.getUserFriendByStatus(status);
	}

	@Override
	public List<UserFriend> getUserFriendByType(Integer type) {
		return this.userFriendRepository.getUserFriendByType(type);

	}

	@Transactional
	@Override
	public void removeUserFriend(UserFriend userFriend) {
		if(userFriend != null && userFriend.getFriendId() != null) {
			this.remove(userFriend);
			//删除标签
			this.userFriendTagManager.deleteUserFriendTagByUserId(StringUtil.nullToLong(userFriend.getFriendUserId()));
		}
	}

	@Override
	public void deleteUserFriendByUserId(Long userId) {
		this.userFriendRepository.deleteUserFriendByUserId(userId);
	}
	
}
