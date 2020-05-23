package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.PostageTemplate;

@Repository("postageTemplateRepository")
public interface PostageTemplateRepository extends GenericRepository<PostageTemplate, Long> {

	@Query("from PostageTemplate where warehouseId =:warehouseId and isFreeTemplate =:isFreeTemplate")
	public List<PostageTemplate> getTemplateListByWarehouseId(@Param("warehouseId") Long warehouseId,@Param("isFreeTemplate") Boolean isFreeTemplate);
	
	@Query("from PostageTemplate where isFreeTemplate =:isFreeTemplate")
	public List<PostageTemplate> getTemplateListByIsFreeTemplate(@Param("isFreeTemplate") Boolean isFreeTemplate);
	
	@Query("from PostageTemplate where warehouseId =:warehouseId")
	public List<PostageTemplate> getTemplateListByWarehouseId(@Param("warehouseId") Long warehouseId);
	
	@Query("from PostageTemplate where updateTime >:updateTime")
	public List<PostageTemplate> getPostageTemplateListByUpdateTime(@Param("updateTime")Date updateTime);
	
}
