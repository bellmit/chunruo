package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserAdviserTag;
import com.chunruo.core.repository.UserAdviserTagRepository;
import com.chunruo.core.service.UserAdviserTagManager;

@Component("userAdviserTagManager")
public class UserAdviserTagManagerImpl extends GenericManagerImpl<UserAdviserTag, Long> implements UserAdviserTagManager{
	private UserAdviserTagRepository userAdviserTagRepository;

	@Autowired
	public UserAdviserTagManagerImpl(UserAdviserTagRepository userAdviserTagRepository) {
		super(userAdviserTagRepository);
		this.userAdviserTagRepository = userAdviserTagRepository;
	}

	@Override
	public List<UserAdviserTag> getUserAdviserTagListByUpdateTime(Date updateTime) {
		return this.userAdviserTagRepository.getUserAdviserTagListByUpdateTime(updateTime);
	}

	@Override
	public List<UserAdviserTag> getUserAdviserTagListByIsEnable(boolean isEnable) {
		return this.userAdviserTagRepository.getUserAdviserTagListByIsEnable(isEnable);
	}

	@Override
	public UserAdviserTag getUserAdviserTagByName(String name) {
		return this.userAdviserTagRepository.getUserAdviserTagByName(name);

	}
}
