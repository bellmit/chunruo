package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.HomePopup;

public interface HomePopupManager extends GenericManager<HomePopup, Long> {

	public List<HomePopup> getHomePopupListByIsEnable(Boolean isEnable);

	public List<HomePopup> getHomePopupListByUpdateTime(Date updateTime);
}
