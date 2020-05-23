package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.RechargeTemplate;

public interface RechargeTemplateManager extends GenericManager<RechargeTemplate, Long> {

	public List<RechargeTemplate> getRechargeTemplateListByIsEnable(Boolean isEnable);

	public List<RechargeTemplate> getRechargeTemplateListByUpdateTime(Date updateTime);

	public RechargeTemplate saveRechargeTemplate(RechargeTemplate rechargeTemplate);

}
