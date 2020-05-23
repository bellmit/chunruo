package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.NoviceGuide;

public interface NoviceGuideManager extends GenericManager<NoviceGuide, Long>{

	public NoviceGuide getNoviceGuideByPhoneTypeAndHeightWidth(Integer phoneType, Integer height, Integer width);

	public List<NoviceGuide> getNoviceGuideListByStatus(Boolean status);

	public List<NoviceGuide> getNoviceGuideListByUpdateTime(Date updateTime);

}
