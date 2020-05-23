package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserAdviserTag;

public interface UserAdviserTagManager extends GenericManager<UserAdviserTag, Long> {
	
	public List<UserAdviserTag> getUserAdviserTagListByUpdateTime(Date updateTime);

	public List<UserAdviserTag> getUserAdviserTagListByIsEnable(boolean isEnable);

	public UserAdviserTag getUserAdviserTagByName(String name);

}
