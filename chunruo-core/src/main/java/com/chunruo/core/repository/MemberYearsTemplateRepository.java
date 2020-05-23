package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.MemberYearsTemplate;

@Repository("memberYearsTemplateRepository")
public interface MemberYearsTemplateRepository extends GenericRepository<MemberYearsTemplate, Long> {

	@Query("from MemberYearsTemplate where status=:status and isDelete=false")
	public List<MemberYearsTemplate> getMemberYearsTemplateListByStatus(@Param("status")Boolean status);

	@Query("from MemberYearsTemplate where updateTime>:updateTime")
	public List<MemberYearsTemplate> getMemberYearsTemplateListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from MemberYearsTemplate where templateId=:templateId and isDelete=false and status=true")
	public MemberYearsTemplate getMemberYearsTemplateByTemplateId(@Param("templateId")Long templateId);

	@Query("from MemberYearsTemplate where yearsNumber=:yearsNumber and isDelete=false and status=true")
	public List<MemberYearsTemplate> getMemberYearsTemplateListByYearsNumber(@Param("yearsNumber")Double yearsNumber);


}
