package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.repository.WeChatAppConfigRepository;
import com.chunruo.core.service.WeChatAppConfigManager;

@Transactional
@Component("weChatAppConfigManager")
public class WeChatAppConfigManagerImpl extends GenericManagerImpl<WeChatAppConfig, Long> implements WeChatAppConfigManager{
	private WeChatAppConfigRepository weChatAppConfigRepository;
	
	@Autowired
	public WeChatAppConfigManagerImpl(WeChatAppConfigRepository weChatAppConfigRepository) {
		super(weChatAppConfigRepository);
		this.weChatAppConfigRepository = weChatAppConfigRepository;
	}

}