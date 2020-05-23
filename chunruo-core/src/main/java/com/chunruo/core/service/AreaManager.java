package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Area;

public interface AreaManager extends GenericManager<Area, Long>{

	public Area getAreaByNameUnique(String areaName);
	
	List<Area> getAreaListByAreaIdList(List<Long> areaIdList);
	
	List<Area> getAreaListByParentId(Long  parentId);
	
	public List<Area> getAreaListByIsDisUse(Boolean isDisUse);
}
