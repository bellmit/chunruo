package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.StartImage;
import com.chunruo.core.repository.StartImageRepository;
import com.chunruo.core.service.StartImageManager;

@Transactional
@Component("startImageManager")
public class StartImageManagerImpl extends GenericManagerImpl<StartImage, Long> implements StartImageManager{
	private StartImageRepository startImageRepository;

	@Autowired
	public StartImageManagerImpl(StartImageRepository startImageRepository) {
		super(startImageRepository);
		this.startImageRepository = startImageRepository;
	}
	
	@Override
	public List<StartImage> getStartImageList(Integer status){
		List<StartImage> startImageList = startImageRepository.getStartImageList(status);
		return startImageList;
	}
	
	@Override
	public void updateStartImage(Integer isDefault){
		startImageRepository.updateStartImage(isDefault);
	}

	@Override
	public List<StartImage> getBeanListByUpdateTime(Date updateTime) {
		return startImageRepository.getBeanListByUpdateTime(updateTime);
	}

	@Override
	public StartImage getBeanListByWidthHeightAndPhoneTypeAndTemplateId(Integer width, Integer height, Integer phoneType,Long templateId) {
		List<StartImage> list = this.startImageRepository.getBeanListByWidthHeightAndPhoneTypeAndTemplateId(width, height, phoneType,templateId);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
		
	}

	@Override
	public void updateStartImageRepeat(String imagePath,Long id) {
		startImageRepository.updateStartImageRepeat(imagePath,id);
	}

	@Override
	public void updateStartImageByPhoneType(Integer isDefault, Integer phoneType) {
		startImageRepository.updateStartImageByPhoneType(isDefault,phoneType);	
	}

	@Override
	public List<StartImage> getStartImageListByPhoneType(Integer phoneType) {
		List<StartImage> startImageList = startImageRepository.getStartImageListByPhoneType(phoneType);
		return startImageList;
	}

	@Override
	public List<StartImage> getStartImageListByTemplateId(Long templateId) {
		return this.startImageRepository.getStartImageListByTemplateId(templateId);
	}

	
}
