package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.MemberGift;

@Repository("memberGiftRepository")
public interface MemberGiftRepository extends GenericRepository<MemberGift, Long> {

	@Query("from MemberGift where status=:status and isDelete=false")
	public List<MemberGift> getMemberGiftListByStatus(@Param("status")Boolean status);

	@Query("from MemberGift where updateTime>:updateTime")
	public List<MemberGift> getMemberGiftListByStatus(@Param("updateTime")Date updateTime);

	@Query("from MemberGift where templateId=:templateId and isDelete=false and status=true")
	public List<MemberGift> getMemberGiftListByTemplateId(@Param("templateId")Long templateId);
}
