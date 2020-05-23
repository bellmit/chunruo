package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.MemberYearsTemplate;

public interface MemberYearsTemplateManager extends GenericManager<MemberYearsTemplate, Long> {

	public List<MemberYearsTemplate> getMemberYearsTemplateListByStatus(boolean status);

	public List<MemberYearsTemplate> getMemberYearsTemplateListByUpdateTime(Date updateTime);

	public MemberYearsTemplate getMemberYearsTemplateByTemplateId(Long templateId);

	public List<MemberYearsTemplate> getMemberYearsTemplateListByYearsNumber(Double yearsNumber);

}
