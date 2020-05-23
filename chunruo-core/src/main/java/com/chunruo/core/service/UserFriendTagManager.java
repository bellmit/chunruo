package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserFriendTag;
import com.chunruo.core.vo.MsgModel;

public interface UserFriendTagManager extends GenericManager<UserFriendTag, Long> {

	public MsgModel<List<Long>> updateUserFriendTagByTagId(Long tagId, String userIds,Long userId) throws Exception;

	public List<UserFriendTag> getUserFriendTagListByTagId(Long tagId);

	public List<UserFriendTag> getUserFriendTagListByUpdateTime(Date updateTime);

	public List<UserFriendTag> getUserFriendTagListByUserId(Long userId);

	public void deleteUserFriendTagByUserIdListAndImUserId(List<Long> userIdList, Long userId);

	public void saveUserFriendTagList(List<UserFriendTag> userFriendTagList, Long userId);
	
	public void deleteUserFriendTagByUserId(Long userId);

	public List<UserFriendTag> getUserFriendTagListByImUserId(Long imUserId);

	public List<UserFriendTag> getUserFriendTagListByUserIdList(List<Long> userIdList);

	public void deleteUserFriendTagByUserIdList(List<Long> userIdList);

	public List<UserFriendTag> getUserFriendTagListByTagIdList(List<Long> tagIdList);
}
