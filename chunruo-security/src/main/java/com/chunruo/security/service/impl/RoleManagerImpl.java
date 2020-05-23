package com.chunruo.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.Role;
import com.chunruo.security.repository.RoleRepository;
import com.chunruo.security.service.RoleManager;

@Transactional
@Component("roleManager")
public class RoleManagerImpl extends GenericManagerImpl<Role, Long> implements RoleManager{
	private RoleRepository roleRepository;

	@Autowired
	public RoleManagerImpl(RoleRepository roleRepository) {
		super(roleRepository);
		this.roleRepository = roleRepository;
	}

	@Override
	public boolean isExistName(String name, Long roleId) {
		List<Role> list = this.roleRepository.getRoleByName(StringUtil.null2Str(name).toUpperCase());
		if(list != null && list.size() > 0){
			if(roleId != null){
				for(Role role : list){
					if(!StringUtil.compareObject(role.getId(), roleId)){
						return true;
					}
				}
			}else{
				return true;
			}
		}
		return false;
	}
}
