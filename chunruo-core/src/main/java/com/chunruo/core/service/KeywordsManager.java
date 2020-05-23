package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Keywords;

public interface KeywordsManager extends GenericManager<Keywords, Long>{

	public Keywords getKeywordsByIsDefault(Boolean isDefault);
	
	public void updateKeywordsDefault(boolean isDefault, Long keywordsId);
}
