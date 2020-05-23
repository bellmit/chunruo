package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserSaleStandard;
import com.chunruo.core.repository.UserSaleStandardRepository;
import com.chunruo.core.service.UserSaleStandardManager;

@Transactional
@Component("userSaleStandardManager")
public class UserSaleStandardManagerImpl extends GenericManagerImpl<UserSaleStandard, Long> implements UserSaleStandardManager{
	private UserSaleStandardRepository userSaleStandardRepository;
	
	@Autowired
	public UserSaleStandardManagerImpl(UserSaleStandardRepository userSaleStandardRepository) {
		super(userSaleStandardRepository);
		this.userSaleStandardRepository = userSaleStandardRepository;
	}

}