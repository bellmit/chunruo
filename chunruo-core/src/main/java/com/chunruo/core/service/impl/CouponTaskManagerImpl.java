package com.chunruo.core.service.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.CouponTask;
import com.chunruo.core.repository.CouponRepository;
import com.chunruo.core.repository.CouponTaskRepository;
import com.chunruo.core.service.CouponTaskManager;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("couponTaskManager")
public class CouponTaskManagerImpl extends GenericManagerImpl<CouponTask, Long> implements CouponTaskManager {
	private CouponTaskRepository couponTaskRepository;
	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	public CouponTaskManagerImpl(CouponTaskRepository couponTaskRepository) {
		super(couponTaskRepository);
		this.couponTaskRepository = couponTaskRepository;
	}


	@Override
	public void updateCouponTaskByBindCoupon(Long couponTaskId, Coupon coupon) {
		//任务id是否为空
	    if(!StringUtil.compareObject(couponTaskId, 0L)) {
	    	CouponTask couponTask=this.get(couponTaskId);
	    	//保存优惠券
	    	if(coupon != null) {
	    		this.couponRepository.save(coupon);
	    	}
    	    if(couponTask != null && couponTask.getTaskId() != null) {
	    		couponTask.setCouponId(coupon.getCouponId());
	    		couponTask.setUpdateTime(new Date());
    		
	    		// 直接放入优惠券任务集合中，直接生效
				Constants.COUPON_TASK_MAP.put(couponTask.getTaskId(), couponTask);
				this.update(couponTask);
    	    }
	    }
	}

	

}
