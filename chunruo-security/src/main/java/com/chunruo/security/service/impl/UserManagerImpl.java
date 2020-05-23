package com.chunruo.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.security.model.User;
import com.chunruo.security.repository.UserRepository;
import com.chunruo.security.service.UserManager;

@Transactional
@Component("userManager")
public class UserManagerImpl extends GenericManagerImpl<User, Long> implements UserManager{
	private UserRepository userRepository;
	
	@Autowired
	public UserManagerImpl(UserRepository userRepository) {
		super(userRepository);
		this.userRepository = userRepository;
	}


	@Override
	public User getUserByName(String userName) {
        return this.userRepository.getUserByName(userName);
	}

	@Override
	public List<User> getUserListByGroupIdList(List<Long> groupIdList) {
		return this.userRepository.getUserListByGroupIdList(groupIdList);
	}

}
