package com.chunruo.core.service;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.CouponTask;

public interface CouponTaskManager extends GenericManager<CouponTask, Long>{

	public void updateCouponTaskByBindCoupon(Long couponTaskId,Coupon coupon);
}
