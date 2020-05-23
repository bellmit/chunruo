package com.chunruo.security.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.security.model.Group;

public interface GroupManager extends GenericManager<Group, Long> {
	
	boolean isExistName(String name, Long groupId);
	
	void updateEnable(List<Long> groupIdList, boolean isEnable);
}