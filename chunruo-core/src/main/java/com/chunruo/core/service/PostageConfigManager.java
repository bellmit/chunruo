package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.PostageConfig;

public interface PostageConfigManager extends GenericManager<PostageConfig, Long> {

	
	List<PostageConfig> getPostageConfigListByUpdateTime(Date updateTime);

}
