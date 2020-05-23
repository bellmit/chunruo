package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.RechargeTemplate;
import com.chunruo.core.repository.RechargeTemplateRepository;
import com.chunruo.core.service.CouponManager;
import com.chunruo.core.service.RechargeTemplateManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Component("rechargeTemplateManager")
public class RechargeTemplateManagerImpl extends GenericManagerImpl<RechargeTemplate, Long> implements RechargeTemplateManager{
	private RechargeTemplateRepository rechargeTemplateRepository;

	@Autowired
	private CouponManager couponManager;
	@Autowired
	public RechargeTemplateManagerImpl(RechargeTemplateRepository rechargeTemplateRepository) {
		super(rechargeTemplateRepository);
		this.rechargeTemplateRepository = rechargeTemplateRepository;
	}

	@Override
	public List<RechargeTemplate> getRechargeTemplateListByIsEnable(Boolean isEnable) {
		return this.rechargeTemplateRepository.getRechargeTemplateListByIsEnable(isEnable);
	}

	@Override
	public List<RechargeTemplate> getRechargeTemplateListByUpdateTime(Date updateTime) {
		return this.rechargeTemplateRepository.getRechargeTemplateListByUpdateTime(updateTime);
	}

	@Override
	public RechargeTemplate saveRechargeTemplate(RechargeTemplate rechargeTemplate) {
		rechargeTemplate = this.save(rechargeTemplate);
		if(StringUtil.nullToInteger(rechargeTemplate.getType()).compareTo(RechargeTemplate.RECHARGE_TEMPLATE_TYPE_PRODUCT) == 0) {
			//赠品类型为商品时，默认创建一张优惠券
			Coupon coupon = this.couponManager.getCouponByRechargeTemplateId(StringUtil.nullToLong(rechargeTemplate.getTemplateId()),StringUtil.null2Str(rechargeTemplate.getProductId()));
			if(coupon == null || coupon.getCouponId() == null) {
				coupon = new Coupon();
				coupon.setCreateTime(new Date());
			}
			String beginTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.getCurrentDate());
			String endTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.getDateAfterByDay(DateUtil.getCurrentDate(), 1095));

			coupon.setRechargeTemplateId(StringUtil.nullToLong(rechargeTemplate.getTemplateId()));
			coupon.setUseRangeType(Coupon.USER_RANGE_TYPE_PRODUCT);
			coupon.setReceiveBeginTime(beginTime);
			coupon.setReceiveEndTime(endTime);
			coupon.setEffectiveTime(90);
			coupon.setAttributeContent(StringUtil.null2Str(rechargeTemplate.getProductId()));
			coupon.setCouponName(String.format("充值满%s元赠送", rechargeTemplate.getAmount()));
			coupon.setCouponType(Coupon.COUPON_TYPE_RECHARGE);
			coupon.setAttribute(Coupon.COUPON_ATTRIBUTE_PRODUCT);
			coupon.setIsEnable(true);
			coupon.setIsRechargeProductCoupon(true);
			coupon.setUsedCount(0);
			coupon.setIsGiftCoupon(false);
			coupon.setUpdateTime(new Date());
			this.couponManager.save(coupon);
		}
		return rechargeTemplate;
	}

	
}
