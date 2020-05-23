package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserTeam;

public interface UserTeamManager extends GenericManager<UserTeam, Long>{

	public void updateUserTeamByLoadFunction();
	
	public List<UserTeam> getUserTeamListByUpdateTime(Date updateTime);
	
	public List<UserTeam> getUserTeamListByTopUserId(Long topUserId);
	
	public UserTeam getUserTeamByUserId(Long userId);
	
	public void changeUserTeamRecord(UserInfo userInfo);
	
	public List<UserTeam> getUserTeamListByUserIdList(List<Long> userIdList);
	
}
