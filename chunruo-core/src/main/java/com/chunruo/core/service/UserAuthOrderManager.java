package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserAuthOrder;

public interface UserAuthOrderManager extends GenericManager<UserAuthOrder, Long> {

	public UserAuthOrder getUserAuthOrderByOrderNo(String orderNo);

	public UserAuthOrder updateUserAuthOrderPaymentSuccStatus(Long orderId, String tradeNo, int paymentType);

	public UserAuthOrder updateUserAuthOrderAuthSuccStatus(UserAuthOrder userAuthOrder);

	public List<UserAuthOrder> getUserAuthOrderListByIsRefund(Boolean isRefund,Date payTime);

}
