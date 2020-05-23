package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.StartImageTemplate;

@Repository("startImageTemplateRepository")
public interface StartImageTemplateRepository extends GenericRepository<StartImageTemplate, Long> {

	@Query("from StartImageTemplate where status=:status")
	public List<StartImageTemplate> getStartImageTemplateListByStatus(@Param("status")Boolean status);

	@Query("from StartImageTemplate where updateTime >:updateTime")
	public List<StartImageTemplate> getStartImageTemplateListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from StartImageTemplate where isDelete=:isDelete")
	public List<StartImageTemplate> getStartImageTemplateListByIsDelete(@Param("isDelete")Boolean isDelete);
}
