package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants;
import com.chunruo.core.Constants.UserCouponStatus;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.CouponTask;
import com.chunruo.core.model.OrderEvaluate;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.UserCoupon;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.repository.OrderEvaluateRepository;
import com.chunruo.core.service.OrderEvaluateManager;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.service.UserCouponManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("orderEvaluateManager")
public class OrderEvaluateManagerImpl extends GenericManagerImpl<OrderEvaluate, Long> implements OrderEvaluateManager{
	private OrderEvaluateRepository orderEvaluateRepository;
	@Autowired
	private OrderItemsManager orderItemsManager;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private UserCouponManager userCouponManager;
	

	@Autowired
	public OrderEvaluateManagerImpl(OrderEvaluateRepository orderEvaluateRepository) {
		super(orderEvaluateRepository);
		this.orderEvaluateRepository = orderEvaluateRepository;
	}

	@Override
	public boolean isExistEvaluateByItemId(Long itemId) {
		OrderEvaluate orderEvaluate = this.orderEvaluateRepository.getOrderEvaluateByItemId(itemId);
		return (orderEvaluate != null && orderEvaluate.getEvaluateId() != null) ? true : false;
	}
	
	@Override
	public List<OrderEvaluate> getOrderEvaluateListByProductId(Long productId){
		return this.orderEvaluateRepository.getOrderEvaluateListByProductId(productId);
	}
	
	@Override
	public List<OrderEvaluate> getOrderEvaluateListByUserId(Long userId, int limit){
		return this.orderEvaluateRepository.getOrderEvaluateListByUserId(userId, limit);
	}
	
	@Override
	public List<OrderEvaluate> getOrderEvaluateListByUpdateTime(Date updateTime) {
		return this.orderEvaluateRepository.getOrderEvaluateListByUpdateTime(updateTime);
	}

	@Override
	public OrderEvaluate saveOrderEvaluate(OrderEvaluate orderEvaluate) {
		orderEvaluate.setStatus(OrderEvaluate.EVALUATE_RECORD_STATUS_WAIT);
		orderEvaluate.setUpdateTime(DateUtil.getCurrentDate());
		orderEvaluate = this.save(orderEvaluate);
		
		// 更新评价商品状态
		OrderItems orderItems = this.orderItemsManager.get(orderEvaluate.getItemId());
		orderItems.setIsEvaluate(true);
		orderItems.setUpdateTime(DateUtil.getCurrentDate());
		this.orderItemsManager.save(orderItems);
		return orderEvaluate;
	}

	@Override
	public MsgModel<List<UserInfo>> updateEvaluateStatusByIdList(List<OrderEvaluate> orderEvaluateList, Integer status) {
		MsgModel<List<UserInfo>> msgModel = new MsgModel<List<UserInfo>>();
		List<Long> userIdList = new ArrayList<Long>();
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		if(orderEvaluateList != null && orderEvaluateList.size() > 0) {
			for(OrderEvaluate evaluate : orderEvaluateList ) {
				if(evaluate != null && evaluate.getEvaluateId() != null) {
					evaluate.setStatus(status);
					evaluate.setUpdateTime(DateUtil.getCurrentDate());
					userIdList.add(StringUtil.nullToLong(evaluate.getUserId()));
				}
			}
		}
		
		//被评为精选送优惠券
		if(StringUtil.compareObject(OrderEvaluate.EVALUATE_RECORD_STATUS_EXCELLENT, status)) {
			boolean isValidCoupon = true;
			//任务券
			CouponTask couponTask = Constants.COUPON_TASK_MAP.get(CouponTask.TASK_NAME_EVALUATE_EXCELLENT);
			Coupon coupon = null;
			if(couponTask == null || couponTask.getTaskId() == null
					|| couponTask.getCouponId() == null) {
				isValidCoupon = false;
			}else {
				coupon = Constants.COUPON_MAP.get(couponTask.getCouponId());
				if(coupon == null  || coupon.getCouponId() == null ) {
					isValidCoupon = false;
				}
			}
			
			if(StringUtil.nullToBoolean(isValidCoupon)) {
				//发送优惠券
				List<UserCoupon> userCouponList = new ArrayList<UserCoupon>();
				if(userIdList != null && userIdList.size() > 0) {
					String receiveTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.getCurrentDate());
					String effectiveTime = DateUtil.getNearlyDate(StringUtil.nullToInteger(coupon.getEffectiveTime()));
					for(Long userId : userIdList) {
						UserCoupon userCoupon = new UserCoupon();
						userCoupon.setCouponId(couponTask.getCouponId());
						userCoupon.setCouponStatus(UserCouponStatus.USER_COUPON_STATUS_NOT_USED);
						userCoupon.setCouponNo(DateUtil.getCurrentMillisecond() + userIdList.indexOf(userId));
						userCoupon.setReceiveTime(receiveTime);
						userCoupon.setEffectiveTime(effectiveTime);
						userCoupon.setUserId(userId);
						userCoupon.setCreateTime(DateUtil.getCurrentDate());
						userCoupon.setUpdateTime(userCoupon.getCreateTime());
						
						userCouponList.add(userCoupon);
					}
				}
				
				userInfoList = this.userInfoManager.getByIdList(userIdList);
				
				//批量保存赠送优惠券
				this.userCouponManager.batchInsertUserCoupon(userCouponList, userCouponList.size());
			}
		}
		
		//更新评价状态
		this.batchInsert(orderEvaluateList, orderEvaluateList.size());
		
		msgModel.setIsSucc(true);
		msgModel.setData(userInfoList);
		return msgModel;
	}
}
