package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.TagModel;
import com.chunruo.core.repository.TagModelRepository;
import com.chunruo.core.service.TagModelManager;

@Transactional
@Component("tagModelManager")
public class TagModelManagerImpl extends GenericManagerImpl<TagModel, Long> implements TagModelManager{
	private TagModelRepository tagModelRepository;

	@Autowired
	public TagModelManagerImpl(TagModelRepository tagModelRepository) {
		super(tagModelRepository);
		this.tagModelRepository = tagModelRepository;
	}

	@Override
	public boolean isExistName(String name) {
		List<TagModel> list = tagModelRepository.getTagModelListByName(name);
		if(list != null && list.size() > 0){
			return true;
		}
		return false;
	}

	@Override
	public List<TagModel> getTagModelListByObjectId(Long objectId, Integer tagType) {
		return this.tagModelRepository.getTagModelListByObjectId(objectId, tagType);
	}

	@Override
	public List<TagModel> getTagModelListByUpdateTime(Date updateTime) {
		return this.tagModelRepository.getTagModelListByUpdateTime(updateTime);
	}

	@Override
	public List<TagModel> getTagModelListByTagType(Integer tagType) {
		return this.tagModelRepository.getTagModelListByTagType(tagType);
	}

	@Override
	public void updateTagModelisHotWord(List<Long> tagIdList, Boolean isHotWord) {
		this.tagModelRepository.updateTagModelisHotWord(tagIdList,isHotWord);
	}
}
