package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.PostageTemplate;

public interface PostageTemplateManager extends GenericManager<PostageTemplate, Long>{

	public List<PostageTemplate> getTemplateListByWarehouseId(Long warehouseId, Boolean isFreeTemplate);
	
	public List<PostageTemplate> getTemplateListByIsFreeTemplate(Boolean isFreeTemplate);
	
	public List<PostageTemplate> getTemplateListByWarehouseId(Long warehouseId);
	
	public List<PostageTemplate> getPostageTemplateListByUpdateTime(Date updateTime);
}
