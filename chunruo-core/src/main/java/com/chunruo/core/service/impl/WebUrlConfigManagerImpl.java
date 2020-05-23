package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.WebUrlConfig;
import com.chunruo.core.repository.WebUrlConfigRepository;
import com.chunruo.core.service.WebUrlConfigManager;

@Component("webUrlConfigManager")
public class WebUrlConfigManagerImpl extends GenericManagerImpl<WebUrlConfig, Long> implements WebUrlConfigManager{
	private WebUrlConfigRepository webUrlConfigRepository;
	
	@Autowired
	public WebUrlConfigManagerImpl(WebUrlConfigRepository webUrlConfigRepository) {
		super(webUrlConfigRepository);
		this.webUrlConfigRepository = webUrlConfigRepository;
	}

	@Override
	public WebUrlConfig getWebUrlConfigByUrl(String url) {
		return this.webUrlConfigRepository.getWebUrlConfigByUrl(url);
	}

}