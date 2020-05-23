package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserInviteMember;
import com.chunruo.core.repository.UserInviteMemberRepository;
import com.chunruo.core.service.UserInviteMemberManager;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userInviteMemberManager")
public class UserInviteMemberManagerImpl extends GenericManagerImpl<UserInviteMember, Long> implements UserInviteMemberManager{
	private UserInviteMemberRepository userInviteMemberRepository;

	@Autowired
	public UserInviteMemberManagerImpl(UserInviteMemberRepository userInviteMemberRepository) {
		super(userInviteMemberRepository);
		this.userInviteMemberRepository = userInviteMemberRepository;
	}

	@Override
	public void updateUserInviteMemberUserLevelByLoadFunction() {
		String uniqueString = StringUtil.null2Str(UUID.randomUUID().toString());
		String batchNumber = this.userInviteMemberRepository.executeSqlFunction("{?=call updateUserInviteMemberUserLevel_Fnc(?)}", new Object[]{uniqueString});
		log.debug("updateUserInviteMemberUserLevelByLoadFunction=======>>> " + StringUtil.null2Str(batchNumber));
	}

	@Override
	public UserInviteMember getUserInviteMemberByUserId(Long userId) {
		return this.userInviteMemberRepository.getUserInviteMemberByUserId(userId);
	}

	@Override
	public List<UserInviteMember> getUserInviteMemberListByUpdateTime(Date updateTime) {
		return this.userInviteMemberRepository.getUserInviteMemberListByUpdateTime(updateTime);
	}


}
