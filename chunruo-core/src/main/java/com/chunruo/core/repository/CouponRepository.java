package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Coupon;

@Repository("couponRepository")
public interface CouponRepository extends GenericRepository<Coupon, Long> {

	@Query("from Coupon where rechargeTemplateId=:rechargeTemplateId and attributeContent=:attributeContent")
	public List<Coupon> getCouponByRechargeTemplateId(@Param("rechargeTemplateId")Long rechargeTemplateId,@Param("attributeContent")String attributeContent);

}