package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.StartImage;

public interface StartImageManager extends GenericManager<StartImage, Long>{
	
	List<StartImage> getStartImageList(Integer status);

	void updateStartImage(Integer isDefault);

	List<StartImage> getBeanListByUpdateTime(Date updateTime);
	
	StartImage getBeanListByWidthHeightAndPhoneTypeAndTemplateId(Integer width,Integer height,Integer  phoneType,Long templateId);
	
	public void updateStartImageRepeat(String imagePath,Long id);
	
	void updateStartImageByPhoneType(Integer isDefault,Integer phoneType);
	
	List<StartImage> getStartImageListByPhoneType(Integer phoneType);

	public List<StartImage> getStartImageListByTemplateId(Long templateId);
}
