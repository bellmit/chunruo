package com.chunruo.security.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.security.model.User;

public interface UserManager extends GenericManager<User, Long> {

	public User getUserByName(String userName);
	
	public List<User> getUserListByGroupIdList(List<Long> groupIdList);

}
