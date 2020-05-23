package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.SignImageText;
import com.chunruo.core.repository.SignImageTextRepository;
import com.chunruo.core.service.SignImageTextManager;

@Transactional
@Component("signImageTextManager")
public class SignImageTextManagerImpl extends GenericManagerImpl<SignImageText, Long> implements SignImageTextManager{
	private SignImageTextRepository signImageTextRepository;
	
	@Autowired
	public SignImageTextManagerImpl(SignImageTextRepository signImageTextRepository) {
		super(signImageTextRepository);
        this.signImageTextRepository=signImageTextRepository;
	}

}
