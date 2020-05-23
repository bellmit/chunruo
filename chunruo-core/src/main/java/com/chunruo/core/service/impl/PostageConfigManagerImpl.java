package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.PostageConfig;
import com.chunruo.core.repository.PostageConfigRepository;
import com.chunruo.core.service.PostageConfigManager;

@Component("postageConfigManager")
public class PostageConfigManagerImpl extends GenericManagerImpl<PostageConfig, Long> implements PostageConfigManager{
	private PostageConfigRepository postageConfigRepository;

	@Autowired
	public PostageConfigManagerImpl(PostageConfigRepository postageConfigRepository) {
		super(postageConfigRepository);
		this.postageConfigRepository = postageConfigRepository;
	}

	@Override
	public List<PostageConfig> getPostageConfigListByUpdateTime(Date updateTime) {
		return this.postageConfigRepository.getPostageConfigListByUpdateTime(updateTime);
	}

	
}
