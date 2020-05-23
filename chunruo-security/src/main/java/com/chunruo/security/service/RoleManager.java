package com.chunruo.security.service;

import com.chunruo.core.base.GenericManager;
import com.chunruo.security.model.Role;

public interface RoleManager extends GenericManager<Role, Long>{
	
	boolean isExistName(String name, Long roleId);
}
