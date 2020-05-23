package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserAdviserTag;
import com.chunruo.core.model.UserFriend;
import com.chunruo.core.model.UserFriendTag;
import com.chunruo.core.repository.UserFriendTagRepository;
import com.chunruo.core.service.UserAdviserTagManager;
import com.chunruo.core.service.UserFriendManager;
import com.chunruo.core.service.UserFriendTagManager;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Component("userFriendTagManager")
public class UserFriendTagManagerImpl extends GenericManagerImpl<UserFriendTag, Long> implements UserFriendTagManager{
	private UserFriendTagRepository userFriendTagRepository;
	
	@Autowired
	private UserFriendManager userFriendManager;
	@Autowired
	private UserAdviserTagManager uerAdviserTagManager;

	@Autowired
	public UserFriendTagManagerImpl(UserFriendTagRepository userFriendTagRepository) {
		super(userFriendTagRepository);
		this.userFriendTagRepository = userFriendTagRepository;
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor = Exception.class)
	@Override
	public MsgModel<List<Long>> updateUserFriendTagByTagId(Long tagId, String userIds,Long userId) throws Exception {
		MsgModel<List<Long>> msgModel = new MsgModel<List<Long>>();
		
		UserAdviserTag userAdviserTag = this.uerAdviserTagManager.get(StringUtil.nullToLong(tagId));
		if(userAdviserTag == null || userAdviserTag.getTagId() == null
				|| !StringUtil.nullToBoolean(userAdviserTag.getIsEnable())) {
			msgModel.setIsSucc(false);
			msgModel.setMessage("该标签不存在或已被禁用");
			return msgModel;
		}
		
		Map<Long,UserFriend> userFriendMap = new HashMap<Long,UserFriend>();
		List<UserFriend> userFriendList = this.userFriendManager.getUserFriendListByUserId(userId);
		if(userFriendList != null && !userFriendList.isEmpty()) {
			for(UserFriend userFriend : userFriendList) {
				userFriendMap.put(StringUtil.nullToLong(userFriend.getFriendUserId()), userFriend);
			}
		}
		
		List<Long> userIdList = StringUtil.stringToLongArray(userIds);
		if(userIdList != null && !userIdList.isEmpty()) {
			List<UserFriendTag> userFriendTagList = new ArrayList<UserFriendTag>();
			for(Long friendUserId : userIdList) {
				if(!userFriendMap.containsKey(friendUserId)) {
					msgModel.setIsSucc(false);
					msgModel.setMessage("好友关系异常！");
					return msgModel;
				}
				UserFriendTag userFriendTag = new UserFriendTag();	
				userFriendTag.setImUserId(StringUtil.nullToLong(userId));
				userFriendTag.setTagId(StringUtil.nullToLong(tagId));
				userFriendTag.setUserId(StringUtil.nullToLong(friendUserId));
				userFriendTag.setCreateTime(DateUtil.getCurrentDate());
				userFriendTag.setUpdateTime(userFriendTag.getCreateTime());
				userFriendTagList.add(userFriendTag);
			}
			
			//删除旧数据
			this.userFriendTagRepository.deleteUserFriendTagByTagIdAndImUserId(tagId,userId);
			//新建
			this.batchInsert(userFriendTagList, userFriendTagList.size());
		}
		msgModel.setIsSucc(true);
		msgModel.setData(userIdList);
		return msgModel;
		
	}

	@Override
	public List<UserFriendTag> getUserFriendTagListByTagId(Long tagId) {
		return this.userFriendTagRepository.getUserFriendTagListByTagId(tagId);
	}

	@Override
	public List<UserFriendTag> getUserFriendTagListByUpdateTime(Date updateTime) {
		return this.userFriendTagRepository.getUserFriendTagListByUpdateTime(updateTime);
	}

	@Override
	public List<UserFriendTag> getUserFriendTagListByUserId(Long userId) {
		return this.userFriendTagRepository.getUserFriendTagListByUserId(userId);
	}

	@Override
	public void deleteUserFriendTagByUserIdListAndImUserId(List<Long> userIdList, Long imUserId) {
		this.userFriendTagRepository.deleteUserFriendTagByUserIdListAndImUserId(userIdList,imUserId);
	}

	@Transactional
	@Override
	public void saveUserFriendTagList(List<UserFriendTag> userFriendTagList, Long userId) {
		this.deleteUserFriendTagByUserId(userId);
		if(userFriendTagList != null && !userFriendTagList.isEmpty()) {
			this.batchInsert(userFriendTagList, userFriendTagList.size());
		}
	}

	@Override
	public void deleteUserFriendTagByUserId(Long userId) {
		this.userFriendTagRepository.deleteUserFriendTagByUserId(userId);
	}

	@Override
	public List<UserFriendTag> getUserFriendTagListByImUserId(Long imUserId) {
		return this.userFriendTagRepository.getUserFriendTagListByImUserId(imUserId);
	}

	@Override
	public List<UserFriendTag> getUserFriendTagListByUserIdList(List<Long> userIdList) {
		return this.userFriendTagRepository.getUserFriendTagListByUserIdList(userIdList);
	}

	@Override
	public void deleteUserFriendTagByUserIdList(List<Long> userIdList) {
		this.userFriendTagRepository.deleteUserFriendTagByUserId(userIdList);
	}

	@Override
	public List<UserFriendTag> getUserFriendTagListByTagIdList(List<Long> tagIdList) {
		return this.userFriendTagRepository.getUserFriendTagListByTagIdList(tagIdList);
	}	
}
