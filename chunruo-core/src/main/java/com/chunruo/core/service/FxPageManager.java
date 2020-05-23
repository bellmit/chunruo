package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.FxPage;

public interface FxPageManager extends GenericManager<FxPage, Long>{
	
	List<FxPage> getFxPageListByChannelId(Long channelId);
	
    List<FxPage> getFxPageListByChannelId(Long channelId, Integer category);

	String getImages(Long pageId);
	
	public void deletePageByPageIdList(List<Long> pageIdList);
	
	public FxPage saveFxPage(FxPage fxPage);
	
	List<FxPage> getFxPageListByChannelIdList(List<Long> channelIdList,Integer category);

	public List<FxPage> getInnerFxPageList();

}
