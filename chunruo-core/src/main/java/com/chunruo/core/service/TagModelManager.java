package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.TagModel;

public interface TagModelManager extends GenericManager<TagModel, Long>{
	
	public boolean isExistName(String name);

	public List<TagModel> getTagModelListByObjectId(Long objectId, Integer tagType);
	
	public List<TagModel> getTagModelListByUpdateTime(Date updateTime);

	public List<TagModel> getTagModelListByTagType(Integer tagType);

	public void updateTagModelisHotWord(List<Long> tagIdList, Boolean isHotWord);
}
