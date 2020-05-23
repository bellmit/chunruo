package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.StartImageTemplate;
import com.chunruo.core.repository.StartImageTemplateRepository;
import com.chunruo.core.service.StartImageTemplateManager;

@Transactional
@Component("startImageTemplateManager")
public class StartImageTemplateManagerImpl extends GenericManagerImpl<StartImageTemplate, Long> implements StartImageTemplateManager{
	private StartImageTemplateRepository startImageTemplateRepository;

	@Autowired
	public StartImageTemplateManagerImpl(StartImageTemplateRepository startImageTemplateRepository) {
		super(startImageTemplateRepository);
		this.startImageTemplateRepository = startImageTemplateRepository;
	}

	@Override
	public List<StartImageTemplate> getStartImageTemplateListByStatus(Boolean status) {
		return this.startImageTemplateRepository.getStartImageTemplateListByStatus(status);
	}

	@Override
	public List<StartImageTemplate> getStartImageTemplateListByUpdateTime(Date updateTime) {
		return this.startImageTemplateRepository.getStartImageTemplateListByUpdateTime(updateTime);
	}

	@Override
	public List<StartImageTemplate> getStartImageTemplateListByIsDelete(Boolean isDelete) {
		return this.startImageTemplateRepository.getStartImageTemplateListByIsDelete(isDelete);
	}
}
