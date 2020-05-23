package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.FxChildren;

public interface FxChildrenManager extends GenericManager<FxChildren, Long>{

	List<FxChildren> getFxChildrenListByPageId(Long pageId);

	void saveNewChildList(List<FxChildren> childrens , Long pageId);

	List<FxChildren> getFxChildrenListByPageIdList(List<Long> pageIdList);

	List<Object[]>  getEffectiveFxChildrenList();	
}
