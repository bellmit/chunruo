package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Area;
import com.chunruo.core.repository.AreaRepository;
import com.chunruo.core.service.AreaManager;

@Transactional
@Component("areaManager")
public class AreaManagerImpl extends GenericManagerImpl<Area, Long> implements AreaManager{
	private AreaRepository areaRepository;

	@Autowired
	public AreaManagerImpl(AreaRepository areaRepository) {
		super(areaRepository);
		this.areaRepository = areaRepository;
	}

	@Override
	public Area getAreaByNameUnique(String areaName) {
		List<Area> areaList = this.areaRepository.getAreaListByAreaName(areaName);
		return (areaList != null && areaList.size() > 0) ? areaList.get(0) : null;
	}
	
	@Override
	public List<Area> getAreaListByAreaIdList(List<Long> areaIdList){
		if(areaIdList == null || areaIdList.size() <= 0){
			return null;
		}
		return this.areaRepository.getAreaListByAreaIdList(areaIdList);
	}

	@Override
	public List<Area> getAreaListByParentId(Long parentId) {
		return this.areaRepository.getAreaListByParentId(parentId);
	}

	@Override
	public List<Area> getAreaListByIsDisUse(Boolean isDisUse) {
		return this.areaRepository.getAreaListByIsDisUse(isDisUse);
	}

	
}
