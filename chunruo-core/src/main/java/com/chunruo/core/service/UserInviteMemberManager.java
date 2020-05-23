package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserInviteMember;

public interface UserInviteMemberManager extends GenericManager<UserInviteMember, Long> {

	public void updateUserInviteMemberUserLevelByLoadFunction();

	public UserInviteMember getUserInviteMemberByUserId(Long userId);

	public List<UserInviteMember> getUserInviteMemberListByUpdateTime(Date updateTime);

}
