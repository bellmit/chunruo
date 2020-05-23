package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserSociety;

public interface UserSocietyManager extends GenericManager<UserSociety, Long>{

	public UserSociety getUserSocietyByOpenIdAndConfigId(Long appConfigId, String openId);
	
	public UserSociety saveUserSociety(UserSociety userSociety);

	public List<UserSociety> getUserSocietyByUnionId(String unionId);
	
	public void deleteUserSocietyByUnionId(String unionId);
	
	public void deleteUserSocietyByOpenId(List<String> openidList, List<Long> userIdList);

	public UserSociety getUserSocietyByUniodIdAndAppConfigId(String unionId, Long appConfigId);
}
