package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.PostageTemplate;
import com.chunruo.core.repository.PostageTemplateRepository;
import com.chunruo.core.service.PostageTemplateManager;

@Transactional
@Component("postageTemplateManager")
public class PostageTemplateManagerImpl extends GenericManagerImpl<PostageTemplate, Long> implements PostageTemplateManager{
	private PostageTemplateRepository postageTemplateRepository;

	@Autowired
	public PostageTemplateManagerImpl(PostageTemplateRepository postageTemplateRepository) {
		super(postageTemplateRepository);
		this.postageTemplateRepository = postageTemplateRepository;
	}

	@Override
	public List<PostageTemplate> getTemplateListByWarehouseId(Long warehouseId, Boolean isFreeTemplate) {
		return this.postageTemplateRepository.getTemplateListByWarehouseId(warehouseId, isFreeTemplate);
	}

	@Override
	public List<PostageTemplate> getTemplateListByIsFreeTemplate(Boolean isFreeTemplate) {
		return  this.postageTemplateRepository.getTemplateListByIsFreeTemplate(isFreeTemplate);
	}
	
	@Override
	public List<PostageTemplate> getTemplateListByWarehouseId(Long warehouseId) {
		return this.postageTemplateRepository.getTemplateListByWarehouseId(warehouseId);
	}
	
	@Override
	public List<PostageTemplate> getPostageTemplateListByUpdateTime(Date updateTime) {
		return this.postageTemplateRepository.getPostageTemplateListByUpdateTime(updateTime);
	}

}