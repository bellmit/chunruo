package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserLevelExplain;
import com.chunruo.core.repository.UserLevelExplainRepository;
import com.chunruo.core.service.UserLevelExplainManager;

@Transactional
@Component("userLevelExplainManager")
public class UserLevelExplainManagerImpl extends GenericManagerImpl<UserLevelExplain, Long> implements UserLevelExplainManager {
	private UserLevelExplainRepository userLevelExplainRepository;
	
	@Autowired
	public UserLevelExplainManagerImpl(UserLevelExplainRepository userLevelExplainRepository) {
		super(userLevelExplainRepository);
		this.userLevelExplainRepository = userLevelExplainRepository;
	}
	
	@Override
	public List<UserLevelExplain> getUserLevelExplainList(Integer level, List<Integer> typeList) {
		return this.userLevelExplainRepository.getUserLevelExplainList(level, typeList);
	}

}
