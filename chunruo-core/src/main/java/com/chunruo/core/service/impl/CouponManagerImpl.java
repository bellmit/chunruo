package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.UserCoupon;
import com.chunruo.core.repository.CouponRepository;
import com.chunruo.core.service.CouponManager;
import com.chunruo.core.service.UserCouponManager;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("couponManager")
public class CouponManagerImpl extends GenericManagerImpl<Coupon, Long>  implements CouponManager {
	private CouponRepository couponRepository;
	@Autowired
	private UserCouponManager userCouponManager;
	
	@Autowired
	public CouponManagerImpl(CouponRepository couponRepository) {
		super(couponRepository);
		this.couponRepository = couponRepository;
	}

	@Override
	public void setCouponTimeout(Coupon coupon) {
		if(coupon != null && coupon.getCouponId() != null) {
			coupon.setIsEnable(false);
			coupon.setUpdateTime(new Date());
			this.update(coupon);
			//直接清除缓存
			Constants.COUPON_MAP.remove(coupon.getCouponId());
			
			List<UserCoupon> userCouponList = this.userCouponManager.getUserCouponListByCouponId(coupon.getCouponId());
			if(userCouponList != null && userCouponList.size() > 0){
				List<Long> idList = new ArrayList<>();
				for(UserCoupon uc : userCouponList){
					if(StringUtil.compareObject(uc.getCouponStatus(), UserCoupon.USER_COUPON_STATUS_NOT_USED)){
						idList.add(uc.getUserCouponId());
					}
				}
				//如果禁用优惠券，则直接删除用户优惠券
				if(idList.size() > 0){
					this.userCouponManager.deleteByIdList(idList);
				}
			}
			
		}
		
	}

	@Override
	public Coupon getCouponByRechargeTemplateId(Long rechargeTemplateId,String attributeContent) {
		List<Coupon> couponList = this.couponRepository.getCouponByRechargeTemplateId(rechargeTemplateId,attributeContent);
		if(couponList != null && !couponList.isEmpty()) {
			return couponList.get(0);
		}
		return null;
	}
	

}
