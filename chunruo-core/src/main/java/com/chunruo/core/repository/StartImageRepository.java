package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.StartImage;

@Repository("startImageRepository")
public interface StartImageRepository extends GenericRepository<StartImage, Long> {
	
	@Query("from StartImage where status=:status")
	List<StartImage> getStartImageList(@Param("status") Integer status);
	
	@Modifying
	@Query("update StartImage set isDefault =:isDefault, updateTime = now()")
	public void updateStartImage(@Param("isDefault")Integer isDefault);
	
	@Query("from StartImage where updateTime >:updateTime")
	List<StartImage> getBeanListByUpdateTime(@Param("updateTime") Date updateTime);
	
	
	@Query("from StartImage where width=:width and height=:height and phoneType=:phoneType and templateId=:templateId")
	List<StartImage> getBeanListByWidthHeightAndPhoneTypeAndTemplateId(@Param("width") Integer width,@Param("height") Integer height,@Param("phoneType") Integer  phoneType,@Param("templateId") Long  templateId);

	@Modifying
	@Query("update StartImage set imagePath =:imagePath, updateTime = now() where id=:id")
	public void updateStartImageRepeat(@Param("imagePath")String imagePath,@Param("id") Long id);
	
	@Modifying
	@Query("update StartImage set isDefault =:isDefault, updateTime = now() where phoneType=:phoneType")
	public void updateStartImageByPhoneType(@Param("isDefault") Integer isDefault,@Param("phoneType") Integer phoneType);

	@Query("from StartImage where phoneType=:phoneType")
	List<StartImage> getStartImageListByPhoneType(@Param("phoneType") Integer phoneType);

	@Query("from StartImage where templateId=:templateId")
	List<StartImage> getStartImageListByTemplateId(@Param("templateId") Long templateId);
}
