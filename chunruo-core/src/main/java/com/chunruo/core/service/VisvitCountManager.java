package com.chunruo.core.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.VisvitCount;

public interface VisvitCountManager extends GenericManager<VisvitCount, Long> {

	Set<Long> getVisvitCountUserId();

	List<VisvitCount> getVisvitCountListByUpdateTime(Date updateTime);

}
