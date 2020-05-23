package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.HomePopup;
import com.chunruo.core.repository.HomePopupRepository;
import com.chunruo.core.service.HomePopupManager;

@Transactional
@Component("homePopupManager")
public class HomePopupManagerImpl extends GenericManagerImpl<HomePopup, Long> implements HomePopupManager{
	private HomePopupRepository homePopupRepository;

	@Autowired
	public HomePopupManagerImpl(HomePopupRepository homePopupRepository) {
		super(homePopupRepository);
		this.homePopupRepository = homePopupRepository;
	}

	@Override
	public List<HomePopup> getHomePopupListByIsEnable(Boolean isEnable) {
		return this.homePopupRepository.getHomePopupListByIsEnable(isEnable);
	}

	@Override
	public List<HomePopup> getHomePopupListByUpdateTime(Date updateTime) {
		return this.homePopupRepository.getHomePopupListByUpdateTime(updateTime);
	}
}
