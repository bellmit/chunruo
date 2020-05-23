package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.RechargeTemplate;

@Repository("rechargeTemplateRepository")
public interface RechargeTemplateRepository extends GenericRepository<RechargeTemplate, Long> {

	@Query("from RechargeTemplate where isDelete=false and isEnable=:isEnable")
	public List<RechargeTemplate> getRechargeTemplateListByIsEnable(@Param("isEnable")Boolean isEnable);

	@Query("from RechargeTemplate where updateTime>:updateTime")
	public List<RechargeTemplate> getRechargeTemplateListByUpdateTime(@Param("updateTime")Date updateTime);
}
