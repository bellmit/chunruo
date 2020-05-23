package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.StartImageTemplate;

public interface StartImageTemplateManager extends GenericManager<StartImageTemplate, Long>{

	public List<StartImageTemplate> getStartImageTemplateListByStatus(Boolean status);

	public List<StartImageTemplate> getStartImageTemplateListByUpdateTime(Date updateTime);

	public List<StartImageTemplate> getStartImageTemplateListByIsDelete(Boolean isDelete);
	

}
