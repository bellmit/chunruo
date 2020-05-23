package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserAuthOrder;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.repository.UserAuthOrderRepository;
import com.chunruo.core.service.UserAuthOrderManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Component("userAuthOrderManager")
public class UserAuthOrderManagerImpl extends GenericManagerImpl<UserAuthOrder, Long> implements UserAuthOrderManager{
	private UserAuthOrderRepository userAuthOrderRepository;

	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	public UserAuthOrderManagerImpl(UserAuthOrderRepository userAuthOrderRepository) {
		super(userAuthOrderRepository);
		this.userAuthOrderRepository = userAuthOrderRepository;
	}

	@Override
	public UserAuthOrder getUserAuthOrderByOrderNo(String orderNo) {
		return this.userAuthOrderRepository.getUserAuthOrderByOrderNo(orderNo);
	}

	@Override
	public UserAuthOrder updateUserAuthOrderPaymentSuccStatus(Long orderId, String tradeNo, int paymentType) {
		UserAuthOrder userAuthOrder = this.get(orderId);
		if(userAuthOrder != null
				&& userAuthOrder.getOrderId() != null
				&& !StringUtil.nullToBoolean(userAuthOrder.getIsPaySucc())) {
			userAuthOrder.setIsPaySucc(true);
			userAuthOrder.setTradeNo(tradeNo);
			userAuthOrder.setPayType(paymentType);
			userAuthOrder.setPayTime(DateUtil.getCurrentDate());
			userAuthOrder.setUpdateTime(DateUtil.getCurrentDate());
			userAuthOrder = this.save(userAuthOrder);
		}
		return userAuthOrder;
	}

	@Transactional
	@Override
	public UserAuthOrder updateUserAuthOrderAuthSuccStatus(UserAuthOrder userAuthOrder) {
		userAuthOrder.setIsAuthSucc(true);
		userAuthOrder.setUpdateTime(DateUtil.getCurrentDate());
		userAuthOrder = this.save(userAuthOrder);
		
		UserInfo userInfo = this.userInfoManager.get(StringUtil.nullToLong(userAuthOrder.getUserId()));
		if(userInfo != null && userInfo.getUserId() != null) {
			userInfo.setIsAuthSucc(true);
			userInfo.setIdCardName(StringUtil.null2Str(userAuthOrder.getIdCardName()));
			userInfo.setIdCardNo(StringUtil.null2Str(userAuthOrder.getIdCardNo()));
			userInfo.setAuthTime(DateUtil.getCurrentDate());
			this.userInfoManager.save(userInfo);
		}
		return userAuthOrder;
	}

	@Override
	public List<UserAuthOrder> getUserAuthOrderListByIsRefund(Boolean isRefund, Date payTime) {
		return this.userAuthOrderRepository.getUserAuthOrderListByIsRefund(isRefund,payTime);
	}
}
