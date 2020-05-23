package com.chunruo.core.service;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Coupon;

public interface CouponManager extends GenericManager<Coupon, Long> {

	public void setCouponTimeout(Coupon coupon);

	public Coupon getCouponByRechargeTemplateId(Long rechargeTemplateId,String attributeContent);
}
