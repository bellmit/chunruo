package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.OrderEvaluate;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.vo.MsgModel;

public interface OrderEvaluateManager extends GenericManager<OrderEvaluate, Long>{
	
	public boolean isExistEvaluateByItemId(Long itemId);
	
	public OrderEvaluate saveOrderEvaluate(OrderEvaluate orderEvaluate);
	
	public List<OrderEvaluate> getOrderEvaluateListByUserId(Long userId, int limit);
	
	public List<OrderEvaluate> getOrderEvaluateListByProductId(Long productId);

	public List<OrderEvaluate> getOrderEvaluateListByUpdateTime(Date updateTime);

	public MsgModel<List<UserInfo>> updateEvaluateStatusByIdList(List<OrderEvaluate> orderEvaluateList, Integer status);
}
