package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Sign;
import com.chunruo.core.repository.SignRepository;
import com.chunruo.core.service.SignManager;

@Transactional
@Component("signManager")
public class SignManagerImpl extends GenericManagerImpl<Sign, Long> implements SignManager{
	private SignRepository signRepository;
	
	@Autowired
	public SignManagerImpl(SignRepository signRepository) {
		super(signRepository);
		this.signRepository = signRepository;
	}

	@Override
	public Sign getSignByUserId(Long userId) {
		return this.signRepository.getSignByUserId(userId);
	}

	@Override
	public List<Sign> getSignListByUpdateTime(Date updateTime) {
		return this.signRepository.getSignListByUpdateTime(updateTime);
	}

}
