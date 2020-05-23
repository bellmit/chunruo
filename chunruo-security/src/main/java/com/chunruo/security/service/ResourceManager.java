package com.chunruo.security.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.security.model.Resource;

public interface ResourceManager extends GenericManager<Resource, Long> {

	boolean isExistName(String name, Long resourceId);
	
	public boolean isExistLinkPath(String linkPath, Long resourceId);
	
	public void updateEnable(List<Long> resourceIdList, boolean isEnable);
}