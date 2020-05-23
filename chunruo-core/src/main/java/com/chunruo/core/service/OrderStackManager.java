package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderStack;

public interface OrderStackManager extends GenericManager<OrderStack, Long> {

	public List<OrderStack> getOrderStackListByUserId(Long userId);
	
	public List<OrderStack> getOrderStackListByGroupKey(String groupKey);
	
	public void deleteAllByOrderStackId(Long orderStackId);
}
