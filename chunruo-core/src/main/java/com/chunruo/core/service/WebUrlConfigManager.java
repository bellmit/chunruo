package com.chunruo.core.service;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.WebUrlConfig;

public interface WebUrlConfigManager extends GenericManager<WebUrlConfig, Long>{

	public WebUrlConfig getWebUrlConfigByUrl(String url);

}
